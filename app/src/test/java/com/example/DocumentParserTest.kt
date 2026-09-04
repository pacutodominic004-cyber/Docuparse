package com.example

import com.example.data.model.ParsedDocumentResult
import com.example.data.model.SamplePresetsData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class DocumentParserTest {

    @Test
    fun `parse sacco deposit counterfoil json`() {
        val saccoPreset = SamplePresetsData.presets.find { it.id == "sacco_wazalendo" }
        assertNotNull(saccoPreset)

        val result = ParsedDocumentResult.fromJson(saccoPreset!!.goldenJson)
        assertEquals("SACCO_Receipt", result.metadata.detectedType)
        assertEquals("High", result.metadata.confidenceScore)
        assertEquals("Wazalendo Savings & Credit Co-operative Society Ltd", result.metadata.organizationName)
        assertEquals("REC-2026-UG-88219", result.metadata.referenceNumber)

        // Key values check (UGX normalization)
        assertTrue(result.extractedKeyValues.containsKey("total_amount_deposited"))
        assertEquals("UGX 688,000", result.extractedKeyValues["total_amount_deposited"])
        assertEquals("UGX 350,000", result.extractedKeyValues["share_contribution"])

        // Tabular data check
        assertEquals(3, result.tabularData.size)
        assertEquals("May 2026", result.tabularData[0]["period"])

        // Anomalies check
        assertEquals(3, result.flaggedAnomalies.size)
    }

    @Test
    fun `parse school continuous assessment exam json`() {
        val schoolPreset = SamplePresetsData.presets.find { it.id == "school_cat_exam" }
        assertNotNull(schoolPreset)

        val result = ParsedDocumentResult.fromJson(schoolPreset!!.goldenJson)
        assertEquals("School_Exam", result.metadata.detectedType)
        assertEquals("High", result.metadata.confidenceScore)
        assertEquals("Alliance High School (Kikuyu, Kenya)", result.metadata.organizationName)
        assertEquals("ADM-2024-819", result.metadata.referenceNumber)
        assertEquals("Kevin Mwangi Otieno", result.extractedKeyValues["student_name"])
        assertEquals("86", result.extractedKeyValues["marks_obtained"])
        assertEquals("A (Plain)", result.extractedKeyValues["grade"])
        assertEquals(7, result.tabularData.size)
    }

    @Test
    fun `parse fleet fuel receipt pos and route log json`() {
        val fleetPreset = SamplePresetsData.presets.find { it.id == "fleet_fuel_total" }
        assertNotNull(fleetPreset)

        val result = ParsedDocumentResult.fromJson(fleetPreset!!.goldenJson)
        assertEquals("Fleet_Fuel_Invoice", result.metadata.detectedType)
        assertEquals("TotalEnergies Marketing Kenya PLC - Mombasa Rd Station", result.metadata.organizationName)
        assertEquals("KDE 784V", result.extractedKeyValues["vehicle_reg_number"])
        assertEquals("KES 29,548.60", result.extractedKeyValues["total_amount_paid"])
        assertEquals(2, result.tabularData.size)
        assertEquals(2, result.flaggedAnomalies.size)
    }

    @Test
    fun `parse ura efris electronic tax invoice json`() {
        val efrisPreset = SamplePresetsData.presets.find { it.id == "ura_efris_invoice" }
        assertNotNull(efrisPreset)

        val result = ParsedDocumentResult.fromJson(efrisPreset!!.goldenJson)
        assertEquals("EFRIS_Tax_Invoice", result.metadata.detectedType)
        assertEquals("FDN-0202609010088", result.metadata.referenceNumber)
        assertEquals("Mukwano Enterprises Limited (Kampala, Uganda)", result.metadata.organizationName)
        assertEquals("UGX 8,496,000", result.extractedKeyValues["gross_total_payable"])
        assertEquals(3, result.tabularData.size)
    }

    @Test
    fun `viewModel onDocumentCaptured sets bitmap and initiates parsing`() {
        val app = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = com.example.ui.DocumentParserViewModel(app)
        val testBitmap = android.graphics.Bitmap.createBitmap(100, 100, android.graphics.Bitmap.Config.ARGB_8888)

        viewModel.onDocumentCaptured(testBitmap)

        val state = viewModel.uiState.value
        assertEquals(testBitmap, state.selectedBitmap)
        org.junit.Assert.assertNull(state.activePreset)
        assertEquals(0, state.activeTab)
    }

    @Test
    fun `universal format converter generates valid docx, xlsx, searchable pdf, and png files`() {
        val app = androidx.test.core.app.ApplicationProvider.getApplicationContext<android.app.Application>()
        val saccoPreset = SamplePresetsData.presets.first()
        val result = ParsedDocumentResult.fromJson(saccoPreset.goldenJson)
        val exportDir = java.io.File(app.cacheDir, "exports").apply { mkdirs() }

        // Test DOCX generation
        val docxFile = com.example.util.UniversalFormatConverter.generateWordDocx(result, exportDir)
        assertTrue(docxFile.exists())
        assertTrue(docxFile.length() > 0)
        assertTrue(docxFile.name.endsWith(".docx"))

        // Test XLSX generation
        val xlsxFile = com.example.util.UniversalFormatConverter.generateExcelXlsx(result, exportDir)
        assertTrue(xlsxFile.exists())
        assertTrue(xlsxFile.length() > 0)
        assertTrue(xlsxFile.name.endsWith(".xlsx"))

        // Test Searchable PDF generation
        val pdfFile = com.example.util.UniversalFormatConverter.generateSearchablePdf(app, result, null)
        assertTrue(pdfFile.exists())
        assertTrue(pdfFile.length() > 0)
        assertTrue(pdfFile.name.endsWith(".pdf"))

        // Test High-Res Image (PNG) generation
        val pngFile = com.example.util.UniversalFormatConverter.generateHighResImage(
            app,
            result,
            null,
            com.example.util.UniversalFormatConverter.ImageExportFormat.PNG
        )
        assertTrue(pngFile.exists())
        assertTrue(pngFile.length() > 0)
        assertTrue(pngFile.name.endsWith(".png"))

        // Test CSV generation
        val csv = com.example.util.AccountingExportManager.generateAccountingCsv(result)
        assertTrue(csv.contains("DOCUPARSE EA"))
        assertTrue(csv.contains("FINANCIAL & LEDGER SUMMARY ATTRIBUTES"))
    }
}
