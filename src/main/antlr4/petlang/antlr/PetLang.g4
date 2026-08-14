grammar PetLang;


program
    : statement* EOF
    ;

statement
    : varDecl
    | assignment
    | domainCommand
    | ifStmt
    | whileStmt
    ;

varDecl
    : type ID '=' expr
    ;

assignment
    : ID '=' expr
    ;

type
    : INT_T
    | FLOAT_T
    | STRING_T
    ;

domainCommand
    : FEED ID expr
    | VET ID STRING_LIT
    ;

ifStmt
    : IF expr block (ELSE block)?
    ;

whileStmt
    : WHILE expr block
    ;

block
    : '{' statement* '}'
    ;

expr
    : '(' expr ')'                                        # ParenExpr
    | expr op=('*'|'/') expr                              # MulDivExpr
    | expr op=('+'|'-') expr                              # AddSubExpr
    | expr op=('>'|'<'|'>='|'<='|'=='|'!=') expr          # CompareExpr
    | expr op=('&&'|'||') expr                            # LogicExpr
    | INT_LIT                                             # IntLitExpr
    | FLOAT_LIT                                           # FloatLitExpr
    | STRING_LIT                                          # StringLitExpr
    | ID                                                  # VarRefExpr
    ;



IF       : 'if';
ELSE     : 'else';
WHILE    : 'while';
INT_T    : 'int';
FLOAT_T  : 'float';
STRING_T : 'string';
FEED     : 'feed';
VET      : 'vet';
AND      : '&&';
OR       : '||';

ID         : [a-zA-Z_][a-zA-Z_0-9]*;

FLOAT_LIT  : [0-9]+ '.' [0-9]+;
INT_LIT    : [0-9]+;
STRING_LIT : '"' (~["\r\n])* '"';

WS         : [ \t\r\n]+ -> skip;
COMMENT    : '//' ~[\r\n]* -> skip;


UNRECOGNIZED : . ;