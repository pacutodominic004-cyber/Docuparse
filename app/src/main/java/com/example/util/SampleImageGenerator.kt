package com.example.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface

object SampleImageGenerator {

    fun generatePresetBitmap(presetId: String): Bitmap {
        return when (presetId) {
            "sacco_wazalendo" -> generateSaccoBitmap()
            "sacco_kigezi_ledger" -> generateKigeziLedgerBitmap()
            "school_cat_exam" -> generateSchoolExamBitmap()
            "fleet_fuel_total" -> generateFuelSlipBitmap()
            "ura_efris_invoice" -> generateEfrisInvoiceBitmap()
            else -> generateSaccoBitmap()
        }
    }

    private fun generateSaccoBitmap(): Bitmap {
        val width = 1000
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Paper background (vintage ledger tone)
        canvas.drawColor(Color.rgb(250, 246, 238))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.rgb(40, 80, 50)
        }

        // Outer ledger border
        canvas.drawRect(30f, 30f, width - 30f, height - 30f, borderPaint)
        canvas.drawRect(36f, 36f, width - 36f, height - 36f, borderPaint.apply { strokeWidth = 1f })

        // Header band
        paint.color = Color.rgb(20, 90, 50)
        paint.style = Paint.Style.FILL
        canvas.drawRect(40f, 40f, width - 40f, 150f, paint)

        // Header text
        paint.color = Color.WHITE
        paint.textSize = 34f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("WAZALENDO SACCO SOCIETY LTD", width / 2f, 92f, paint)

        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("SAVINGS PASSBOOK & DEPOSIT COUNTERFOIL • UGANDA", width / 2f, 130f, paint)

        // Metadata grid
        paint.color = Color.rgb(40, 40, 40)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val startY = 190f
        canvas.drawText("RECEIPT NO: REC-2026-UG-88219", 60f, startY, paint)
        canvas.drawText("DATE: 14-AUG-2026", 620f, startY, paint)

        canvas.drawText("MEMBER ID: WZ-94021", 60f, startY + 45f, paint)
        canvas.drawText("BRANCH: KAMPALA MAIN", 620f, startY + 45f, paint)

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 22f
        canvas.drawText("MEMBER NAME:", 60f, startY + 90f, paint)
        drawCursiveHandwriting(canvas, "Francis Okello", 240f, startY + 90f, color = Color.rgb(20, 45, 115), textSize = 25f, varyingInkDensity = true)
        canvas.drawText("TELLER ID: TEL-KLA-04", 620f, startY + 90f, paint)

        // Key Value breakdown box
        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.rgb(180, 180, 180)
        }
        canvas.drawRoundRect(RectF(60f, startY + 120f, width - 60f, startY + 420f), 12f, 12f, boxPaint)

        // Rows inside box
        var rowY = startY + 165f
        drawKeyValueRow(canvas, "Share Capital Contribution:", "UGX 350,000", 80f, width - 80f, rowY, paint)
        rowY += 45f
        drawKeyValueRow(canvas, "Monthly Savings Deposit:", "UGX 200,000", 80f, width - 80f, rowY, paint)
        rowY += 45f
        drawKeyValueRow(canvas, "Loan Principal Repayment:", "UGX 120,000", 80f, width - 80f, rowY, paint)
        rowY += 45f
        drawKeyValueRow(canvas, "Loan Interest Repayment:", "UGX 18,000", 80f, width - 80f, rowY, paint)
        rowY += 50f

        // Subtotal separator
        paint.strokeWidth = 2f
        paint.color = Color.rgb(100, 100, 100)
        canvas.drawLine(80f, rowY - 15f, width - 80f, rowY - 15f, paint)

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 26f
        paint.color = Color.rgb(20, 90, 50)
        drawKeyValueRow(canvas, "TOTAL DEPOSIT PAID:", "UGX 688,000", 80f, width - 80f, rowY + 20f, paint)

        // Historical Ledger Table
        val tableTop = startY + 460f
        paint.color = Color.rgb(30, 30, 30)
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("MEMBER RECENT TRANSACTION REGISTER", 60f, tableTop - 15f, paint)

        val tablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.rgb(100, 100, 100)
        }

        // Table Header
        paint.color = Color.rgb(225, 235, 225)
        paint.style = Paint.Style.FILL
        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 50f, paint)

        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 260f, tablePaint)
        // Vertical dividers
        canvas.drawLine(220f, tableTop, 220f, tableTop + 260f, tablePaint)
        canvas.drawLine(440f, tableTop, 440f, tableTop + 260f, tablePaint)
        canvas.drawLine(660f, tableTop, 660f, tableTop + 260f, tablePaint)
        canvas.drawLine(840f, tableTop, 840f, tableTop + 260f, tablePaint)

        // Header titles
        paint.color = Color.rgb(20, 50, 25)
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Period", 80f, tableTop + 33f, paint)
        canvas.drawText("Savings", 240f, tableTop + 33f, paint)
        canvas.drawText("Loan Princ.", 460f, tableTop + 33f, paint)
        canvas.drawText("Cum. Balance", 680f, tableTop + 33f, paint)
        canvas.drawText("Sign", 860f, tableTop + 33f, paint)

        // Table Rows
        val tableRows = listOf(
            listOf("May 2026", "UGX 200,000", "UGX 120,000", "UGX 3,800,000", "M.B."),
            listOf("Jun 2026", "UGX 200,000", "UGX 120,000", "UGX 4,000,000", "M.B."),
            listOf("Jul 2026", "UGX 200,000", "UGX 120,000", "UGX 4,200,000", "M.B.")
        )

        paint.typeface = Typeface.DEFAULT
        paint.textSize = 20f
        paint.color = Color.rgb(40, 40, 40)
        var rowBaseY = tableTop + 50f
        for (row in tableRows) {
            canvas.drawLine(60f, rowBaseY, width - 60f, rowBaseY, tablePaint)
            canvas.drawText(row[0], 80f, rowBaseY + 45f, paint)
            canvas.drawText(row[1], 240f, rowBaseY + 45f, paint)
            canvas.drawText(row[2], 460f, rowBaseY + 45f, paint)
            canvas.drawText(row[3], 680f, rowBaseY + 45f, paint)
            // Handwritten teller initials
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            canvas.drawText(row[4], 870f, rowBaseY + 45f, paint)
            paint.typeface = Typeface.DEFAULT
            rowBaseY += 70f
        }

        // Circular Stamp Mark (purple rubber stamp)
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.argb(180, 120, 20, 160)
        }
        canvas.drawCircle(780f, tableTop + 380f, 90f, stampPaint)
        canvas.drawCircle(780f, tableTop + 380f, 82f, stampPaint.apply { strokeWidth = 1.5f })

        val stampTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 120, 20, 160)
            textSize = 18f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("WAZALENDO SACCO", 780f, tableTop + 355f, stampTextPaint)
        canvas.drawText("• VERIFIED •", 780f, tableTop + 380f, stampTextPaint)
        canvas.drawText("14 AUG 2026", 780f, tableTop + 405f, stampTextPaint)

        // Bottom Footer Notes & Anomaly hint
        paint.color = Color.rgb(100, 100, 100)
        paint.textSize = 18f
        canvas.drawText("Passbook verified by teller. Slanted signature noted.", 60f, height - 70f, paint)

        return bitmap
    }

    private fun generateSchoolExamBitmap(): Bitmap {
        val width = 1000
        val height = 1400
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Paper background (crisp exam sheet)
        canvas.drawColor(Color.rgb(253, 253, 253))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Header Title
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 36f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("ALLIANCE HIGH SCHOOL - KIKUYU", width / 2f, 90f, paint)

        paint.textSize = 22f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(51, 65, 85)
        canvas.drawText("CONTINUOUS ASSESSMENT TEST (CAT 2) • TERM II 2026", width / 2f, 130f, paint)
        canvas.drawText("DEPARTMENT OF MATHEMATICS • FORM 3 WEST", width / 2f, 160f, paint)

        // Separator line
        paint.strokeWidth = 2f
        paint.color = Color.rgb(203, 213, 225)
        canvas.drawLine(50f, 185f, width - 50f, 185f, paint)

        // Student Info box
        paint.color = Color.rgb(30, 41, 59)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("STUDENT NAME:", 60f, 230f, paint)
        drawCursiveHandwriting(canvas, "Kevin Mwangi Otieno", 250f, 230f, color = Color.rgb(30, 45, 115), textSize = 23f, varyingInkDensity = true)
        canvas.drawText("ADM NO: ADM-2024-819", 600f, 230f, paint)
        canvas.drawText("SUBJECT: Mathematics Paper 1", 60f, 275f, paint)
        canvas.drawText("DATE: 22-JUL-2026", 600f, 275f, paint)

        // Red Pen Grading Box
        val redBoxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.rgb(220, 38, 38)
        }
        canvas.drawRoundRect(RectF(60f, 310f, width - 60f, 430f), 12f, 12f, redBoxPaint)

        val redTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(185, 28, 28)
            textSize = 26f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("SCORE:", 85f, 365f, redTextPaint)
        drawCursiveHandwriting(canvas, "86 / 100", 185f, 365f, color = Color.rgb(185, 28, 28), textSize = 28f, varyingInkDensity = true)
        canvas.drawText("GRADE:", 420f, 365f, redTextPaint)
        drawCursiveHandwriting(canvas, "A (Plain)", 520f, 365f, color = Color.rgb(185, 28, 28), textSize = 28f, varyingInkDensity = true)
        canvas.drawText("CLASS RANK: 4 of 48", 700f, 365f, redTextPaint)

        val remarkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(185, 28, 28)
            textSize = 20f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }
        canvas.drawText("Examiner Remarks: Outstanding grasp of calculus principles and trigonometry.", 85f, 405f, remarkPaint)

        // Question Marks Grid
        val tableTop = 470f
        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DETAILED QUESTION & SECTION BREAKDOWN", 60f, tableTop - 12f, paint)

        val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.rgb(148, 163, 184)
        }
        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 560f, gridPaint)
        canvas.drawLine(420f, tableTop, 420f, tableTop + 560f, gridPaint)
        canvas.drawLine(560f, tableTop, 560f, tableTop + 560f, gridPaint)
        canvas.drawLine(700f, tableTop, 700f, tableTop + 560f, gridPaint)

        // Header
        paint.color = Color.rgb(241, 245, 249)
        paint.style = Paint.Style.FILL
        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 50f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Section / Question", 80f, tableTop + 33f, paint)
        canvas.drawText("Max", 460f, tableTop + 33f, paint)
        canvas.drawText("Awarded", 590f, tableTop + 33f, paint)
        canvas.drawText("Comments", 730f, tableTop + 33f, paint)

        val questions = listOf(
            listOf("Section A (Algebra Q1-Q8)", "40", "37", "Strong justification"),
            listOf("Section B (Q9 Vector Geometry)", "10", "8", "Part (b) ratio proof omitted"),
            listOf("Section B (Q10 Differential Calc)", "10", "9", "Turning point confirmed"),
            listOf("Section B (Q11 Matrix Transf.)", "10", "8", "Det inversion clean"),
            listOf("Section B (Q12 Statistics & Dev)", "10", "8", "Variance calculation clean"),
            listOf("Section B (Q13 Trig Graphs)", "10", "8", "Amplitude boundary accurate"),
            listOf("Section B (Q14 Commercial Arith.)", "10", "8", "Compound interest rounded")
        )

        var qY = tableTop + 50f
        for (q in questions) {
            canvas.drawLine(60f, qY, width - 60f, qY, gridPaint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 19f
            paint.color = Color.rgb(51, 65, 85)
            canvas.drawText(q[0], 80f, qY + 45f, paint)
            canvas.drawText(q[1], 475f, qY + 45f, paint)

            // Red ink marks
            paint.color = Color.rgb(185, 28, 28)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(q[2], 620f, qY + 45f, paint)

            paint.color = Color.rgb(100, 116, 139)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            paint.textSize = 17f
            canvas.drawText(q[3], 720f, qY + 45f, paint)

            qY += 70f
        }

        // Examiner Signature in red ink
        val redSigPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(185, 28, 28)
            textSize = 24f
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }
        canvas.drawText("Examiner: Mr. D. Ochieng (H.O.D. Mathematics)", 60f, tableTop + 620f, redSigPaint)

        return bitmap
    }

    private fun generateFuelSlipBitmap(): Bitmap {
        val width = 900
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Thermal POS paper tone
        canvas.drawColor(Color.rgb(246, 248, 250))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Top receipt border jagged look
        paint.color = Color.rgb(220, 226, 235)
        canvas.drawRect(40f, 40f, width - 40f, height - 40f, paint)
        paint.color = Color.WHITE
        canvas.drawRect(46f, 46f, width - 46f, height - 46f, paint)

        // Header
        paint.color = Color.rgb(220, 38, 38)
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("TOTALENERGIES KENYA", width / 2f, 100f, paint)

        paint.color = Color.rgb(30, 41, 59)
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("MOMBASA ROAD SOUTH SERVICE STATION", width / 2f, 135f, paint)
        canvas.drawText("ETR TAX RECEIPT NO: TAX-NAI-99410", width / 2f, 165f, paint)
        canvas.drawText("PIN: P051102941X • DATE: 02-AUG-2026 08:42", width / 2f, 195f, paint)

        // Dashed divider
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f
        paint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
        paint.color = Color.rgb(148, 163, 184)
        canvas.drawLine(70f, 220f, width - 70f, 220f, paint)

        paint.pathEffect = null
        paint.style = Paint.Style.FILL
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        // Vehicle info
        canvas.drawText("VEHICLE REG: KDE 784V", 80f, 265f, paint)
        canvas.drawText("FLEET UNIT: Isuzu FRR 10T", 500f, 265f, paint)
        canvas.drawText("DRIVER: Josephat Kimani", 80f, 305f, paint)
        canvas.drawText("ODOMETER: 142,890 KM", 500f, 305f, paint)

        // Fuel transaction detail
        canvas.drawLine(70f, 335f, width - 70f, 335f, paint.apply { strokeWidth = 1.5f })

        var curY = 380f
        drawKeyValueRow(canvas, "Product:", "Excellium Diesel 50ppm", 80f, width - 80f, curY, paint)
        curY += 45f
        drawKeyValueRow(canvas, "Pump Number:", "Pump #04", 80f, width - 80f, curY, paint)
        curY += 45f
        drawKeyValueRow(canvas, "Volume Dispensed:", "145.20 Litres", 80f, width - 80f, curY, paint)
        curY += 45f
        drawKeyValueRow(canvas, "Unit Price / Litre:", "KES 203.50", 80f, width - 80f, curY, paint)
        curY += 45f
        drawKeyValueRow(canvas, "Taxable Net Amount:", "KES 25,472.93", 80f, width - 80f, curY, paint)
        curY += 45f
        drawKeyValueRow(canvas, "VAT (16% Included):", "KES 4,075.67", 80f, width - 80f, curY, paint)
        curY += 55f

        // Total Paid
        paint.strokeWidth = 2.5f
        paint.color = Color.rgb(30, 41, 59)
        canvas.drawLine(80f, curY - 20f, width - 80f, curY - 20f, paint)

        paint.textSize = 28f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.rgb(2, 132, 199)
        drawKeyValueRow(canvas, "TOTAL PAID (KES):", "KES 29,548.60", 80f, width - 80f, curY + 20f, paint)

        curY += 75f
        paint.textSize = 20f
        paint.color = Color.rgb(51, 65, 85)
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("PAYMENT METHOD: M-PESA Business Till 629188", 80f, curY, paint)
        curY += 35f
        canvas.drawText("M-PESA CONFIRMATION CODE: SHK893041F", 80f, curY, paint)

        // Vehicle Log Table
        curY += 60f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 22f
        canvas.drawText("VEHICLE DISPATCH LOG TRIP LEGS", 80f, curY, paint)

        curY += 20f
        val logPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.rgb(148, 163, 184)
        }
        canvas.drawRect(70f, curY, width - 70f, curY + 160f, logPaint)
        canvas.drawLine(420f, curY, 420f, curY + 160f, logPaint)
        canvas.drawLine(600f, curY, 600f, curY + 160f, logPaint)

        paint.textSize = 18f
        paint.color = Color.rgb(71, 85, 105)
        canvas.drawText("Leg 1: Nairobi to Naivasha", 85f, curY + 45f, paint)
        canvas.drawText("142,890 KM", 440f, curY + 45f, paint)
        canvas.drawText("145.20 L Added", 620f, curY + 45f, paint)

        canvas.drawLine(70f, curY + 75f, width - 70f, curY + 75f, logPaint)
        canvas.drawText("Leg 2: Naivasha to Nakuru", 85f, curY + 120f, paint)
        canvas.drawText("143,052 KM", 440f, curY + 120f, paint)
        canvas.drawText("Pending", 620f, curY + 120f, paint)

        // Barcode lines at bottom
        val barcodePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            strokeWidth = 3f
        }
        var barX = 180f
        val barY = height - 120f
        val heights = listOf(50f, 65f, 40f, 65f, 50f, 65f, 40f, 65f, 50f, 40f, 65f, 50f, 65f, 40f, 65f, 50f)
        for (h in heights) {
            canvas.drawLine(barX, barY, barX, barY + h, barcodePaint)
            barX += 14f
        }

        return bitmap
    }

    private fun generateEfrisInvoiceBitmap(): Bitmap {
        val width = 1000
        val height = 1380
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(Color.rgb(250, 252, 255))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // URA EFRIS Header Emblem
        paint.color = Color.rgb(14, 116, 144)
        paint.textSize = 32f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("MUKWANO ENTERPRISES LIMITED", width / 2f, 85f, paint)

        paint.color = Color.rgb(51, 65, 85)
        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("PLOT 30/32 MUKWANO COMPLEX, JINJA ROAD, KAMPALA", width / 2f, 120f, paint)

        // EFRIS Badge
        paint.color = Color.rgb(3, 105, 161)
        paint.textSize = 22f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("UGANDA REVENUE AUTHORITY • EFRIS TAX INVOICE", width / 2f, 165f, paint)

        paint.color = Color.rgb(203, 213, 225)
        paint.strokeWidth = 2f
        canvas.drawLine(50f, 190f, width - 50f, 190f, paint)

        // Seller & Buyer Details
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 20f
        paint.color = Color.rgb(30, 41, 59)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        canvas.drawText("FDN: FDN-0202609010088", 60f, 235f, paint)
        canvas.drawText("INVOICE DATE: 01-SEP-2026", 580f, 235f, paint)
        canvas.drawText("SELLER TIN: 1000041289", 60f, 275f, paint)
        canvas.drawText("BUYER TIN: 1004819203", 580f, 275f, paint)

        paint.typeface = Typeface.DEFAULT
        canvas.drawText("BUYER NAME: Arua Wholesale Distributors Ltd", 60f, 315f, paint)
        canvas.drawText("VERIFICATION: VRF-UG-77402-EFRIS", 580f, 315f, paint)

        // Items Table
        val tableTop = 360f
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
            color = Color.rgb(148, 163, 184)
        }
        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 380f, borderPaint)
        canvas.drawLine(200f, tableTop, 200f, tableTop + 380f, borderPaint)
        canvas.drawLine(520f, tableTop, 520f, tableTop + 380f, borderPaint)
        canvas.drawLine(640f, tableTop, 640f, tableTop + 380f, borderPaint)
        canvas.drawLine(780f, tableTop, 780f, tableTop + 380f, borderPaint)

        // Header
        paint.color = Color.rgb(224, 242, 254)
        paint.style = Paint.Style.FILL
        canvas.drawRect(60f, tableTop, width - 60f, tableTop + 50f, paint)

        paint.color = Color.rgb(3, 105, 161)
        paint.textSize = 19f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Item Code", 75f, tableTop + 33f, paint)
        canvas.drawText("Description", 220f, tableTop + 33f, paint)
        canvas.drawText("Qty", 540f, tableTop + 33f, paint)
        canvas.drawText("Unit Price", 660f, tableTop + 33f, paint)
        canvas.drawText("Line Total", 800f, tableTop + 33f, paint)

        val items = listOf(
            listOf("MKW-SOAP-01", "Star Laundry Bar Soap (Box 25)", "50 Boxes", "UGX 64,000", "UGX 3,200,000"),
            listOf("MKW-OIL-02", "Refined Sunflower Oil 5L (Carton 4)", "40 Ctn", "UGX 75,000", "UGX 3,000,000"),
            listOf("MKW-DET-03", "Aqua White Detergent 500g (Pck 30)", "25 Pcks", "UGX 40,000", "UGX 1,000,000")
        )

        var itY = tableTop + 50f
        for (item in items) {
            canvas.drawLine(60f, itY, width - 60f, itY, borderPaint)
            paint.typeface = Typeface.DEFAULT
            paint.textSize = 18f
            paint.color = Color.rgb(30, 41, 59)
            canvas.drawText(item[0], 75f, itY + 45f, paint)
            canvas.drawText(item[1], 220f, itY + 45f, paint)
            canvas.drawText(item[2], 540f, itY + 45f, paint)
            canvas.drawText(item[3], 660f, itY + 45f, paint)
            paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(item[4], 800f, itY + 45f, paint)
            itY += 75f
        }

        // Summary Tax Calculation
        var sumY = tableTop + 430f
        drawKeyValueRow(canvas, "Net Taxable Amount (UGX):", "UGX 7,200,000", 460f, width - 80f, sumY, paint)
        sumY += 45f
        drawKeyValueRow(canvas, "Standard VAT (18%):", "UGX 1,296,000", 460f, width - 80f, sumY, paint)
        sumY += 55f

        paint.color = Color.rgb(14, 116, 144)
        paint.textSize = 26f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        drawKeyValueRow(canvas, "GROSS TOTAL PAYABLE:", "UGX 8,496,000", 400f, width - 80f, sumY, paint)

        // URA EFRIS QR Code simulated matrix
        val qrTop = height - 320f
        val qrPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42)
        }
        canvas.drawRect(80f, qrTop, 220f, qrTop + 140f, Paint().apply { color = Color.WHITE; style = Paint.Style.FILL })
        canvas.drawRect(80f, qrTop, 220f, qrTop + 140f, Paint().apply { color = Color.DKGRAY; style = Paint.Style.STROKE; strokeWidth = 2f })

        // Nested simulated QR blocks
        canvas.drawRect(95f, qrTop + 15f, 135f, qrTop + 55f, qrPaint)
        canvas.drawRect(165f, qrTop + 15f, 205f, qrTop + 55f, qrPaint)
        canvas.drawRect(95f, qrTop + 85f, 135f, qrTop + 125f, qrPaint)
        canvas.drawRect(150f, qrTop + 75f, 180f, qrTop + 105f, qrPaint)

        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 17f
        paint.typeface = Typeface.DEFAULT
        canvas.drawText("Scan QR with URA EFRIS App to authenticate tax stamp", 240f, qrTop + 60f, paint)
        canvas.drawText("Security Hash: 8b71d9a04f2c91834e021a88", 240f, qrTop + 95f, paint)

        return bitmap
    }

    private fun drawKeyValueRow(
        canvas: Canvas,
        label: String,
        value: String,
        startX: Float,
        endX: Float,
        y: Float,
        paint: Paint
    ) {
        val oldAlign = paint.textAlign
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText(label, startX, y, paint)
        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText(value, endX, y, paint)
        paint.textAlign = oldAlign
    }

    fun drawCursiveHandwriting(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        color: Int = Color.rgb(24, 45, 115), // Authentic East African blue ballpoint ink
        textSize: Float = 24f,
        isSlightlyBlurred: Boolean = false,
        varyingInkDensity: Boolean = true
    ) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            this.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        }

        canvas.save()
        // Marion Richardson / Palmer cursive standard 14-degree forward slant
        canvas.translate(x, y)
        canvas.skew(-0.24f, 0f)

        if (varyingInkDensity) {
            var curX = 0f
            for (i in text.indices) {
                val ch = text.substring(i, i + 1)
                val charW = paint.measureText(ch)
                // Natural ballpoint pen pressure variations (start/stops darker, hairline lighter)
                val alphaVal = when {
                    i == 0 || i == text.length - 1 -> 255
                    i % 3 == 0 -> 190
                    i % 2 == 0 -> 240
                    else -> 215
                }
                paint.alpha = alphaVal

                if (isSlightlyBlurred) {
                    val blurPaint = Paint(paint).apply { alpha = 70 }
                    canvas.drawText(ch, curX + 0.8f, 0.6f, blurPaint)
                }

                canvas.drawText(ch, curX, 0f, paint)
                curX += charW
            }
        } else {
            if (isSlightlyBlurred) {
                val blurPaint = Paint(paint).apply { alpha = 70 }
                canvas.drawText(text, 0.8f, 0.6f, blurPaint)
            }
            canvas.drawText(text, 0f, 0f, paint)
        }

        canvas.restore()
    }

    private fun generateKigeziLedgerBitmap(): Bitmap {
        val width = 1000
        val height = 1350
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Aged ledger paper background (faint cream with subtle ruled lines)
        canvas.drawColor(Color.rgb(248, 245, 237))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Blue ledger horizontal guide lines
        val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(220, 226, 235)
            strokeWidth = 1f
        }
        for (y in 200..1300 step 42) {
            canvas.drawLine(40f, y.toFloat(), width - 40f, y.toFloat(), linePaint)
        }

        // Red left margin vertical line (typical in school & SACCO manual ledgers)
        val marginPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(248, 180, 180)
            strokeWidth = 2f
        }
        canvas.drawLine(180f, 40f, 180f, height - 40f, marginPaint)

        // Header Border
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            color = Color.rgb(55, 65, 81)
        }
        canvas.drawRect(35f, 35f, width - 35f, height - 35f, borderPaint)

        // Header Title
        paint.color = Color.rgb(17, 24, 39)
        paint.textSize = 30f
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("KIGEZI FARMERS CO-OPERATIVE SACCO LTD", width / 2f, 85f, paint)

        paint.textSize = 20f
        paint.typeface = Typeface.DEFAULT
        paint.color = Color.rgb(75, 85, 99)
        canvas.drawText("KABALE MAIN BRANCH • MANUAL LOAN & PASSBOOK FOLIO #84", width / 2f, 120f, paint)
        canvas.drawText("UGANDA CO-OPERATIVE SOCIETIES ACT CAP 112", width / 2f, 150f, paint)

        // Metadata block (mixture of pre-printed rubber headers and handwritten cursive details)
        paint.textAlign = Paint.Align.LEFT
        paint.textSize = 21f
        paint.color = Color.rgb(55, 65, 81)
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        val startY = 210f
        canvas.drawText("FOLIO NO: FOLIO-2026-KBL-084", 50f, startY, paint)
        canvas.drawText("DATE: 18-AUG-2026", 620f, startY, paint)

        canvas.drawText("MEMBER ID: KGZ-5108", 50f, startY + 45f, paint)
        canvas.drawText("BRANCH: KABALE CENTRAL", 620f, startY + 45f, paint)

        canvas.drawText("MEMBER NAME:", 50f, startY + 90f, paint)
        // Authentic East African Cursive Name with variable ballpoint ink density
        drawCursiveHandwriting(
            canvas,
            "Grace Atuhaire Tumusiime",
            220f,
            startY + 90f,
            color = Color.rgb(18, 48, 128),
            textSize = 24f,
            isSlightlyBlurred = false,
            varyingInkDensity = true
        )

        canvas.drawText("PURPOSE:", 50f, startY + 135f, paint)
        drawCursiveHandwriting(
            canvas,
            "Agricultural Lime & Irish Potato Fertilizer",
            170f,
            startY + 135f,
            color = Color.rgb(20, 50, 130),
            textSize = 22f,
            varyingInkDensity = true
        )

        // Handwritten Financial Summary Box
        val boxPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 2f
            color = Color.rgb(156, 163, 175)
        }
        canvas.drawRoundRect(RectF(50f, startY + 160f, width - 50f, startY + 440f), 10f, 10f, boxPaint)

        var rowY = startY + 205f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 22f
        canvas.drawText("Principal Installment (UGX):", 70f, rowY, paint)
        drawCursiveHandwriting(canvas, "UGX 450,000", width - 240f, rowY, color = Color.rgb(25, 45, 115), textSize = 24f)

        rowY += 45f
        canvas.drawText("Monthly Interest Charge (UGX):", 70f, rowY, paint)
        drawCursiveHandwriting(canvas, "UGX 36,000", width - 230f, rowY, color = Color.rgb(25, 45, 115), textSize = 24f)

        rowY += 45f
        canvas.drawText("Share Development Levy:", 70f, rowY, paint)
        drawCursiveHandwriting(canvas, "UGX 50,000", width - 230f, rowY, color = Color.rgb(25, 45, 115), textSize = 24f)

        rowY += 45f
        canvas.drawLine(70f, rowY - 15f, width - 70f, rowY - 15f, linePaint.apply { strokeWidth = 1.5f })

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.color = Color.rgb(17, 24, 39)
        canvas.drawText("TOTAL CASH DEPOSITED:", 70f, rowY + 25f, paint)
        // Bold cursive handwritten total
        drawCursiveHandwriting(canvas, "UGX 536,000", width - 260f, rowY + 25f, color = Color.rgb(15, 60, 35), textSize = 27f, varyingInkDensity = true)

        // Manual Loan Schedule Ledger Table
        val tableTop = startY + 480f
        paint.color = Color.rgb(17, 24, 39)
        paint.textSize = 23f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("REPAYMENT LEDGER ENTRIES (MANUAL RECORD)", 50f, tableTop - 15f, paint)

        // Table Header
        paint.color = Color.rgb(241, 245, 249)
        paint.style = Paint.Style.FILL
        canvas.drawRect(50f, tableTop, width - 50f, tableTop + 45f, paint)
        canvas.drawRect(50f, tableTop, width - 50f, tableTop + 45f, borderPaint.apply { strokeWidth = 1.5f })

        paint.color = Color.rgb(15, 23, 42)
        paint.textSize = 19f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("INSTALLMENT", 60f, tableTop + 30f, paint)
        canvas.drawText("PRINCIPAL", 250f, tableTop + 30f, paint)
        canvas.drawText("INTEREST", 460f, tableTop + 30f, paint)
        canvas.drawText("RUNNING BAL", 660f, tableTop + 30f, paint)
        canvas.drawText("OFFICER", 860f, tableTop + 30f, paint)

        // Table Rows with deliberate variations in ink density and slight optical blur
        var tY = tableTop + 45f
        val rows = listOf(
            Triple("Inst 1 (May)", "UGX 450,000", "UGX 45,000") to ("UGX 2,250,000" to "B. Byaruhanga"),
            Triple("Inst 2 (Jun)", "UGX 450,000", "UGX 42,000") to ("UGX 1,800,000" to "B. Byaruhanga"),
            Triple("Inst 3 (Jul)", "UGX 450,000", "UGX 39,000") to ("UGX 1,350,000" to "B. Byaruhanga")
        )

        rows.forEachIndexed { idx, (trip, bal) ->
            // Row background
            paint.style = Paint.Style.STROKE
            paint.color = Color.rgb(203, 213, 225)
            canvas.drawRect(50f, tY, width - 50f, tY + 60f, paint)

            val rowColor = if (idx == 1) {
                // Faded carbon-paper impression (lighter ink density)
                Color.rgb(80, 100, 150)
            } else {
                Color.rgb(20, 45, 115)
            }

            drawCursiveHandwriting(canvas, trip.first, 60f, tY + 40f, color = rowColor, textSize = 20f)
            drawCursiveHandwriting(canvas, trip.second, 250f, tY + 40f, color = rowColor, textSize = 20f)
            drawCursiveHandwriting(canvas, trip.third, 460f, tY + 40f, color = rowColor, textSize = 20f)
            // Row 2 is slightly blurred to simulate carbon copy second sheet
            drawCursiveHandwriting(canvas, bal.first, 660f, tY + 40f, color = rowColor, textSize = 20f, isSlightlyBlurred = (idx == 1))
            drawCursiveHandwriting(canvas, bal.second, 860f, tY + 40f, color = rowColor, textSize = 18f)

            tY += 60f
        }

        // Circular Teller Rubber Stamp simulation
        val stampCenter = Pair(820f, height - 160f)
        val stampPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 3f
            color = Color.argb(160, 160, 20, 50) // Weathered purple-red stamp ink
        }
        canvas.drawCircle(stampCenter.first, stampCenter.second, 65f, stampPaint)
        canvas.drawCircle(stampCenter.first, stampCenter.second, 58f, stampPaint.apply { strokeWidth = 1f })

        val stampTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(170, 160, 20, 50)
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("KIGEZI SACCO", stampCenter.first, stampCenter.second - 20f, stampTextPaint)
        canvas.drawText("18 AUG 2026", stampCenter.first, stampCenter.second + 5f, stampTextPaint)
        canvas.drawText("VERIFIED - PAID", stampCenter.first, stampCenter.second + 30f, stampTextPaint)

        // Footer notice
        paint.color = Color.rgb(107, 114, 128)
        paint.textSize = 16f
        paint.textAlign = Paint.Align.LEFT
        canvas.drawText("Manual ledger entry recorded by: Ledger Officer B. Byaruhanga • Kabale", 50f, height - 50f, paint)

        return bitmap
    }
}
