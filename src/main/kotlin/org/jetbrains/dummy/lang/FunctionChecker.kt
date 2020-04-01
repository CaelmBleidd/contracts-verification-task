package org.jetbrains.dummy.lang

import org.jetbrains.dummy.lang.tree.*

class FunctionChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        for (function in file.functions) {
            checkDuplicateReturnStatements(function)
            checkIfReturnNotLastStatement(function)
        }
    }

    private fun checkDuplicateReturnStatements(block: Block): Boolean {
        var count = 0
        var result = false
        for (statement in block.statements) {
            when (statement) {
                is ReturnStatement -> ++count
                is IfStatement -> {
                    result = result || checkDuplicateReturnStatements(statement.thenBlock)
                    if (statement.elseBlock != null) {
                        result = result || checkDuplicateReturnStatements(statement.elseBlock)
                    }
                }
            }
        }
        if (count > 1) {
            result = true
        }
        return result
    }

    private fun checkDuplicateReturnStatements(function: FunctionDeclaration) {
        if (checkDuplicateReturnStatements(function.body)) {
            reportDuplicateReturnStatements(function)
        }
    }

    private fun findReturnStatement(statements: List<Statement>): Boolean {
        for (statement in statements) {
            when (statement) {
                is ReturnStatement -> return true
                is IfStatement -> {
                    return if (statement.elseBlock == null) {
                        findReturnStatement(statement.thenBlock.statements)
                    } else {
                        findReturnStatement(statement.thenBlock.statements) &&
                                findReturnStatement(statement.elseBlock.statements)
                    }
                }
            }
        }
        return false
    }

    private fun checkIfReturnNotLastStatement(function: FunctionDeclaration) {
        if (findReturnStatement(function.body.statements.dropLast(1))) {
            for (statement in function.body.statements) {
                if (findReturnStatement(listOf(statement))) {
                    reportUnreachableCode(function, statement)
                    return
                }
            }
        }
    }

    private fun reportDuplicateReturnStatements(function: FunctionDeclaration) {
        reporter.report(
                function,
                "Function has too many return statements",
                DiagnosticReporter.ReportType.ERROR
        )
    }

    private fun reportUnreachableCode(function: FunctionDeclaration, returnStatement: Statement) {
        reporter.report(returnStatement,
                "Unreachable code after line ${returnStatement.line} because of return statement" +
                        " in function '${function.name}'",
                DiagnosticReporter.ReportType.WARNING)
    }
}