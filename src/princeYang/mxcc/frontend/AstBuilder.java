package princeYang.mxcc.frontend;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.TerminalNode;
import princeYang.mxcc.ast.*;
import princeYang.mxcc.errors.MxError;
import princeYang.mxcc.parser.*;

import java.nio.file.LinkOption;
import java.util.ArrayList;
import java.util.List;

public class AstBuilder extends MxBaseVisitor<Node>
{

    Type voidType = new VoidType();
    Type intType = new IntType();
    Type stringType = new StringType();
    Type boolType = new BoolType();
    Type nullType = new NullType();

    @Override
    public Node visitMxprogram(MxParser.MxprogramContext ctx)
    {
        Location location = new Location(ctx);
        List<DeclNode> declarations = new ArrayList<DeclNode>();
        if (ctx.declarations() != null)
        {
            for (ParserRuleContext declaration : ctx.declarations())
            {
                Node decla = visit(declaration);
                declarations.add((DeclNode) decla);
            }
        }
        return new MxProgNode(location, declarations);
    }

    @Override
    public Node visitDeclarations(MxParser.DeclarationsContext ctx)
    {
        Location location = new Location(ctx);
        DeclNode decla;
        if (ctx.variableDeclaration() != null)
            return visit(ctx.variableDeclaration());
        else if (ctx.functionDeclaration() != null)
            return visit(ctx.functionDeclaration());
        else if (ctx.classDeclaration() != null)
            return visit(ctx.classDeclaration());
        else throw new MxError(location, "AstBuilder: Declarations Type Error!");
    }

    @Override
    public Node visitVariableDeclaration(MxParser.VariableDeclarationContext ctx)
    {
        Location location = new Location(ctx);
        TypeNode type = (TypeNode) visit(ctx.nonVoidType());
        String name = ctx.Identifier().getText();
        ExprNode init;
        if (ctx.expression() != null)
            init = (ExprNode) visit(ctx.expression());
        else init = null;
        return new VarDeclNode(location, name, type, init);
    }

    @Override
    public Node visitWithVoidType(MxParser.WithVoidTypeContext ctx)
    {
        Location location = new Location(ctx);
        if (ctx.nonVoidType() != null)
            return visit(ctx.nonVoidType());
        else
            return new TypeNode(location, voidType);
    }

    @Override
    public Node visitArrayType(MxParser.ArrayTypeContext ctx)
    {
        Location location = new Location(ctx);
        TypeNode arrType = (TypeNode) visit(ctx.nonVoidType());
        return new TypeNode(location, new ArrayType(arrType.getType()));
    }

    @Override
    public Node visitNonVoidnonArrayType(MxParser.NonVoidnonArrayTypeContext ctx)
    {
        Type type;
        Location location = new Location(ctx);
        if (ctx.Identifier() != null)
            return new TypeNode(location, new ClassType(ctx.Identifier().getText()));
        if (ctx.Int() != null)
            type = intType;
        else if (ctx.Bool() != null)
            type = boolType;
        else if (ctx.String() != null)
            type = stringType;
        else throw new MxError(location, "AstBuilder: NonVoidNonArrayType ERROR!");
        return new TypeNode(location, type);
    }

    @Override
    public Node visitFunctionDeclaration(MxParser.FunctionDeclarationContext ctx)
    {
        Location location = new Location(ctx);
        TypeNode retType = null;
        String identifier = ctx.Identifier().getText();
        VarDeclNode paraDecl;
        List<VarDeclNode> paramentDeclarations = new ArrayList<VarDeclNode>();
        FuncBlockNode funcBlock;

        if (ctx.withVoidType() != null)
            retType = (TypeNode) visit(ctx.withVoidType());
        if (ctx.paramentDeclarations() != null)
        {
            for (ParserRuleContext para : ctx.paramentDeclarations().paramentDeclaration())
            {
                paraDecl = (VarDeclNode) visit(para);
                paramentDeclarations.add(paraDecl);
            }
        }
        funcBlock = (FuncBlockNode) visit(ctx.functionBlock());
        return new FuncDeclNode(location, identifier, retType, paramentDeclarations, funcBlock);
    }



    @Override
    public Node visitClassDeclaration(MxParser.ClassDeclarationContext ctx)
    {
        Location location = new Location(ctx);
        String identifier = ctx.Identifier().getText();
        List<VarDeclNode> varDecls = new ArrayList<VarDeclNode>();
        List<FuncDeclNode> funDecls = new ArrayList<FuncDeclNode>();
        DeclNode classState;

        if (ctx.classStatement() != null)
        {
            for (ParserRuleContext state : ctx.classStatement())
            {
                classState = (DeclNode) visit(state);
                if (classState instanceof VarDeclNode)
                    varDecls.add((VarDeclNode) classState);
                else if (classState instanceof FuncDeclNode)
                    funDecls.add((FuncDeclNode) classState);
                else throw new MxError(location,"AstBuilder: ClassDeclaration classStatement ERROR!");
            }
        }
        return new ClassDeclNode(location, identifier, varDecls, funDecls);
    }

    @Override
    public Node visitFunctionBlock(MxParser.FunctionBlockContext ctx)
    {
        Location location = new Location(ctx);
        List<StateNode> funcStates = new ArrayList<StateNode>();
        List<VarDeclNode> varDeclas = new ArrayList<VarDeclNode>();
        Node funcState;

        if (ctx.functionStatement() != null)
        {
            for (ParserRuleContext state : ctx.functionStatement())
            {
                funcState = visit(state);
                if (funcState != null)
                {
                    if (funcState instanceof VarDeclNode)
                        varDeclas.add((VarDeclNode) funcState);
                    else funcStates.add((StateNode) funcState);
                }
            }
        }
        return new FuncBlockNode(location, funcStates, varDeclas);
    }


}

