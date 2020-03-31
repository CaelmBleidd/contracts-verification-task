package org.jetbrains.dummy.lang

import org.jetbrains.dummy.lang.tree.Element
import java.io.OutputStream
import java.io.PrintStream

class DiagnosticReporter(
        outputStream: OutputStream
) {
    private val outputStream = PrintStream(outputStream)

    enum class ReportType(val type: String) {
        WARNING("warning"),
        ERROR("error")
    }

    fun report(element: Element, message: String, type: ReportType) {
        outputStream.println(
                "${if (type == ReportType.WARNING) "WARNING" else "ERROR"}: line ${element.line}: $message"
        )
    }
}