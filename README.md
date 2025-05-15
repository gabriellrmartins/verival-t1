# Trabalho T1: Verival - Classificador de Triângulos (BeeCrowd 1045)

Este projeto implementa e testa um classificador de triângulos baseado no problema 1045 do BeeCrowd ("Tipos de Triângulos"). O objetivo é aplicar diversas técnicas de teste de software para garantir a qualidade e a corretude da solução.

**Link para o problema no BeeCrowd:** [Problema 1045 - Tipos de Triângulos](https://www.beecrowd.com.br/judge/pt/problems/view/1045)

## Integrantes
* Gabriel Levasseur Rocha Martins 
* Matheus Melo

## Sistema Sob Teste
A classe principal do sistema sob teste é `com.example.triangle.TriangleClassifier`. Ela recebe três valores `double` como entrada, representando os lados de um triângulo, e retorna uma lista de strings com as classificações do triângulo, conforme as regras:
1.  Os lados são ordenados em ordem decrescente (A, B, C, onde A é o maior).
2.  Se `A >= B + C` ou se algum lado for não positivo (considerando uma pequena tolerância `EPSILON`), é classificado como "NAO FORMA TRIANGULO".
3.  Caso contrário, é classificado quanto ao ângulo:
    * `A^2` "quase igual" a `B^2 + C^2`: TRIANGULO RETANGULO
    * `A^2` "claramente maior" que `B^2 + C^2`: TRIANGULO OBTUSANGULO
    * `A^2` "claramente menor" que `B^2 + C^2`: TRIANGULO ACUTANGULO
4.  Adicionalmente, é classificado quanto aos lados (se formar triângulo):
    * Todos os lados "quase iguais": TRIANGULO EQUILATERO
    * Exatamente dois lados "quase iguais": TRIANGULO ISOSCELES

## Estrutura do Projeto
O projeto utiliza Maven para gerenciamento de dependências e build.
* Código fonte: `src/main/java/com/example/triangle/TriangleClassifier.java`
* Código de teste: `src/test/java/com/example/triangle/TriangleClassifierTest.java`
* Arquivo de configuração Maven: `pom.xml`

## Como Compilar e Executar os Testes
1.  Certifique-se de ter o JDK (versão 11 ou superior) e o Maven instalados.
2.  Clone este repositório.
3.  Navegue até a raiz do projeto (`triangle-classifier`) no terminal.
4.  Execute o comando Maven para limpar, compilar e rodar os testes:
    ```bash
    mvn clean test
    ```
5.  Após a execução, o relatório de cobertura do JaCoCo estará disponível em `target/site/jacoco/index.html`.

## Técnicas de Teste Aplicadas
Neste trabalho, foram aplicadas as seguintes quatro técnicas de teste:

1.  **Particionamento de Equivalência:**
    * **Descrição:** As entradas (lados do triângulo) foram particionadas com base na capacidade de formar um triângulo e, caso formem, no tipo de ângulo e de lado. As partições de entrada incluem:
        * Lados que não formam triângulo (ex: `a >= b+c`, lado zero, lado negativo).
        * Lados que formam triângulos válidos.
    As partições de saída incluem as classificações: "NAO FORMA TRIANGULO", "TRIANGULO RETANGULO", "TRIANGULO OBTUSANGULO", "TRIANGULO ACUTANGULO", "TRIANGULO EQUILATERO", "TRIANGULO ISOSCELES", e combinações válidas destas.
    * **Resultados dos Testes:** Foram criados casos de teste para cada partição identificada (ver `TriangleClassifierTest.java`, método `triangleTestData()`). Por exemplo:
        * `(7.0, 2.0, 3.0)` -> "NAO FORMA TRIANGULO"
        * `(6.0, 6.0, 6.0)` -> ["TRIANGULO ACUTANGULO", "TRIANGULO EQUILATERO"]
        * `(3.0, 4.0, 5.0)` -> ["TRIANGULO RETANGULO"]
    Todos os testes baseados em partição passaram após os ajustes de `EPSILON`.
    * **Injeção de Falha e Detecção:**
        * **Falha Injetada:** Alterou-se a condição de "TRIANGULO EQUILATERO" em `TriangleClassifier.java` de `(aEqualsB && bEqualsC)` para `(aEqualsB && !bEqualsC)` (uma condição que nunca seria verdadeira para equilátero e incorreta para isósceles).
        * **Detecção:** O caso de teste para triângulo equilátero, como `Arguments.of(6.0, 6.0, 6.0, List.of("TRIANGULO ACUTANGULO", "TRIANGULO EQUILATERO"), ...)` falhou, pois a classificação "TRIANGULO EQUILATERO" não foi retornada.

2.  **Análise de Valor Limite:**
    * **Descrição:** Foram testados valores nos limites das condições de decisão:
        * Lados onde `a` é exatamente `b+c`.
        * Lados onde `a` é ligeiramente maior ou menor que `b+c`.
        * Lados onde `a^2` é exatamente `b^2+c^2`.
        * Lados onde `a^2` é ligeiramente maior ou menor que `b^2+c^2`.
        * Lados muito pequenos (próximos de zero, mas positivos).
        * Lados iguais a zero.
    * **Resultados dos Testes:** Casos de teste específicos foram criados para estes limites (ver `TriangleClassifierTest.java`, método `triangleTestData()`). Por exemplo:
        * `(7.0, 5.0, 2.0)` (A = B+C) -> "NAO FORMA TRIANGULO"
        * `(5.0 - DELTA, 3.0, 2.0)` (A ligeiramente < B+C) -> ["TRIANGULO OBTUSANGULO"]
        * `(1.0, Math.sqrt(2.0), 1.0)` (A^2 = B^2+C^2) -> ["TRIANGULO RETANGULO", "TRIANGULO ISOSCELES"]
    Todos os testes de valor limite passaram após os ajustes de `EPSILON` e do caso de teste dos lados muito pequenos.
    * **Injeção de Falha e Detecção:**
        * **Falha Injetada:** Modificou-se a condição `a >= (b + c) - EPSILON` para `a > (b + c) + EPSILON` na verificação de "NAO FORMA TRIANGULO".
        * **Detecção:** O teste de limite `Arguments.of(7.0, 5.0, 2.0, List.of("NAO FORMA TRIANGULO"), ...)` (onde `a = b+c`) falhou, pois a lógica defeituosa passou a classificar este caso como formando um triângulo.

3.  **Cobertura de Código (utilizando JaCoCo):**
    * **Descrição:** O plugin JaCoCo foi configurado no `pom.xml` para gerar relatórios de cobertura de código após a execução dos testes. O objetivo é garantir que uma alta percentagem das instruções e ramos da classe `TriangleClassifier` sejam executados pelos testes, identificando partes do código não testadas.
    * **Resultados dos Testes:** Após a execução de `mvn clean test`, o relatório em `target/site/jacoco/index.html` mostrou [INSIRA AQUI A SUA COBERTURA, ex: 100% de instruções e 95% de ramos para a classe `TriangleClassifier`]. (Anexe um print do relatório na Wiki/Issues e referencie aqui).
    * **Injeção de Falha e Detecção (Demonstração):**
        * **Falha Injetada:** Adicionou-se um bloco `if (a > 1000000) { /* lógica de classificação alternativa e errada */ }` que não era coberto pelos testes existentes.
        * **Detecção:** O relatório JaCoCo indicaria que este novo bloco `if` não foi coberto. Se este bloco contivesse uma falha funcional, e não houvesse testes para valores de `a` tão grandes, a falha não seria detetada pelos testes de funcionalidade. A ferramenta de cobertura aponta para a necessidade de testes adicionais para essa condição ou para a remoção de código morto.

4.  **Testes Baseados em Contratos (simulados com JUnit):**
    * **Descrição:** Foram definidos testes para verificar propriedades e invariantes que a função `classify` deve manter:
        * A lista de saída nunca deve ser nula.
        * Se "NAO FORMA TRIANGULO" é retornado, deve ser a única classificação.
        * Se um triângulo é formado, deve haver exatamente uma classificação de ângulo.
        * A ordem das classificações (ângulo primeiro, depois tipo de lado) deve ser respeitada.
    * **Resultados dos Testes:** Foram criados métodos de teste específicos em `TriangleClassifierTest.java` para cada contrato (ex: `testContratoSaidaNaoNulaOuVazia()`, `testContratoUmaClassificacaoAngulo()`). Todos estes testes passaram.
    * **Injeção de Falha e Detecção:**
        * **Falha Injetada:** Comentou-se a linha `classifications.add("TRIANGULO RETANGULO");` dentro da lógica de classificação de ângulo.
        * **Detecção:** O teste `testContratoUmaClassificacaoAngulo()` falhou quando executado com entradas para um triângulo retângulo, pois a contagem de classificações de ângulo retornada foi 0 em vez de 1. O teste específico para triângulo retângulo (ex: 3,4,5) também falharia ao não encontrar a classificação esperada.

## Resultados Gerais dos Testes
* Todos os 25 testes unitários desenvolvidos na suite `TriangleClassifierTest` estão passando.
* A cobertura de código para a classe `TriangleClassifier`, medida pelo JaCoCo, é de [INSIRA AQUI A SUA COBERTURA, ex: 100% de instruções e X% de ramos].
* A combinação das técnicas de teste e a subsequente depuração (especialmente em relação ao `EPSILON` para comparações de ponto flutuante) resultaram numa suíte de testes robusta, capaz de detetar diversas falhas injetadas, aumentando a confiança na corretude do classificador de triângulos.

## Evidências e Documentação Adicional
* **Wiki do Projeto:** [Link para a Wiki do GitHub do seu projeto aqui]
* **Issues do Projeto:** [Link para as Issues do GitHub do seu projeto aqui]

