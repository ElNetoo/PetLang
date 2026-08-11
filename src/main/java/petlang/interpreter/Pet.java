package petlang.interpreter;

import java.util.ArrayList;
import java.util.List;

public class Pet {
    public String nome;
    public String especie;
    public int idade;
    public double peso;
    public List<Double> alimentacoes = new ArrayList<>();
    public List<String> consultas = new ArrayList<>();

    public Pet(String nome) {
        this.nome = nome;
        this.especie = "desconhecida";
        this.idade = 0;
        this.peso = 0.0;
    }

    public String statusPeso() {
        double[] faixa = faixaPesoIdeal();
        if (peso == 0.0){
            return "indefinido";
        } 

        if (peso < faixa[0]){
            return "abaixo";
        }

        if (peso > faixa[1]){
            return "acima";
        }
        return "ideal";
    }

    public double[] faixaPesoIdeal() {
        if (especie.equalsIgnoreCase("gato")){
            return new double[]{3.5, 5.0};
        }

        if (especie.equalsIgnoreCase("cachorro")){
            return new double[]{8.0, 15.0};
        }

        return new double[]{0.0, 9999.0};
    }

    public String statusRacao() {
        if (alimentacoes.isEmpty()){
            return "sem registro";
        }

        double media = alimentacoes.stream().mapToDouble(d -> d).average().orElse(0);
        double[] faixa = faixaRacaoIdeal();

        if (media < faixa[0]){
            return "insuficiente";
        }

        if (media > faixa[1]){
            return "excessiva";
        }

        return "adequada";
    }

    public double mediaRacao() {
        if (alimentacoes.isEmpty()){
            return 0;
        }

        return alimentacoes.stream().mapToDouble(d -> d).average().orElse(0);
    }

    public double[] faixaRacaoIdeal() {
        if (especie.equalsIgnoreCase("gato")){
            return new double[]{60.0, 100.0};
        }

        if (especie.equalsIgnoreCase("cachorro")){
            return new double[]{120.0, 200.0};
        }

        return new double[]{0.0, 9999.0};
    }

    public boolean isIdoso() {
        if (especie.equalsIgnoreCase("cachorro")) {
            return idade > 7;
        }

        if (especie.equalsIgnoreCase("gato")){
            return idade > 10;
        }

        return false;
    }

    public String statusIdade() {
        if (idade == 0) {
            return "indefinida";
        }

        if (isIdoso()) {
            return "idoso";
        } 
        
        return "adulto";
    }
}