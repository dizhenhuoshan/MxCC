// Generated from /home/wymt/Code/Compiler2019/MxCompiler/src/princeYang/mxcc/paser/Mx.g4 by ANTLR 4.7.2
package princeYang.mxcc.paser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MxParser}.
 */
public interface MxListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MxParser#mxprogram}.
	 * @param ctx the parse tree
	 */
	void enterMxprogram(MxParser.MxprogramContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#mxprogram}.
	 * @param ctx the parse tree
	 */
	void exitMxprogram(MxParser.MxprogramContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(MxParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(MxParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#withVoidType}.
	 * @param ctx the parse tree
	 */
	void enterWithVoidType(MxParser.WithVoidTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#withVoidType}.
	 * @param ctx the parse tree
	 */
	void exitWithVoidType(MxParser.WithVoidTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayType}
	 * labeled alternative in {@link MxParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void enterArrayType(MxParser.ArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayType}
	 * labeled alternative in {@link MxParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void exitArrayType(MxParser.ArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NonArrayType}
	 * labeled alternative in {@link MxParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayType(MxParser.NonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NonArrayType}
	 * labeled alternative in {@link MxParser#nonVoidType}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayType(MxParser.NonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#nonVoidnonArrayType}.
	 * @param ctx the parse tree
	 */
	void enterNonVoidnonArrayType(MxParser.NonVoidnonArrayTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#nonVoidnonArrayType}.
	 * @param ctx the parse tree
	 */
	void exitNonVoidnonArrayType(MxParser.NonVoidnonArrayTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(MxParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(MxParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#paramentDeclarations}.
	 * @param ctx the parse tree
	 */
	void enterParamentDeclarations(MxParser.ParamentDeclarationsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#paramentDeclarations}.
	 * @param ctx the parse tree
	 */
	void exitParamentDeclarations(MxParser.ParamentDeclarationsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#paramentDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterParamentDeclaration(MxParser.ParamentDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#paramentDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitParamentDeclaration(MxParser.ParamentDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionBlock}.
	 * @param ctx the parse tree
	 */
	void enterFunctionBlock(MxParser.FunctionBlockContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionBlock}.
	 * @param ctx the parse tree
	 */
	void exitFunctionBlock(MxParser.FunctionBlockContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(MxParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(MxParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#classStatement}.
	 * @param ctx the parse tree
	 */
	void enterClassStatement(MxParser.ClassStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#classStatement}.
	 * @param ctx the parse tree
	 */
	void exitClassStatement(MxParser.ClassStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#functionStatement}.
	 * @param ctx the parse tree
	 */
	void enterFunctionStatement(MxParser.FunctionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#functionStatement}.
	 * @param ctx the parse tree
	 */
	void exitFunctionStatement(MxParser.FunctionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MxParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MxParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void enterLoopStatement(MxParser.LoopStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#loopStatement}.
	 * @param ctx the parse tree
	 */
	void exitLoopStatement(MxParser.LoopStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterJumpStatement(MxParser.JumpStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitJumpStatement(MxParser.JumpStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void enterConditionalStatement(MxParser.ConditionalStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#conditionalStatement}.
	 * @param ctx the parse tree
	 */
	void exitConditionalStatement(MxParser.ConditionalStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code FunctionCallExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterFunctionCallExpr(MxParser.FunctionCallExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code FunctionCallExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitFunctionCallExpr(MxParser.FunctionCallExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PostFixExp}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPostFixExp(MxParser.PostFixExpContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PostFixExp}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPostFixExp(MxParser.PostFixExpContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubScriptExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubScriptExpr(MxParser.SubScriptExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubScriptExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubScriptExpr(MxParser.SubScriptExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterIdentifierExpr(MxParser.IdentifierExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code IdentifierExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitIdentifierExpr(MxParser.IdentifierExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code PreFixExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterPreFixExpr(MxParser.PreFixExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code PreFixExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitPreFixExpr(MxParser.PreFixExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code SubExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterSubExpr(MxParser.SubExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code SubExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitSubExpr(MxParser.SubExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code BinaryExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitBinaryExpr(MxParser.BinaryExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NewExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitNewExpr(MxParser.NewExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConstantExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterConstantExpr(MxParser.ConstantExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConstantExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitConstantExpr(MxParser.ConstantExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ThisExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitThisExpr(MxParser.ThisExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code MemeryAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterMemeryAccessExpr(MxParser.MemeryAccessExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code MemeryAccessExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitMemeryAccessExpr(MxParser.MemeryAccessExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code AssignExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterAssignExpr(MxParser.AssignExprContext ctx);
	/**
	 * Exit a parse tree produced by the {@code AssignExpr}
	 * labeled alternative in {@link MxParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitAssignExpr(MxParser.AssignExprContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConstInt}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstInt(MxParser.ConstIntContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConstInt}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstInt(MxParser.ConstIntContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConstStr}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstStr(MxParser.ConstStrContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConstStr}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstStr(MxParser.ConstStrContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConstBool}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstBool(MxParser.ConstBoolContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConstBool}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstBool(MxParser.ConstBoolContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ConstNull}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstNull(MxParser.ConstNullContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ConstNull}
	 * labeled alternative in {@link MxParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstNull(MxParser.ConstNullContext ctx);
	/**
	 * Enter a parse tree produced by {@link MxParser#paramentList}.
	 * @param ctx the parse tree
	 */
	void enterParamentList(MxParser.ParamentListContext ctx);
	/**
	 * Exit a parse tree produced by {@link MxParser#paramentList}.
	 * @param ctx the parse tree
	 */
	void exitParamentList(MxParser.ParamentListContext ctx);
	/**
	 * Enter a parse tree produced by the {@code ArrayCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterArrayCreator(MxParser.ArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code ArrayCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitArrayCreator(MxParser.ArrayCreatorContext ctx);
	/**
	 * Enter a parse tree produced by the {@code NonArrayCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void enterNonArrayCreator(MxParser.NonArrayCreatorContext ctx);
	/**
	 * Exit a parse tree produced by the {@code NonArrayCreator}
	 * labeled alternative in {@link MxParser#creator}.
	 * @param ctx the parse tree
	 */
	void exitNonArrayCreator(MxParser.NonArrayCreatorContext ctx);
}