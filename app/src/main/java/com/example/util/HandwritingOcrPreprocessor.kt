package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min

/**
 * Image preprocessing pipeline specialized for handwritten text in East African documents:
 * - Normalizes variable ballpoint and carbon-copy ink density
 * - De-blurs character contours with high-pass unsharp masking
 * - Stretches dynamic contrast to separate faint ink strokes from textured ledger/newsprint paper
 * - Encodes high-fidelity bitmap to preserve delicate cursive ascenders, crossed 't's, and dots.
 */
object HandwritingOcrPreprocessor {

    /**
     * Preprocesses an input document bitmap to maximize OCR legibility for cursive
     * handwriting, faint ballpoint strokes, and blurred character edges.
     */
    fun preprocessForHandwritingOcr(original: Bitmap): Bitmap {
        val width = original.width
        val height = original.height

        // Downscale moderately if excessively large (> 2048px) to conserve memory while preserving cursive detail
        val maxDim = 2048
        val workingBitmap = if (width > maxDim || height > maxDim) {
            val ratio = width.toFloat() / height.toFloat()
            val newW = if (width > height) maxDim else (maxDim * ratio).toInt()
            val newH = if (width > height) (maxDim / ratio).toInt() else maxDim
            Bitmap.createScaledBitmap(original, newW, newH, true)
        } else {
            original
        }

        val w = workingBitmap.width
        val h = workingBitmap.height
        val pixels = IntArray(w * h)
        workingBitmap.getPixels(pixels, 0, w, 0, 0, w, h)

        // Step 1: Calculate luminance histogram to determine paper background and ink floor
        var minLum = 255
        var maxLum = 0
        // Sample every 4th pixel for speed
        val step = 4
        for (i in 0 until pixels.size step step) {
            val p = pixels[i]
            val r = (p shr 16) and 0xFF
            val g = (p shr 8) and 0xFF
            val b = p and 0xFF
            val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()
            if (lum < minLum) minLum = lum
            if (lum > maxLum) maxLum = lum
        }

        if (maxLum <= minLum) {
            return workingBitmap
        }

        // Clamp bounds for robust contrast expansion
        val lowBound = max(0, minLum + 10)
        val highBound = min(255, maxLum - 5)
        val lumRange = max(1, highBound - lowBound)

        val enhancedPixels = IntArray(w * h)

        // Step 2: Adaptive contrast expansion & ink density normalization
        for (i in pixels.indices) {
            val p = pixels[i]
            val a = (p shr 24) and 0xFF
            var r = (p shr 16) and 0xFF
            var g = (p shr 8) and 0xFF
            var b = p and 0xFF

            // Calculate luminance
            val lum = (0.299 * r + 0.587 * g + 0.114 * b).toInt()

            // Ink density boost: Amplify faint ballpoint and carbon copy strokes
            val normalizedLum = when {
                lum <= lowBound -> (lum * 0.75).toInt() // Deepen dark ink strokes
                lum >= highBound -> 255 // Pure clean paper background
                else -> {
                    // Non-linear gamma curve that darkens mid-tone ink while keeping paper bright
                    val ratio = (lum - lowBound).toFloat() / lumRange
                    val curved = Math.pow(ratio.toDouble(), 1.35)
                    (curved * 255).toInt().coerceIn(0, 255)
                }
            }

            val factor = if (lum > 0) normalizedLum.toFloat() / lum.toFloat() else 1f
            val newR = (r * factor).toInt().coerceIn(0, 255)
            val newG = (g * factor).toInt().coerceIn(0, 255)
            val newB = (b * factor).toInt().coerceIn(0, 255)

            enhancedPixels[i] = (a shl 24) or (newR shl 16) or (newG shl 8) or newB
        }

        // Step 3: Fast 3x3 unsharp mask / edge sharpening for slightly blurred characters & cursive loops
        val finalPixels = IntArray(w * h)
        // Sharpening kernel: Center = 5f, Cardinals = -0.75f, Corners = -0.25f (Sum = 1)
        for (y in 0 until h) {
            val yOffset = y * w
            val yPrev = max(0, y - 1) * w
            val yNext = min(h - 1, y + 1) * w

            for (x in 0 until w) {
                val xPrev = max(0, x - 1)
                val xNext = min(w - 1, x + 1)

                val center = enhancedPixels[yOffset + x]
                val cR = (center shr 16) and 0xFF
                val cG = (center shr 8) and 0xFF
                val cB = center and 0xFF

                val top = enhancedPixels[yPrev + x]
                val bottom = enhancedPixels[yNext + x]
                val left = enhancedPixels[yOffset + xPrev]
                val right = enhancedPixels[yOffset + xNext]

                val nR = (((top shr 16) and 0xFF) + ((bottom shr 16) and 0xFF) +
                         ((left shr 16) and 0xFF) + ((right shr 16) and 0xFF)) / 4
                val nG = (((top shr 8) and 0xFF) + ((bottom shr 8) and 0xFF) +
                         ((left shr 8) and 0xFF) + ((right shr 8) and 0xFF)) / 4
                val nB = ((top and 0xFF) + (bottom and 0xFF) +
                         (left and 0xFF) + (right and 0xFF)) / 4

                // Unsharp formula: Center + amount * (Center - Neighbors)
                val amount = 0.45f
                val sR = (cR + amount * (cR - nR)).toInt().coerceIn(0, 255)
                val sG = (cG + amount * (cG - nG)).toInt().coerceIn(0, 255)
                val sB = (cB + amount * (cB - nB)).toInt().coerceIn(0, 255)

                finalPixels[yOffset + x] = ((center shr 24) and 0xFF shl 24) or (sR shl 16) or (sG shl 8) or sB
            }
        }

        val output = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        output.setPixels(finalPixels, 0, w, 0, 0, w, h)
        return output
    }

    /**
     * Converts a bitmap to a high-fidelity Base64 string for Gemini OCR,
     * maintaining high resolution (up to 2048px) and 92% JPEG quality to ensure
     * fine cursive details, dots, and currency symbols are preserved.
     */
    fun encodeBitmapToBase64(bitmap: Bitmap, applyPreprocessing: Boolean = true): String {
        val processed = if (applyPreprocessing) preprocessForHandwritingOcr(bitmap) else bitmap
        val outputStream = ByteArrayOutputStream()
        processed.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)
        val bytes = outputStream.toByteArray()
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
