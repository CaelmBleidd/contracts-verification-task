package org.jetbrains.dummy.lang

import org.junit.Test

class DummyLanguageTestGenerated : AbstractDummyLanguageTest() {
    @Test
    fun testAccessBeforeDeclaration() {
        doTest("testData/accessBeforeDeclaration.dummy")
    }
    
    @Test
    fun testAccessBeforeInitialization() {
        doTest("testData/accessBeforeInitialization.dummy")
    }
    
    @Test
    fun testAlwaysReturnConst() {
        doTest("testData/alwaysReturnConst.dummy")
    }
    
    @Test
    fun testAssignment() {
        doTest("testData/assignment.dummy")
    }
    
    @Test
    fun testAssignmentBeforeDeclaration() {
        doTest("testData/assignmentBeforeDeclaration.dummy")
    }
    
    @Test
    fun testAssignmentReturnValue() {
        doTest("testData/assignmentReturnValue.dummy")
    }
    
    @Test
    fun testAssignmentToItself() {
        doTest("testData/assignmentToItself.dummy")
    }
    
    @Test
    fun testBad() {
        doTest("testData/bad.dummy")
    }
    
    @Test
    fun testBooleanConstInCondition() {
        doTest("testData/booleanConstInCondition.dummy")
    }
    
    @Test
    fun testCombo() {
        doTest("testData/combo.dummy")
    }
    
    @Test
    fun testDoubledeclaration() {
        doTest("testData/doubledeclaration.dummy")
    }
    
    @Test
    fun testDuplicateDeclaration() {
        doTest("testData/duplicateDeclaration.dummy")
    }
    
    @Test
    fun testDuplicatedParameters() {
        doTest("testData/duplicatedParameters.dummy")
    }
    
    @Test
    fun testEmptyBody() {
        doTest("testData/emptyBody.dummy")
    }
    
    @Test
    fun testFunctionWithoutDeclaration() {
        doTest("testData/functionWithoutDeclaration.dummy")
    }
    
    @Test
    fun testFunctionchecker() {
        doTest("testData/functionchecker.dummy")
    }
    
    @Test
    fun testFunctions() {
        doTest("testData/functions.dummy")
    }
    
    @Test
    fun testGood() {
        doTest("testData/good.dummy")
    }
    
    @Test
    fun testIfEmptyBody() {
        doTest("testData/ifEmptyBody.dummy")
    }
    
    @Test
    fun testIgnore() {
        doTest("testData/ignore.dummy")
    }
    
    @Test
    fun testIntConstCondition() {
        doTest("testData/intConstCondition.dummy")
    }
    
    @Test
    fun testMultiplyReturnStatements() {
        doTest("testData/multiplyReturnStatements.dummy")
    }
    
    @Test
    fun testUnreachableCode() {
        doTest("testData/unreachableCode.dummy")
    }
    
    @Test
    fun testUnused() {
        doTest("testData/unused.dummy")
    }
}
