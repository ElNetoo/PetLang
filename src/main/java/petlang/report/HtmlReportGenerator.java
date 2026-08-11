package petlang.report;

import petlang.interpreter.Pet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class HtmlReportGenerator {

    public static void generate(List<Pet> pets, String outputPath) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <title>PetLang - Relatório de Cuidados</title>
                <style>
                    body { font-family: Arial, sans-serif; background: #f9f3ff; padding: 2rem; }
                    h1   { color: #5b2d8e; }
                    h2   { color: #7b4dab; margin-top: 2rem; }
                    .card {
                        background: white;
                        border-radius: 12px;
                        padding: 1.5rem;
                        margin-bottom: 2rem;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.1);
                    }
                    table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
                    th { background: #5b2d8e; color: white; padding: 10px; text-align: left; }
                    td { padding: 10px; border-bottom: 1px solid #eee; }
                    tr:hover { background: #f3e9ff; }
                    .badge {
                        display: inline-block;
                        background: #7b4dab;
                        color: white;
                        border-radius: 20px;
                        padding: 2px 12px;
                        font-size: 0.85rem;
                        margin: 2px;
                    }
                    .empty { color: #999; font-style: italic; }
                </style>
            </head>
            <body>
                <h1>🐾 PetLang — Relatório de Cuidados</h1>
            """);

        for (Pet pet : pets) {
            sb.append("<div class='card'>");
            sb.append("<h2>🐶 ").append(pet.nome).append("</h2>");
            sb.append("<strong>🍖 Alimentações registradas:</strong><br>");
            if (pet.alimentacoes.isEmpty()) {
                sb.append("<span class='empty'>Nenhuma alimentação registrada.</span>");
            } else {
                sb.append("<table><tr><th>#</th><th>Quantidade</th></tr>");
                for (int i = 0; i < pet.alimentacoes.size(); i++) {
                    sb.append("<tr><td>").append(i + 1).append("</td>")
                      .append("<td>").append(pet.alimentacoes.get(i)).append("</td></tr>");
                }
                sb.append("</table>");
            }

            sb.append("<br><strong>🏥 Consultas veterinárias:</strong><br>");
            if (pet.consultas.isEmpty()) {
                sb.append("<span class='empty'>Nenhuma consulta registrada.</span>");
            } else {
                for (String c : pet.consultas) {
                    sb.append("<span class='badge'>").append(c).append("</span> ");
                }
            }

            sb.append("</div>");
        }

        sb.append("</body></html>");

        Files.writeString(Path.of(outputPath), sb.toString());
        System.out.println("Relatório gerado: " + outputPath);
    }
}