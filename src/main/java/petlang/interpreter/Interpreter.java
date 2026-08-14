package petlang.interpreter;

import petlang.antlr.PetLangBaseVisitor;
import petlang.antlr.PetLangParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter extends PetLangBaseVisitor<Object> {

    private final Map<String, Object> memory = new HashMap<>();
    private final Map<String, Pet>    pets   = new HashMap<>();
    private final List<Pet>           ordem  = new ArrayList<>();
    private boolean temComandoDominio = false;

    public List<Pet> getPets() {
        return ordem;
    }

    public boolean hasComandoDominio() {
        return temComandoDominio;
    }

    @Override
    public Object visitVarDecl(PetLangParser.VarDeclContext ctx) {
        String name  = ctx.ID().getText();
        Object value = visit(ctx.expr());
        memory.put(name, value);
        return null;
    }

    @Override
    public Object visitAssignment(PetLangParser.AssignmentContext ctx) {
        String name  = ctx.ID().getText();
        Object value = visit(ctx.expr());
        memory.put(name, value);
        return null;
    }

    @Override
    public Object visitDomainCommand(PetLangParser.DomainCommandContext ctx) {
        String cmd     = ctx.getStart().getText();
        String petName = resolvePetName(ctx.ID().getText());

        switch (cmd) {
            case "feed" -> {
                temComandoDominio = true;
                double quantidade = toDouble(visit(ctx.expr()));
                Pet pet = getOrCreatePet(petName);
                pet.alimentacoes.add(quantidade);
            }
            case "vet" -> {
                temComandoDominio = true;
                String procedimento = stripQuotes(ctx.STRING_LIT().getText());
                Pet pet = getOrCreatePet(petName);
                pet.consultas.add(procedimento);
            }
        }
        return null;
    }

    @Override
    public Object visitIfStmt(PetLangParser.IfStmtContext ctx) {
        boolean cond = isTruthy(visit(ctx.expr()));
        if (cond) {
            visit(ctx.block(0));
        } else if (ctx.block().size() > 1) {
            visit(ctx.block(1));
        }
        return null;
    }

    @Override
    public Object visitWhileStmt(PetLangParser.WhileStmtContext ctx) {
        while (isTruthy(visit(ctx.expr()))) {
            visit(ctx.block());
        }
        return null;
    }

    @Override
    public Object visitBlock(PetLangParser.BlockContext ctx) {
        ctx.statement().forEach(this::visit);
        return null;
    }

    @Override
    public Object visitLogicExpr(PetLangParser.LogicExprContext ctx) {
        boolean left = isTruthy(visit(ctx.expr(0)));
        String op    = ctx.op.getText();
        if (op.equals("&&")) {
            return left && isTruthy(visit(ctx.expr(1))) ? 1 : 0;
        } else {
            return left || isTruthy(visit(ctx.expr(1))) ? 1 : 0;
        }
    }

    @Override
    public Object visitIntLitExpr(PetLangParser.IntLitExprContext ctx) {
        return Integer.parseInt(ctx.getText());
    }

    @Override
    public Object visitFloatLitExpr(PetLangParser.FloatLitExprContext ctx) {
        return Double.parseDouble(ctx.getText());
    }

    @Override
    public Object visitStringLitExpr(PetLangParser.StringLitExprContext ctx) {
        return stripQuotes(ctx.getText());
    }

    @Override
    public Object visitVarRefExpr(PetLangParser.VarRefExprContext ctx) {
        return memory.get(ctx.ID().getText());
    }

    @Override
    public Object visitParenExpr(PetLangParser.ParenExprContext ctx) {
        return visit(ctx.expr());
    }

    @Override
    public Object visitMulDivExpr(PetLangParser.MulDivExprContext ctx) {
        double l = toDouble(visit(ctx.expr(0)));
        double r = toDouble(visit(ctx.expr(1)));
        return ctx.op.getText().equals("*") ? l * r : l / r;
    }

    @Override
    public Object visitAddSubExpr(PetLangParser.AddSubExprContext ctx) {
        double l = toDouble(visit(ctx.expr(0)));
        double r = toDouble(visit(ctx.expr(1)));
        return ctx.op.getText().equals("+") ? l + r : l - r;
    }

    @Override
    public Object visitCompareExpr(PetLangParser.CompareExprContext ctx) {
        double lv = toDouble(visit(ctx.expr(0)));
        double rv = toDouble(visit(ctx.expr(1)));
        return switch (ctx.op.getText()) {
            case ">"  -> lv > rv  ? 1 : 0;
            case "<"  -> lv < rv  ? 1 : 0;
            case ">=" -> lv >= rv ? 1 : 0;
            case "<=" -> lv <= rv ? 1 : 0;
            case "==" -> lv == rv ? 1 : 0;
            case "!=" -> lv != rv ? 1 : 0;
            default   -> 0;
        };
    }

    private String resolvePetName(String idOrVar) {
        Object val = memory.get(idOrVar);
        if (val == null) return idOrVar;

        String nomePet = val.toString();

        Object especieVal = memory.get("especie");
        if (especieVal != null) {
            Pet pet = getOrCreatePet(nomePet);
            pet.especie = especieVal.toString();
        }

        Object idadeVal = memory.get("idade");
        if (idadeVal != null) {
            Pet pet = getOrCreatePet(nomePet);
            pet.idade = toInt(idadeVal);
        }

        Object pesoVal = memory.get("peso");
        if (pesoVal != null) {
            Pet pet = getOrCreatePet(nomePet);
            pet.peso = toDouble(pesoVal);
        }

        return nomePet;
    }

    private Pet getOrCreatePet(String nome) {
        return pets.computeIfAbsent(nome, n -> {
            Pet p = new Pet(n);
            ordem.add(p);
            return p;
        });
    }

    private boolean isTruthy(Object val) {
        if (val instanceof Integer i) return i != 0;
        if (val instanceof Double  d) return d != 0;
        return false;
    }

    private double toDouble(Object val) {
        if (val instanceof Integer i) return i.doubleValue();
        if (val instanceof Double  d) return d;
        throw new RuntimeException("Valor não numérico: " + val);
    }

    private int toInt(Object val) {
        if (val instanceof Integer i) return i;
        if (val instanceof Double  d) return d.intValue();
        return 0;
    }

    private String stripQuotes(String s) {
        return s.substring(1, s.length() - 1);
    }
}