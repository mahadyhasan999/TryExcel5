package com.example.tryexcel5

import android.util.Log
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object ExcelUtil {

    fun readExcel(file: File): List<User> {
        val users = mutableListOf<User>()
        try {
            FileInputStream(file).use { fis ->
                val workbook = WorkbookFactory.create(fis)
                val sheet = workbook.getSheetAt(0)

                for (row in sheet) {
                    if (row.rowNum == 0) continue // Skip header row

                    val username = getCellValue(row.getCell(0))
                    val userId = getCellValue(row.getCell(1))
                    val password = getCellValue(row.getCell(2))
                    val role = getCellValue(row.getCell(3))
                    val buyer = getCellValue(row.getCell(4))
                    val workOrder = getCellValue(row.getCell(5))
                    val styleCode = getCellValue(row.getCell(6))
                    val stepName = getCellValue(row.getCell(7))
                    val stepNumber = getCellValue(row.getCell(8))
                    val totalQty = getCellValue(row.getCell(9))

                    val user = User(
                        username,
                        userId,
                        password,
                        role,
                        buyer,
                        workOrder,
                        styleCode,
                        stepName,
                        stepNumber,
                        totalQty
                    )
                    users.add(user)
                }

                workbook.close()
            }
            Log.d("ExcelUtil", "Excel file read successfully")
        } catch (e: Exception) {
            Log.e("ExcelUtil", "Error reading Excel file: ${e.message}", e)
        }
        return users
    }

    private fun getCellValue(cell: Cell?): String {
        return when (cell?.cellType) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> {
                // Check if the numeric value is actually an integer
                if (cell.cellStyle.dataFormatString == "General") {
                    // If the cell is formatted as General, handle integer values
                    if (cell.numericCellValue % 1 == 0.0) {
                        cell.numericCellValue.toLong().toString()// Integer value
                    } else {
                        cell.numericCellValue.toString()// Floating-point value
                    }
                } else {
                    cell.numericCellValue.toString()
                }
            }

            CellType.BOOLEAN -> cell.booleanCellValue.toString()
            CellType.FORMULA -> cell.cellFormula
            else -> ""
        }
    }


    fun appendUser(file: File, user: User) {
        try {
            val workbook = if (file.exists()) {
                WorkbookFactory.create(FileInputStream(file))
            } else {
                XSSFWorkbook()
            }
            val sheet = workbook.getSheet("SmartCount") ?: workbook.createSheet("SmartCount")

            // Check if header already exists, if not create it
            val header = sheet.getRow(0) ?: sheet.createRow(0)
            if (header.getCell(0) == null) header.createCell(0).setCellValue("User Name")
            if (header.getCell(1) == null) header.createCell(1).setCellValue("Card Number")
            if (header.getCell(2) == null) header.createCell(2).setCellValue("Password")
            if (header.getCell(3) == null) header.createCell(3).setCellValue("Role")
            if (header.getCell(4) == null) header.createCell(4).setCellValue("Buyer")
            if (header.getCell(5) == null) header.createCell(5).setCellValue("Work Order")
            if (header.getCell(6) == null) header.createCell(6).setCellValue("Style Code")
            if (header.getCell(7) == null) header.createCell(7).setCellValue("Step Name")
            if (header.getCell(8) == null) header.createCell(8).setCellValue("Step Number")
            if (header.getCell(9) == null) header.createCell(9).setCellValue("totalQty")
            if (header.getCell(10) == null) header.createCell(10).setCellValue("Start Time")
            if (header.getCell(11) == null) header.createCell(11).setCellValue("End Time")
            Log.d("ExcelUtil", "Headers created in Excel file if not exists.")

            // Create a cell style for integer values
            val integerStyle: CellStyle = workbook.createCellStyle().apply {
                dataFormat = workbook.createDataFormat()
                    .getFormat("0") // Ensures no decimal places for integers
            }

            // Create a cell style for floating-point values using "General" format
            val floatStyle: CellStyle = workbook.createCellStyle().apply {
                dataFormat = workbook.createDataFormat()
                    .getFormat("General") // Keeps the actual precision of floating-point values
            }

            // Utility function to set cell value and style based on content
            fun setCellValueAndStyle(cell: Cell, value: String) {
                if (value.contains(".")) {
                    try {
                        val numericValue = value.toDouble()
                        cell.cellStyle = floatStyle
                        cell.setCellValue(numericValue)
                    } catch (e: NumberFormatException) {
                        cell.setCellValue(value) // Fallback to text if parsing fails
                    }
                } else {
                    try {
                        val numericValue = value.toLong()
                        cell.cellStyle = integerStyle
                        cell.setCellValue(numericValue.toDouble())
                    } catch (e: NumberFormatException) {
                        cell.setCellValue(value) // Fallback to text if parsing fails
                    }
                }
            }

            // Append new user row
            val newRow = sheet.createRow(sheet.lastRowNum + 1)

            setCellValueAndStyle(newRow.createCell(0), user.username)
            setCellValueAndStyle(newRow.createCell(1), user.userId)
            setCellValueAndStyle(newRow.createCell(2), user.password)
            setCellValueAndStyle(newRow.createCell(3), user.role)
            setCellValueAndStyle(newRow.createCell(4), user.buyer)
            setCellValueAndStyle(newRow.createCell(5), user.workOrder)
            setCellValueAndStyle(newRow.createCell(6), user.styleCode)
            setCellValueAndStyle(newRow.createCell(7), user.stepName)
            setCellValueAndStyle(newRow.createCell(8), user.stepNumber)
            setCellValueAndStyle(newRow.createCell(9), user.totalQty)
            setCellValueAndStyle(newRow.createCell(10), user.startTime)
            setCellValueAndStyle(newRow.createCell(11), user.endTime)

            FileOutputStream(file).use { fos ->
                workbook.write(fos)
            }
            workbook.close()
            Log.d("ExcelUtil", "User data appended successfully")
        } catch (e: Exception) {
            Log.e("ExcelUtil", "Error appending user data: ${e.message}", e)
        }
    }
}