package petlang.interpreter;

import java.util.ArrayList;
import java.util.List;

public class Pet {
    public String nome;
    public int idade;
    public double peso;
    public List<String> alimentacoes = new ArrayList<>();
    public List<String> consultas    = new ArrayList<>();

    public Pet(String nome) {
        this.nome  = nome;
        this.idade = 0;
        this.peso  = 0.0;
    }
}