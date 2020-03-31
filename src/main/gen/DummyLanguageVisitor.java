// Generated from /Users/CaelmBleidd/Programming/contracts-verification-task/src/main/antlr/org/jetbrains/dummy/lang/DummyLanguage.g4 by ANTLR 4.8

package org.jetbrains.dummy.lang;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link DummyLanguageParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface DummyLanguageVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#prog}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProg(DummyLanguageParser.ProgContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#func_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_def(DummyLanguageParser.Func_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#block}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBlock(DummyLanguageParser.BlockContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#stat}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStat(DummyLanguageParser.StatContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#return_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn_statement(DummyLanguageParser.Return_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#if_statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIf_statement(DummyLanguageParser.If_statementContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#assign}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssign(DummyLanguageParser.AssignContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#var_def}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVar_def(DummyLanguageParser.Var_defContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#func}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc(DummyLanguageParser.FuncContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#expr}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpr(DummyLanguageParser.ExprContext ctx);
	/**
	 * Visit a parse tree produced by {@link DummyLanguageParser#func_call}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunc_call(DummyLanguageParser.Func_callContext ctx);
}