package com.example.data.model

object SamplePresetsData {

    val presets = listOf(
        SamplePreset(
            id = "sacco_wazalendo",
            title = "Wazalendo SACCO Deposit Counterfoil",
            subtitle = "Savings passbook & loan register • Uganda",
            category = DocumentCategory.SACCO,
            detectedType = "SACCO_Receipt",
            sampleImageDescription = "Hand-written carbon-copy savings slip with member ID, shares, monthly deposit, loan principal breakdown, and teller stamps.",
            goldenJson = """{
  "document_metadata": {
    "detected_type": "SACCO_Receipt",
    "confidence_score": "High",
    "primary_date": "2026-08-14",
    "organization_name": "Wazalendo Savings & Credit Co-operative Society Ltd",
    "reference_number": "REC-2026-UG-88219"
  },
  "extracted_key_values": {
    "member_id": "WZ-94021",
    "member_name": "Francis Okello",
    "share_contribution": "UGX 350,000",
    "monthly_savings": "UGX 200,000",
    "loan_repayment": "UGX 120,000",
    "interest_paid": "UGX 18,000",
    "total_amount_deposited": "UGX 688,000",
    "cumulative_shares": "UGX 4,200,000",
    "outstanding_loan_balance": "UGX 1,062,000",
    "currency": "UGX",
    "teller_id": "TEL-KLA-04"
  },
  "tabular_data": [
    {
      "period": "May 2026",
      "savings_deposit": "UGX 200,000",
      "loan_principal": "UGX 120,000",
      "cumulative_balance": "UGX 3,800,000",
      "teller_initial": "M.B."
    },
    {
      "period": "Jun 2026",
      "savings_deposit": "UGX 200,000",
      "loan_principal": "UGX 120,000",
      "cumulative_balance": "UGX 4,000,000",
      "teller_initial": "M.B."
    },
    {
      "period": "Jul 2026",
      "savings_deposit": "UGX 200,000",
      "loan_principal": "UGX 120,000",
      "cumulative_balance": "UGX 4,200,000",
      "teller_initial": "M.B."
    }
  ],
  "flagged_anomalies": [
    "Normalized regional currency from handwritten 'Shs' and 'Ush' to ISO 'UGX'",
    "Slanted handwriting in 'Twelve thousand' verbally corrected in numerical box",
    "Teller ink endorsement stamp partially overlaps running balance figure on row 3"
  ],
  "handwriting_ocr_audit": {
    "handwriting_detected": true,
    "cursive_style_identified": "East African Marian Richardson cursive with connected ascender loops",
    "ink_density_compensation_applied": true,
    "blur_compensation_applied": true,
    "mathematical_reconciliation_verified": true,
    "transcription_accuracy_rating": "100.0% Verified"
  }
}"""
        ),
        SamplePreset(
            id = "school_cat_exam",
            title = "Alliance High School CAT Grade Register",
            subtitle = "Continuous Assessment & Exam Paper • Kenya",
            category = DocumentCategory.SCHOOL,
            detectedType = "School_Exam",
            sampleImageDescription = "Continuous Assessment Test score sheet with student details, question-by-question breakdown, red-ink teacher notes, and grade calculation.",
            goldenJson = """{
  "document_metadata": {
    "detected_type": "School_Exam",
    "confidence_score": "High",
    "primary_date": "2026-07-22",
    "organization_name": "Alliance High School (Kikuyu, Kenya)",
    "reference_number": "ADM-2024-819"
  },
  "extracted_key_values": {
    "student_name": "Kevin Mwangi Otieno",
    "admission_number": "ADM-2024-819",
    "class_stream": "Form 3 West",
    "subject": "Mathematics Paper 1 (Calculus & Vectors)",
    "assessment_type": "Term 2 CAT 2",
    "marks_obtained": "86",
    "total_possible_marks": "100",
    "grade": "A (Plain)",
    "class_rank": "4 of 48",
    "examiner_name": "Mr. D. Ochieng",
    "term_fee_balance": "KES 0.00 (Cleared)"
  },
  "tabular_data": [
    {
      "section": "Section A (Algebra Q1-Q8)",
      "max_marks": "40",
      "marks_awarded": "37",
      "remarks": "Strong step justification"
    },
    {
      "section": "Section B (Q9 Vector Geometry)",
      "max_marks": "10",
      "marks_awarded": "8",
      "remarks": "Part (b) ratio proof omitted"
    },
    {
      "section": "Section B (Q10 Differential Calculus)",
      "max_marks": "10",
      "marks_awarded": "9",
      "remarks": "Turning point confirmed"
    },
    {
      "section": "Section B (Q11 Matrix Transformations)",
      "max_marks": "10",
      "marks_awarded": "8",
      "remarks": "Determinant inversion correct"
    },
    {
      "section": "Section B (Q12 Statistics & Deviations)",
      "max_marks": "10",
      "marks_awarded": "8",
      "remarks": "Variance calculation clean"
    },
    {
      "section": "Section B (Q13 Trigonometric Graphs)",
      "max_marks": "10",
      "marks_awarded": "8",
      "remarks": "Amplitude boundary accurate"
    },
    {
      "section": "Section B (Q14 Commercial Arithmetic)",
      "max_marks": "10",
      "marks_awarded": "8",
      "remarks": "Compound interest rounded to 2 d.p."
    }
  ],
  "flagged_anomalies": [
    "Question 11 score corrected with red ink cross-out from 7 to 8 with examiner initial",
    "Top margin hole-punch slightly clips institution crest header line"
  ],
  "handwriting_ocr_audit": {
    "handwriting_detected": true,
    "cursive_style_identified": "Palmer Script cursive student answers & red-ink examiner annotations",
    "ink_density_compensation_applied": true,
    "blur_compensation_applied": true,
    "mathematical_reconciliation_verified": true,
    "transcription_accuracy_rating": "100.0% Verified"
  }
}"""
        ),
        SamplePreset(
            id = "fleet_fuel_total",
            title = "TotalEnergies Fleet Fuel & Vehicle Log",
            subtitle = "Fuel POS slip & driver route sheet • Kenya",
            category = DocumentCategory.FLEET_CPA,
            detectedType = "Fleet_Fuel_Invoice",
            sampleImageDescription = "Thermal POS fuel invoice paired with vehicle mileage log sheet showing litres, pump unit price, VAT breakdown, and M-PESA confirmation.",
            goldenJson = """{
  "document_metadata": {
    "detected_type": "Fleet_Fuel_Invoice",
    "confidence_score": "High",
    "primary_date": "2026-08-02",
    "organization_name": "TotalEnergies Marketing Kenya PLC - Mombasa Rd Station",
    "reference_number": "ETR-2026-TOT-99410"
  },
  "extracted_key_values": {
    "vendor": "TotalEnergies Marketing Kenya PLC",
    "station_branch": "Mombasa Road South Service Station, Nairobi",
    "vehicle_reg_number": "KDE 784V",
    "vehicle_type": "Isuzu FRR 10-Tonner",
    "driver_name": "Josephat Kimani",
    "fuel_product": "Excellium Diesel 50ppm",
    "litres_dispensed": "145.20 L",
    "unit_price_per_litre": "KES 203.50",
    "taxable_net_amount": "KES 25,472.93",
    "vat_16_percent": "KES 4,075.67",
    "total_amount_paid": "KES 29,548.60",
    "payment_mode": "M-PESA Business Till 629188",
    "mpesa_trans_code": "SHK893041F",
    "odometer_reading": "142,890 KM"
  },
  "tabular_data": [
    {
      "trip_leg": "Leg 1: Nairobi Industrial to Naivasha Turnoff",
      "start_km": "142,730",
      "end_km": "142,890",
      "fuel_added": "145.20 L",
      "driver_sig": "J. Kimani"
    },
    {
      "trip_leg": "Leg 2: Naivasha to Nakuru Depot",
      "start_km": "142,890",
      "end_km": "143,052",
      "fuel_added": "Pending",
      "driver_sig": "J. Kimani"
    }
  ],
  "flagged_anomalies": [
    "Slight thermal ribbon fading along receipt right border",
    "Mathematical validation: 145.20 L * KES 203.50 = KES 29,548.20 (40 cents rounding difference on POS software)"
  ]
}"""
        ),
        SamplePreset(
            id = "ura_efris_invoice",
            title = "Uganda Revenue Authority (URA) EFRIS Tax Invoice",
            subtitle = "Electronic fiscal receipt & VAT ledger • Uganda",
            category = DocumentCategory.FLEET_CPA,
            detectedType = "EFRIS_Tax_Invoice",
            sampleImageDescription = "Official URA EFRIS Electronic Fiscal Receipt with Fiscal Device Number (FDN), QR verification code, buyer TIN, and 18% VAT breakdown.",
            goldenJson = """{
  "document_metadata": {
    "detected_type": "EFRIS_Tax_Invoice",
    "confidence_score": "High",
    "primary_date": "2026-09-01",
    "organization_name": "Mukwano Enterprises Limited (Kampala, Uganda)",
    "reference_number": "FDN-0202609010088"
  },
  "extracted_key_values": {
    "organization_name": "Mukwano Enterprises Limited",
    "seller_tin": "1000041289",
    "customer_name": "Arua Wholesale Distributors Ltd",
    "customer_tin": "1004819203",
    "fiscal_device_number": "FDN-0202609010088",
    "verification_code": "VRF-UG-77402-EFRIS",
    "net_taxable_amount": "UGX 7,200,000",
    "vat_amount_18_percent": "UGX 1,296,000",
    "gross_total_payable": "UGX 8,496,000",
    "currency": "UGX",
    "payment_terms": "EFT / Bank Transfer 30 Days"
  },
  "tabular_data": [
    {
      "item_code": "MKW-SOAP-01",
      "description": "Star Laundry Bar Soap 1kg (Box of 25)",
      "qty": "50 Boxes",
      "unit_price": "UGX 64,000",
      "vat_rate": "18%",
      "line_total": "UGX 3,200,000"
    },
    {
      "item_code": "MKW-OIL-02",
      "description": "Refined Sunflower Cooking Oil 5L (Carton of 4)",
      "qty": "40 Cartons",
      "unit_price": "UGX 75,000",
      "vat_rate": "18%",
      "line_total": "UGX 3,000,000"
    },
    {
      "item_code": "MKW-DET-03",
      "description": "Aqua White Washing Powder 500g (Pack of 30)",
      "qty": "25 Packs",
      "unit_price": "UGX 40,000",
      "vat_rate": "18%",
      "line_total": "UGX 1,000,000"
    }
  ],
  "flagged_anomalies": [
    "EFRIS QR Code graphic dot matrix pattern has light ink impression at bottom-left corner",
    "Verified arithmetic sum: UGX 3.2M + UGX 3.0M + UGX 1.0M = UGX 7.2M net + 18% VAT = UGX 8.496M exact match"
  ]
}"""
        ),
        SamplePreset(
            id = "sacco_kigezi_ledger",
            title = "Kigezi SACCO Manual Cursive Ledger",
            subtitle = "Handwritten loan register & passbook • Uganda",
            category = DocumentCategory.SACCO,
            detectedType = "SACCO_Manual_Ledger",
            sampleImageDescription = "Manual cursive loan register with variable ballpoint ink density, faded carbon duplicate lines, blurred cursive entries, and 100% verified arithmetic sums.",
            goldenJson = """{
  "document_metadata": {
    "detected_type": "SACCO_Manual_Ledger",
    "confidence_score": "High",
    "primary_date": "2026-08-18",
    "organization_name": "Kigezi Farmers Co-operative SACCO Ltd (Kabale Branch)",
    "reference_number": "FOLIO-2026-KBL-084"
  },
  "extracted_key_values": {
    "member_id": "KGZ-5108",
    "member_name": "Grace Atuhaire Tumusiime",
    "loan_purpose": "Agricultural Lime & Irish Potato Fertilizer",
    "principal_installment": "UGX 450,000",
    "interest_charge": "UGX 36,000",
    "share_development_levy": "UGX 50,000",
    "total_cash_deposited": "UGX 536,000",
    "remaining_principal_balance": "UGX 1,350,000",
    "ledger_officer": "B. Byaruhanga (Cursive Sig)",
    "currency": "UGX"
  },
  "tabular_data": [
    {
      "installment_no": "Inst 1 (May)",
      "principal_paid": "UGX 450,000",
      "interest_paid": "UGX 45,000",
      "running_balance": "UGX 2,250,000",
      "officer_sig": "B. Byaruhanga"
    },
    {
      "installment_no": "Inst 2 (Jun)",
      "principal_paid": "UGX 450,000",
      "interest_paid": "UGX 42,000",
      "running_balance": "UGX 1,800,000",
      "officer_sig": "B. Byaruhanga"
    },
    {
      "installment_no": "Inst 3 (Jul)",
      "principal_paid": "UGX 450,000",
      "interest_paid": "UGX 39,000",
      "running_balance": "UGX 1,350,000",
      "officer_sig": "B. Byaruhanga"
    }
  ],
  "flagged_anomalies": [
    "Faded carbon-transfer ink on installment row 2 compensated by adaptive dynamic range stretching",
    "Cursive 'Atuhaire' connected loop between 'u' and 'h' verified against member registry",
    "Arithmetic consistency verified: UGX 450,000 + UGX 36,000 + UGX 50,000 = UGX 536,000 exact match"
  ],
  "handwriting_ocr_audit": {
    "handwriting_detected": true,
    "cursive_style_identified": "East African British Marion Richardson Cursive Script",
    "ink_density_compensation_applied": true,
    "blur_compensation_applied": true,
    "mathematical_reconciliation_verified": true,
    "transcription_accuracy_rating": "100.0% Verified"
  }
}"""
        )
    )
}
