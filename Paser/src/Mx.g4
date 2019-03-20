/**
    Mx Grammar File
    Powered by ANTLR 4
    Copyright by princeYang
*/
grammar Mx;

// ===Paser===

// Declaration:
mxprogram
    : (variableDeclaration | functionDeclaration | classDeclaration)* EOF
    ;

variableDeclaration
    : nonVoidType Identifier ('=' expression)? ';'
    ;

    withVoidType
        : nonVoidType
        | Void
        ;

    nonVoidType
        : nonVoidType '[' ']'   # ArrayType
        | nonVoidnonArrayType   # NonArrayType
        ;

    nonVoidnonArrayType
        : Int
        | Bool
        | String
        | Identifier
        ;

functionDeclaration
    : withVoidType Identifier '(' paramentDeclarations? ')' functionBlock
    ;

    paramentDeclarations
        : paramentDeclaration (',' paramentDeclaration)*
        ;

    paramentDeclaration
        : nonVoidType Identifier
        ;

    functionBlock
        : '{' functionStatement* '}'
        ;

classDeclaration
    : Class Identifier '{' classStatement* '}'
    ;

// Statements:
classStatement
    : variableDeclaration
    | functionDeclaration
    ;

functionStatement
    : statement
    | variableDeclaration
    ;

statement
    : expression ';'
    | loopStatement
    | jumpStatement
    | conditionalStatement
    | functionBlock
    | ';'
    ;

    loopStatement
        : For '(' start = expression? ';' stop = expression? ';' step = expression? ')' functionStatement
        | While '(' expression ')' functionBlock
        ;

    jumpStatement
        : Return expression ? ';'
        | Break ';'
        | Continue ';'
        ;

    conditionalStatement
        : If '(' expression ')' thenStatement = statement (Else elseStatement = statement)?
        ;

// expression

expression
    : expression '.' Identifier                                     # MemeryAccessExpr
    | expression '(' paramentList? ')'                              # FunctionCallExpr
    | array = expression '[' sub = expression ']'                   # SubScriptExpr
    | expression op = ('++' | '--')                                 # PostFixExp

    | <assoc = right> New creator                                   # NewExpr
    | <assoc = right> op = ('++' | '--') expression                 # PreFixExpr
    | <assoc = right> op = ('+' | '-') expression                   # PreFixExpr
    | <assoc = right> op = ('!' | '~') expression                   # PreFixExpr

    | lhs = expression op = ('*' | '/' | '%') rhs = expression      # BinaryExpr
    | lhs = expression op = ('+' | '-') rhs = expression            # BinaryExpr
    | lhs = expression op = ('<<' | '>>') rhs = expression          # BinaryExpr
    | lhs = expression op = ('<=' | '>=') rhs = expression          # BinaryExpr
    | lhs = expression op = ('<' | '>') rhs = expression            # BinaryExpr
    | lhs = expression op = ('==' | '!=') rhs = expression          # BinaryExpr
    | lhs = expression op = '&' rhs = expression                    # BinaryExpr
    | lhs = expression op = '^' rhs = expression                    # BinaryExpr
    | lhs = expression op = '|' rhs = expression                    # BinaryExpr
    | lhs = expression op = '&&' rhs = expression                   # BinaryExpr
    | lhs = expression op = '||' rhs = expression                   # BinaryExpr
    | lhs = expression op = '&&' rhs = expression                   # BinaryExpr

    | <assoc = right> lhs = expression op = '=' rhs = expression    # AssignExpr

    | Identifier                                                    # IdentifierExpr
    | This                                                          # ThisExpr
    | constant                                                      # ConstantExpr
    | '(' expression ')'                                            # SubExpr
    ;

    constant
        : ConstIntenger     # ConstInt
        | ConstString       # ConstStr
        | ConstBool         # ConstBool
        | Null              # ConstNull
        ;

    paramentList
        : expression (',' expression)*
        ;

    creator
        : nonVoidnonArrayType ('[' expression ']')+ ('['']')*   # ArrayCreator
        | nonVoidnonArrayType                                   # NonArrayCreator
        ;
// ===Lexer===

Bool: 'bool';
Int: 'int';
String: 'string';
Null: 'null';
Void: 'void';
True: 'true';
False: 'false';
For: 'for';
If: 'if';
Else: 'else';
While: 'while';
Break: 'break';
Continue: 'continue';
Return: 'return';
New: 'new';
Class: 'class';
This: 'this';

ConstIntenger
    : '0'
    | [1-9] Digit*
    ;

ConstString
    : '"' (EscapeCharacter | .)*? '"'
    ;

    fragment EscapeCharacter
        : '\\' [btnr"\\]
        ;

ConstBool
    : True
    | False
    ;

Identifier
    : Character (NonDigit | Digit)*
    ;

    fragment Character
        :   [a-zA-Z]
        ;

    fragment NonDigit
        :   [a-zA-Z_]
        ;

    fragment Digit
        :   [0-9]
        ;


Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;