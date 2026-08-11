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
        lexer.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                System.err.println("[Erro Léxico] Linha " + line + ":" + charPositionInLine + " - " + msg);
                System.exit(1);
            }
        });

        CommonTokenStream tokens = new CommonTokenStream(lexer);

        PetLangParser parser = new PetLangParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine, String msg,
                                    RecognitionException e) {
                System.err.println("[Erro Sintático] Linha " + line + ":" + charPositionInLine + " - " + msg);
                System.exit(1);
            }
        });

        ParseTree tree = parser.program();
        System.out.println("Análise léxica e sintática: OK");


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