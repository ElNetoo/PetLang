# 🐾 PetLang — DSL para Cuidados com Pets

PetLang é uma linguagem de domínio específico (DSL) para registrar e analisar a rotina de cuidados de pets. Ela processa alimentações e consultas veterinárias e gera um relatório HTML com diagnóstico nutricional automático.

Desenvolvido para a disciplina de Compiladores.

---

## Autores

- Francisco Neto
- Ianny Kerlyn

---

## Como rodar o projeto

**Pré-requisitos:** Java 17+ e Maven 3.8+

```bash

mvn package
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar <arquivo.pet>
```

Após a execução, abra o `relatorio.html` gerado na raiz do projeto.

---

## Comandos de domínio

| Comando | Descrição | Exemplo |
|---|---|---|
| `feed` | Registra uma alimentação em gramas | `feed pet 150.0` |
| `vet` | Registra uma consulta veterinária | `vet pet "Check-up geriatrico"` |

---

## Exemplo de programa `.pet`

```
string pet = "Rex"
string especie = "cachorro"
int idade = 9
int dias = 0
float peso = 12.5

while dias < 7 {
    feed pet 150.0
    dias = dias + 1
}

if idade > 7 {
    feed pet 100.0
    vet pet "Check-up geriatrico"
} else {
    feed pet 150.0
}
```

---

## Casos de teste

```bash
# Válidos
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/valid/completo.pet
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/valid/segundo_teste.pet

# Inválidos
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/invalid/erro_lexico.pet
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/invalid/erro_sintatico.pet
java -jar target/petlang-compiler-1.0-SNAPSHOT-jar-with-dependencies.jar src/test/resources/invalid/erro_semantico.pet
```

---

## Gramática

```
program       → statement*
statement     → varDecl | assignment | domainCommand | ifStmt | whileStmt
varDecl       → tipo ID '=' expr
tipo          → 'int' | 'float' | 'string'
domainCommand → 'feed' ID expr | 'vet' ID STRING_LIT
ifStmt        → 'if' expr block ( 'else' block )?
whileStmt     → 'while' expr block
block         → '{' statement* '}'
expr          → ID | INT_LIT | FLOAT_LIT | STRING_LIT | expr OP expr | '(' expr ')'
OP            → '+' | '-' | '*' | '/' | '>' | '<' | '>=' | '<=' | '==' | '!='
```