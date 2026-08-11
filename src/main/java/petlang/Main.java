package petlang;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import petlang.antlr.PetLangLexer;
import petlang.antlr.PetLangParser;
import petlang.interpreter.Interpreter;
import petlang.report.HtmlReportGenerator;
import petlang.semantic.SemanticAnalyzer;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Uso: java -jar petlang.jar <arquivo.pet>");
            return;
        }

        String source = Files.readString(Path.of(args[0]));
        CharStream input = CharStreams.fromString(source);

        PetLangLexer lexer = new PetLangLexer(input);
        lexer.removeErrorListeners();

        final boolean[] lexError = {false};
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                System.err.println("[Erro Léxico] Linha " + line + ":" + charPositionInLine + " - " + msg);
                lexError[0] = true;
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill(); 


        for (Token token : tokens.getTokens()) {
            if (token.getType() == PetLangLexer.UNRECOGNIZED) {
                System.err.println("[Erro Léxico] Linha " + token.getLine() +
                        ":" + token.getCharPositionInLine() +
                        " - caractere inválido: '" + token.getText() + "'");
                lexError[0] = true;
            }
        }

        if (lexError[0]) System.exit(1);
        System.out.println("Análise léxica: OK");

        PetLangParser parser = new PetLangParser(tokens);
        parser.removeErrorListeners();

        final boolean[] syntaxError = {false};
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                System.err.println("[Erro Sintático] Linha " + line + ":" + charPositionInLine + " - " + msg);
                syntaxError[0] = true;
            }
        });

        ParseTree tree = parser.program();

        if (syntaxError[0]) System.exit(1);
        System.out.println("Análise sintática: OK");

        try {
            new SemanticAnalyzer().visit(tree);
            System.out.println("Análise semântica: OK");
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Interpreter interpreter = new Interpreter();
        interpreter.visit(tree);
        System.out.println("Interpretação: OK");
        HtmlReportGenerator.generate(interpreter.getPets(), "relatorio.html");
    }
}