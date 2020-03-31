package org.jetbrains.dummy.lang

import org.jetbrains.dummy.lang.tree.*
import org.jetbrains.dummy.lang.DiagnosticReporter.ReportType;
import java.lang.StringBuilder
import kotlin.collections.HashMap

class StatementChecker(private val reporter: DiagnosticReporter) : AbstractChecker() {
    override fun inspect(file: File) {
        checkDoubleDeclarations(file.functions)
        val functionsName = file.functions
                .map { createNameForFunction(it.name, it.parameters.size) }
                .toHashSet()
        for (functionDeclaration in file.functions) {
            checkFunction(functionDeclaration, functionsName, file.functions.toSet())
        }
    }

    private fun checkFunction(function: FunctionDeclaration,
                              functionsName: HashSet<String>,
                              functions: Set<FunctionDeclaration>) {
        val variables = hashMapOf<String, Boolean>()

        checkParametersForDuplicates(function)
        checkDuplicateReturnStatements(function)
        checkIfReturnNotLastStatement(function)
        checkIfBodyIsEmpty(function)

        variables += function.parameters.map { Pair(it, true) }
        for (statement in function.body.statements) {
            when (statement) {
                is Assignment -> {
                    checkAssignment(statement, variables, functionsName, functions)
                    variables[statement.variable] = true;
                }
                is IfStatement ->
                    checkIfStatement(statement, variables, functionsName, functions)
                is VariableDeclaration -> {
                    checkVariableDeclaration(statement, variables, functionsName, functions)
                    variables.putIfAbsent(statement.name, statement.initializer != null)
                }
                is ReturnStatement ->
                    checkReturnStatement(statement, variables, functionsName, functions)
                is Expression ->
                    checkExpression(statement, variables, functionsName, functions)
            }
        }
    }

    private fun checkIfBodyIsEmpty(function: FunctionDeclaration) {
        if (function.body.statements.isEmpty()) {
            reportEmptyFunctionBody(function)
        }
    }


    private fun checkIfReturnNotLastStatement(function: FunctionDeclaration) {
        val returnStatement = function.body.statements.find { it is ReturnStatement } ?: return
        if (returnStatement != function.body.statements.last()) {
            reportUnreachableCode(function, returnStatement)
        }
    }

    private fun checkDuplicateReturnStatements(function: FunctionDeclaration) {
        val ifContainsManyReturns =
                function.body.statements.filterIsInstance<IfStatement>().map {
                    it.thenBlock.statements.filterIsInstance<ReturnStatement>().size > 1 ||
                            (if (it.elseBlock != null) {
                                it.elseBlock.statements.filterIsInstance<ReturnStatement>().size > 1
                            } else {
                                false
                            })
                }.any { it }

        if (function.body.statements.filterIsInstance<ReturnStatement>().size > 1 || ifContainsManyReturns) {
            reportDuplicateReturnStatements(function)
        }

    }

    private fun checkAssignment(statement: Assignment,
                                variables: HashMap<String, Boolean>,
                                functionNames: HashSet<String>,
                                functions: Set<FunctionDeclaration>) {

        if (!variables.containsKey(statement.variable)) {
            reportAssignmentBeforeDeclaration(statement)
        }

        when (statement.rhs) {
            is FunctionCall -> {
                checkIfFunctionHasReturnValue(statement.rhs, functions)
                checkFunctionCall(statement.rhs, variables, functionNames, functions)
            }
            is VariableAccess -> {
                if (statement.rhs.name == statement.variable) {
                    reportAssignmentVariableToItself(statement)
                }
                checkVariableAccess(statement.rhs, variables)
            }
            else -> return
        }
    }

    private fun checkIfFunctionHasReturnValue(functionCall: FunctionCall,
                                              functions: Set<FunctionDeclaration>) {
        val function =
                functions.find {
                    functionCall.function == it.name && functionCall.arguments.size == it.parameters.size
                } ?: return
        for (statement in function.body.statements.filterIsInstance<ReturnStatement>()) {
            if (statement.result != null) {
                return
            }
        }
        reportUseValueInFunctionCallWithoutReturnValue(functionCall)
    }

    private fun checkFunctionCall(functionCall: FunctionCall,
                                  variables: HashMap<String, Boolean>,
                                  functionNames: HashSet<String>,
                                  functions: Set<FunctionDeclaration>) {

        if (!functionNames.contains(createNameForFunction(functionCall.function, functionCall.arguments.size))) {
            reportFunctionCallWithoutFunctionDeclaration(functionCall)
        }

        for (argument in functionCall.arguments) {
            when (argument) {
                is FunctionCall -> {
                    checkIfFunctionHasReturnValue(argument, functions)
                    checkFunctionCall(argument, variables, functionNames, functions)
                }
                is VariableAccess -> checkVariableAccess(argument, variables)
                is IntConst, is BooleanConst -> return
            }
        }
    }

    private fun checkVariableAccess(rhs: VariableAccess, variables: HashMap<String, Boolean>) {
        if (!variables.containsKey(rhs.name)) {
            reportAccessBeforeDeclaration(rhs);
            return;
        }
        if (!variables[rhs.name]!!) {
            reportAccessBeforeInitialization(rhs)
        }
    }


    private fun checkIfStatement(statement: IfStatement,
                                 variables: HashMap<String, Boolean>,
                                 functionNames: HashSet<String>,
                                 functions: Set<FunctionDeclaration>) {
        when (statement.condition) {
            is VariableAccess -> checkVariableAccess(statement.condition, variables)
            is FunctionCall -> checkFunctionCall(statement.condition, variables, functionNames, functions)
            is IntConst -> reportNonBooleanValueInIfStatement(statement.condition)
            is BooleanConst -> reportConditionIsConst(statement)
        }
        checkIfBlock(statement.thenBlock, variables, functionNames, functions)
        if (statement.elseBlock != null) {
            checkIfBlock(statement.elseBlock, variables, functionNames, functions)
        }
    }

    private fun checkIfBlock(block: Block,
                             variables: HashMap<String, Boolean>,
                             functionNames: java.util.HashSet<String>,
                             functions: Set<FunctionDeclaration>) {
        val blockVariables = hashMapOf<String, Boolean>()

        if (block.statements.isEmpty()) {
            reportEmptyIfBlock(block)
            return
        }

        for (statement in block.statements) {
            when (statement) {
                is Assignment -> {
                    checkAssignment(statement, variables, functionNames, functions)
                    variables[statement.variable] = true
                }
                is IfStatement -> checkIfStatement(statement, variables, functionNames, functions)
                is VariableDeclaration -> {
                    checkVariableDeclaration(
                            statement,
                            (variables + blockVariables) as HashMap<String, Boolean>,
                            functionNames,
                            functions
                    )
                    blockVariables.putIfAbsent(statement.name, statement.initializer != null)
                }
                is ReturnStatement -> checkReturnStatement(statement, variables, functionNames, functions)
                is Expression -> checkExpression(statement, variables, functionNames, functions)
            }
        }
    }


    private fun checkVariableDeclaration(statement: VariableDeclaration,
                                         variables: HashMap<String, Boolean>,
                                         functionNames: HashSet<String>,
                                         functions: Set<FunctionDeclaration>) {
        if (variables.containsKey(statement.name)) {
            reportDoubleVariableDeclaration(statement)
        }

        if (statement.initializer != null) {
            when (statement.initializer) {
                is FunctionCall -> {
                    checkIfFunctionHasReturnValue(statement.initializer, functions)
                    checkFunctionCall(statement.initializer, variables, functionNames, functions)
                }
                is VariableAccess -> checkVariableAccess(statement.initializer, variables)
                else -> return
            }
        }
    }


    private fun checkReturnStatement(statement: ReturnStatement,
                                     variables: HashMap<String, Boolean>,
                                     functionNames: HashSet<String>,
                                     functions: Set<FunctionDeclaration>) {
        if (statement.result != null) {
            when (statement.result) {
                is FunctionCall -> {
                    checkIfFunctionHasReturnValue(statement.result, functions)
                    checkFunctionCall(statement.result, variables, functionNames, functions)
                }
                is VariableAccess -> checkVariableAccess(statement.result, variables)
                else -> reportFunctionReturnsConst(statement)
            }
        }
    }


    private fun checkParametersForDuplicates(function: FunctionDeclaration) {
        if (function.parameters.distinct().size != function.parameters.size) {
            val variables = hashSetOf<String>()
            val duplicates = hashSetOf<String>()
            for (variable in function.parameters) {
                if (variable in variables) {
                    duplicates.add(variable)
                }
                variables.add(variable)
            }
            duplicates.forEach { reportDuplicatesInParametersFunction(function, it) }
        }
    }


    private fun createNameForFunction(functionName: String, parametersSize: Int) =
            functionName + parametersSize

    private fun checkDoubleDeclarations(functions: List<FunctionDeclaration>) {
        val duplicates = functions - functions.distinctBy { createNameForFunction(it.name, it.parameters.size) }
        for (duplicate in duplicates.distinctBy { createNameForFunction(it.name, it.parameters.size) }) {
            reportDoubleFunctionDeclaration(functions.filter {
                createNameForFunction(duplicate.name, duplicate.parameters.size) == createNameForFunction(it.name, it.parameters.size)
            })
        }
    }

    private fun checkExpression(expression: Expression,
                                variables: HashMap<String, Boolean>,
                                functionNames: HashSet<String>,
                                functions: Set<FunctionDeclaration>) {
        when (expression) {
            is FunctionCall -> {
                val function: FunctionDeclaration? = functions.find {
                    it.name == expression.function && it.parameters.size == expression.arguments.size
                }

                if (function != null) {
                    if (function.body.statements.filterIsInstance<ReturnStatement>().any { it.result != null }) {
                        reportFunctionValueIgnored(expression)
                    }
                }
                checkFunctionCall(expression, variables, functionNames, functions)
            }
            is VariableAccess -> {
                checkVariableAccess(expression, variables)
                reportUnusedExpression(expression)
            }
            else -> reportUnusedExpression(expression)
        }
    }

    private fun reportUnusedExpression(expression: Expression) {
        reporter.report(expression, "Unused expression", ReportType.WARNING)
    }

    private fun reportFunctionValueIgnored(expression: FunctionCall) {
        reporter.report(expression, "Return value of function '${expression.function}' is ignored",
                ReportType.WARNING)
    }


    private fun reportDoubleFunctionDeclaration(functions: List<FunctionDeclaration>) {
        val message =
                "Function '${functions.first().name}' with ${functions.first().parameters.size} arguments" +
                        " is declared twice times or more in ${functions.map { it.line }.joinToString()} lines"
        reporter.report(functions.first(), message, ReportType.ERROR)
    }

    // Use this method for reporting errors
    private fun reportAccessBeforeInitialization(access: VariableAccess) {
        reporter.report(
                access,
                "Variable '${access.name}' is accessed before initialization",
                ReportType.ERROR
        )
    }

    private fun reportAccessBeforeDeclaration(variable: VariableAccess) {
        reporter.report(
                variable,
                "Variable '${variable.name}' is accessed before declaration",
                ReportType.ERROR
        )
    }

    private fun reportUnreachableCode(function: FunctionDeclaration, returnStatement: Statement) {
        reporter.report(function,
                "Unreachable code after line ${returnStatement.line} because of return statement",
                ReportType.WARNING)
    }

    private fun reportDuplicateReturnStatements(element: Element) {
        reporter.report(
                element,
                "Occurred many return statements",
                ReportType.ERROR
        )
    }

    private fun reportUseValueInFunctionCallWithoutReturnValue(functionCall: FunctionCall) {
        reporter.report(functionCall,
                "Return value of '${functionCall.function}' is required"
                        + ", but the function doesn't return anything",
                ReportType.ERROR)
    }


    private fun reportDoubleVariableDeclaration(statement: VariableDeclaration) {
        reporter.report(statement,
                "Duplicate declaration of variable '${statement.name}'",
                ReportType.ERROR)
    }

    private fun reportFunctionCallWithoutFunctionDeclaration(functionCall: FunctionCall) {
        reporter.report(functionCall, "Function call '${functionCall.function}' with " +
                "${functionCall.arguments.size} arguments without" +
                " it declaration", ReportType.ERROR)
    }


    private fun reportAssignmentVariableToItself(statement: Assignment) {
        reporter.report(statement, "Assignment variable '${statement.variable}' to itself",
                ReportType.WARNING)
    }

    private fun reportAssignmentBeforeDeclaration(statement: Assignment) {
        reporter.report(statement, "Assignment variable '${statement.variable}' in without declaration",
                ReportType.ERROR)
    }

    private fun reportConditionIsConst(statement: IfStatement) {
        val message = StringBuilder("Boolean const uses in condition, so")
        if (statement.elseBlock != null) {
            message.append(
                    " ${if ((statement.condition as BooleanConst).value) "'else'" else "'then'"} block is unreachable")
        } else {
            message.append(
                    " ${if ((statement.condition as BooleanConst).value) "if can be removed"
                    else "'then' block can be removed"}"
            )
        }
        reporter.report(statement.condition, message.toString(), ReportType.WARNING)
    }

    private fun reportDuplicatesInParametersFunction(function: FunctionDeclaration, parameterName: String) {
        reporter.report(function, "Parameter '$parameterName' occurred twice or more " +
                "times in function declaration", ReportType.ERROR)
    }

    private fun reportFunctionReturnsConst(result: ReturnStatement) {
        reporter.report(result, "Function returns const", ReportType.WARNING)
    }

    private fun reportNonBooleanValueInIfStatement(condition: IntConst) {
        reporter.report(condition, "Non boolean variables uses in condition",
                ReportType.WARNING)
    }

    private fun reportEmptyFunctionBody(function: FunctionDeclaration) {
        reporter.report(function, "Function '${function.name}' has empty body", ReportType.WARNING)
    }

    private fun reportEmptyIfBlock(block: Block) {
        reporter.report(block, "Empty block, can be safely removed", ReportType.WARNING)
    }
}