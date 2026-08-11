package petlang.semantic;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {

    public enum Type { INT, FLOAT, STRING }

    private final Map<String, Type> symbols = new HashMap<>();

    public void declare(String name, Type type, int line) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException(
                "[Erro Semântico] Linha " + line + ": variável '" + name + "' já foi declarada."
            );
        }
        symbols.put(name, type);
    }

    public Type lookup(String name, int line) {
        Type type = symbols.get(name);
        if (type == null) {
            throw new RuntimeException(
                "[Erro Semântico] Linha " + line + ": variável '" + name + "' não foi declarada."
            );
        }
        return type;
    }

    public boolean isDeclared(String name) {
        return symbols.containsKey(name);
    }
}