package org.jetbrains.dummy.lang

import org.junit.Test

class DummyLanguageTestGenerated : AbstractDummyLanguageTest() {
    @Test
    fun testAssignment() {
        doTest("testData/assignment.dummy")
    }
    
    @Test
    fun testBad() {
        doTest("testData/bad.dummy")
    }
    
    @Test
    fun testCombo() {
        doTest("testData/combo.dummy")
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
}
