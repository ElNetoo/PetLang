package petlang.semantic;

import petlang.antlr.PetLangBaseVisitor;
import petlang.antlr.PetLangParser;

public class SemanticAnalyzer extends PetLangBaseVisitor<SymbolTable.Type> {

    private final SymbolTable symbolTable = new SymbolTable();

    @Override
    public SymbolTable.Type visitVarDecl(PetLangParser.VarDeclContext ctx) {
        String name     = ctx.ID().getText();
        String typeName = ctx.type().getText();
        SymbolTable.Type type = parseType(typeName, ctx.getStart().getLine());

        SymbolTable.Type exprType = visit(ctx.expr());
        checkTypeCompatibility(type, exprType, name, ctx.getStart().getLine());

        symbolTable.declare(name, type, ctx.getStart().getLine());
        return null;
    }

    @Override
    public SymbolTable.Type visitAssignment(PetLangParser.AssignmentContext ctx) {
        String name     = ctx.ID().getText();
        SymbolTable.Type declared = symbolTable.lookup(name, ctx.getStart().getLine());
        SymbolTable.Type exprType = visit(ctx.expr());
        checkTypeCompatibility(declared, exprType, name, ctx.getStart().getLine());
        return null;
    }

    @Override
    public SymbolTable.Type visitDomainCommand(PetLangParser.DomainCommandContext ctx) {
        String cmd  = ctx.getStart().getText();
        String id   = ctx.ID().getText();
        int    line = ctx.getStart().getLine();

        symbolTable.lookup(id, line);

        if (cmd.equals("feed")) {
            SymbolTable.Type qType = visit(ctx.expr());
            if (qType == SymbolTable.Type.STRING) {
                throw new RuntimeException(
                    "[Erro Semântico] Linha " + line +
                    ": 'feed' espera um valor numérico como quantidade, recebido string."
                );
            }
        }
        return null;
    }

    @Override
    public SymbolTable.Type visitIfStmt(PetLangParser.IfStmtContext ctx) {
        visit(ctx.expr());
        ctx.block().forEach(this::visit);
        return null;
    }

    @Override
    public SymbolTable.Type visitWhileStmt(PetLangParser.WhileStmtContext ctx) {
        visit(ctx.expr());
        visit(ctx.block());
        return null;
    }

    @Override
    public SymbolTable.Type visitIntLitExpr(PetLangParser.IntLitExprContext ctx) {
        return SymbolTable.Type.INT;
    }

    @Override
    public SymbolTable.Type visitFloatLitExpr(PetLangParser.FloatLitExprContext ctx) {
        return SymbolTable.Type.FLOAT;
    }

    @Override
    public SymbolTable.Type visitStringLitExpr(PetLangParser.StringLitExprContext ctx) {
        return SymbolTable.Type.STRING;
    }

    @Override
    public SymbolTable.Type visitVarRefExpr(PetLangParser.VarRefExprContext ctx) {
        String name = ctx.ID().getText();
        return symbolTable.lookup(name, ctx.getStart().getLine());
    }

    @Override
    public SymbolTable.Type visitParenExpr(PetLangParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public SymbolTable.Type visitMulDivExpr(PetLangParser.MulDivExprContext ctx) {
        return checkArithmeticOp(ctx.expr(0), ctx.expr(1), ctx.getStart().getLine());
    }

    @Override
    public SymbolTable.Type visitAddSubExpr(PetLangParser.AddSubExprContext ctx) {
        return checkArithmeticOp(ctx.expr(0), ctx.expr(1), ctx.getStart().getLine());
    }

    @Override
    public SymbolTable.Type visitCompareExpr(PetLangParser.CompareExprContext ctx) {
        SymbolTable.Type left  = visit(ctx.expr(0));
        SymbolTable.Type right = visit(ctx.expr(1));
        if (left == SymbolTable.Type.STRING || right == SymbolTable.Type.STRING) {
            throw new RuntimeException(
                "[Erro Semântico] Linha " + ctx.getStart().getLine() +
                ": não é possível comparar string com operadores numéricos."
            );
        }
        return SymbolTable.Type.INT;
    }



    private SymbolTable.Type checkArithmeticOp(
            PetLangParser.ExprContext left,
            PetLangParser.ExprContext right,
            int line) {
        SymbolTable.Type l = visit(left);
        SymbolTable.Type r = visit(right);
        if (l == SymbolTable.Type.STRING || r == SymbolTable.Type.STRING) {
            throw new RuntimeException(
                "[Erro Semântico] Linha " + line +
                ": operação aritmética não é permitida com strings."
            );
        }
        return (l == SymbolTable.Type.FLOAT || r == SymbolTable.Type.FLOAT)
                ? SymbolTable.Type.FLOAT
                : SymbolTable.Type.INT;
    }

    private void checkTypeCompatibility(
            SymbolTable.Type expected,
            SymbolTable.Type actual,
            String name,
            int line) {
        if (actual == null) return;
        if (expected == SymbolTable.Type.FLOAT && actual == SymbolTable.Type.INT) return;
        if (expected != actual) {
            throw new RuntimeException(
                "[Erro Semântico] Linha " + line + ": tipo incompatível para '" + name +
                "'. Esperado: " + expected + ", recebido: " + actual + "."
            );
        }
    }

    private SymbolTable.Type parseType(String typeName, int line) {
        return switch (typeName) {
            case "int"    -> SymbolTable.Type.INT;
            case "float"  -> SymbolTable.Type.FLOAT;
            case "string" -> SymbolTable.Type.STRING;
            default -> throw new RuntimeException(
                "[Erro Semântico] Linha " + line + ": tipo desconhecido '" + typeName + "'."
            );
        };
    }
}