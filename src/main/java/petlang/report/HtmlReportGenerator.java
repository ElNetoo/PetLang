package petlang.report;

import petlang.interpreter.Pet;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlReportGenerator {

    public static void generate(List<Pet> pets, String outputPath) throws IOException {
        String template = lerRecurso("template.html");
        String cardTemplate = lerRecurso("card.html");

        StringBuilder cards = new StringBuilder();
        for (Pet pet : pets) {
            cards.append(renderCard(pet, cardTemplate));
        }

        String html = template.replace("{{CARDS}}", cards.toString());
        Files.writeString(Path.of(outputPath), html);
        System.out.println("Relatório gerado: " + outputPath);
    }

    private static String renderCard(Pet pet, String cardTemplate) {
        double[] faixaPeso = pet.faixaPesoIdeal();
        double[] faixaRac  = pet.faixaRacaoIdeal();
        String stPeso  = pet.statusPeso();
        String stRacao = pet.statusRacao();
        String stIdade = pet.statusIdade();

        String card = cardTemplate;
        
        card = card.replace("{{ICONE}}",   resolverIcone(pet.especie));
        card = card.replace("{{NOME}}",    pet.nome);
        card = card.replace("{{ESPECIE}}", pet.especie);
   
        card = card.replace("{{CLASSE_PESO}}", classePeso(stPeso));
        card = card.replace("{{VALOR_PESO}}",  pet.peso > 0 ? String.format("%.1fkg", pet.peso) : "--");
        card = card.replace("{{TEXTO_PESO}}",  textoPeso(stPeso));

        card = card.replace("{{CLASSE_RACAO}}", classeRacao(stRacao));
        card = card.replace("{{VALOR_RACAO}}",  pet.alimentacoes.isEmpty() ? "--" : String.format("%.1fg", pet.mediaRacao()));
        card = card.replace("{{TEXTO_RACAO}}",  textoRacao(stRacao));
        card = card.replace("{{CLASSE_IDADE}}", classeIdade(stIdade));
        card = card.replace("{{VALOR_IDADE}}",  pet.idade > 0 ? pet.idade + " anos" : "--");
        card = card.replace("{{TEXTO_IDADE}}",  textoIdade(stIdade));
        card = card.replace("{{ALERTAS}}", renderAlertas(pet, stPeso, stRacao, faixaPeso, faixaRac));
        card = card.replace("{{TABELA_ALIMENTACOES}}", renderAlimentacoes(pet, faixaRac));
        card = card.replace("{{CONSULTAS}}", renderConsultas(pet));

        return card;
    }
    private static String renderAlertas(Pet pet, String stPeso, String stRacao,
                                        double[] faixaPeso, double[] faixaRac) {
        StringBuilder sb = new StringBuilder();

        if (stPeso.equals("abaixo")) {
            sb.append("<div class='alerta critico'>")
              .append("⚠️ Peso abaixo do ideal para ").append(pet.especie)
              .append(". Faixa recomendada: ").append(faixaPeso[0]).append("kg – ").append(faixaPeso[1])
              .append("kg. Considere aumentar a ração e consultar um veterinário.")
              .append("</div>");
        } else if (stPeso.equals("acima")) {
            sb.append("<div class='alerta critico'>")
              .append("⚠️ Peso acima do ideal para ").append(pet.especie)
              .append(". Faixa recomendada: ").append(faixaPeso[0]).append("kg – ").append(faixaPeso[1])
              .append("kg. Considere reduzir a ração e consultar um veterinário.")
              .append("</div>");
        } else if (stPeso.equals("ideal")) {
            sb.append("<div class='alerta ok'>")
              .append("✅ Peso dentro da faixa ideal para ").append(pet.especie)
              .append(" (").append(faixaPeso[0]).append("kg – ").append(faixaPeso[1]).append("kg).")
              .append("</div>");
        }

        if (stRacao.equals("insuficiente")) {
            sb.append("<div class='alerta'>")
              .append("🟡 Quantidade de ração abaixo do recomendado para ").append(pet.especie)
              .append(". Ideal: ").append(faixaRac[0]).append("g – ").append(faixaRac[1]).append("g por refeição.")
              .append("</div>");
        } else if (stRacao.equals("excessiva")) {
            sb.append("<div class='alerta critico'>")
              .append("🔴 Quantidade de ração acima do recomendado para ").append(pet.especie)
              .append(". Ideal: ").append(faixaRac[0]).append("g – ").append(faixaRac[1]).append("g por refeição.")
              .append("</div>");
        }

        if (pet.isIdoso()) {
            sb.append("<div class='alerta'>")
              .append("🟡 Pet idoso — recomenda-se check-up veterinário semestral.")
              .append("</div>");
        }

        return sb.toString();
    }

    private static String renderAlimentacoes(Pet pet, double[] faixaRac) {
        if (pet.alimentacoes.isEmpty()) {
            return "<span class='empty'>Nenhuma alimentação registrada.</span>";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        sb.append("<tr><th>#</th><th>Quantidade</th><th>Status</th></tr>");

        for (int i = 0; i < pet.alimentacoes.size(); i++) {
            double q = pet.alimentacoes.get(i);
            String status;
            if (q < faixaRac[0]) {
                status = "🟡 Insuficiente";
            } else if (q > faixaRac[1]) {
                status = "🔴 Excessiva";
            } else {
                status = "✅ Adequada";
            }
            sb.append("<tr>")
              .append("<td>").append(i + 1).append("</td>")
              .append("<td>").append(String.format("%.1fg", q)).append("</td>")
              .append("<td>").append(status).append("</td>")
              .append("</tr>");
        }

        sb.append("</table>");
        return sb.toString();
    }

    private static String renderConsultas(Pet pet) {
        if (pet.consultas.isEmpty()) {
            return "<span class='empty'>Nenhuma consulta registrada.</span>";
        }

        StringBuilder sb = new StringBuilder();
        for (String c : pet.consultas) {
            sb.append("<span class='badge'>").append(c).append("</span> ");
        }
        return sb.toString();
    }


    private static String classePeso(String status) {
        if (status.equals("ideal"))  return "verde";
        if (status.equals("abaixo")) return "vermelho";
        if (status.equals("acima"))  return "vermelho";
        return "cinza";
    }

    private static String textoPeso(String status) {
        if (status.equals("ideal"))  return "✅ Ideal";
        if (status.equals("abaixo")) return "🔴 Abaixo";
        if (status.equals("acima"))  return "🔴 Acima";
        return "⚪ N/A";
    }

    private static String classeRacao(String status) {
        if (status.equals("adequada"))     return "verde";
        if (status.equals("insuficiente")) return "amarelo";
        if (status.equals("excessiva"))    return "vermelho";
        return "cinza";
    }

    private static String textoRacao(String status) {
        if (status.equals("adequada"))     return "✅ Adequada";
        if (status.equals("insuficiente")) return "🟡 Insuficiente";
        if (status.equals("excessiva"))    return "🔴 Excessiva";
        return "⚪ N/A";
    }

    private static String classeIdade(String status) {
        if (status.equals("adulto")) return "verde";
        if (status.equals("idoso"))  return "amarelo";
        return "cinza";
    }

    private static String textoIdade(String status) {
        if (status.equals("adulto")) return "✅ Adulto";
        if (status.equals("idoso"))  return "🟡 Idoso";
        return "⚪ N/A";
    }

    private static String resolverIcone(String especie) {
        if (especie.equalsIgnoreCase("cachorro")) return "🐶";
        if (especie.equalsIgnoreCase("gato"))     return "🐱";
        return "🐾";
    }

    private static String lerRecurso(String nomeArquivo) throws IOException {
        InputStream is = HtmlReportGenerator.class
                .getClassLoader()
                .getResourceAsStream(nomeArquivo);
        if (is == null) {
            throw new IOException("Recurso não encontrado: " + nomeArquivo);
        }
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }
}