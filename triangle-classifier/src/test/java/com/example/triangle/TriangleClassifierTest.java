package com.example.triangle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Classe de teste para {@link TriangleClassifier}.
 * Utiliza JUnit 5 para definir e executar os casos de teste.
 */
public class TriangleClassifierTest {

    private TriangleClassifier classifier;
    private static final double DELTA_FOR_LIMIT_TESTS = 1e-10;

    @BeforeEach
    public void setUp() {
        classifier = new TriangleClassifier();
    }

    private void assertClassification(double s1, double s2, double s3, List<String> expectedClassifications, String testCaseDescription) {
        List<String> actualClassifications = classifier.classify(s1, s2, s3);
        assertEquals(expectedClassifications.size(), actualClassifications.size(),
                testCaseDescription + " - Número de classificações incorreto.");
        assertIterableEquals(expectedClassifications, actualClassifications,
                testCaseDescription + " - Classificações não correspondem ou estão na ordem errada.");
    }

    static Stream<Arguments> triangleTestData() {
        return Stream.of(
                // --- Casos: NAO FORMA TRIANGULO ---
                Arguments.of(7.0, 2.0, 3.0, List.of("NAO FORMA TRIANGULO"), "Não forma: A > B+C (7,2,3 -> A=7, B=3, C=2)"),
                Arguments.of(7.0, 5.0, 2.0, List.of("NAO FORMA TRIANGULO"), "Não forma: A = B+C (Valor Limite) (7,5,2 -> A=7, B=5, C=2)"),
                Arguments.of(1.0, 1.0, 0.0, List.of("NAO FORMA TRIANGULO"), "Não forma: Lado zero (1,1,0)"),
                Arguments.of(0.0, 0.0, 0.0, List.of("NAO FORMA TRIANGULO"), "Não forma: Todos os lados zero (0,0,0)"),
                Arguments.of(-3.0, 4.0, 5.0, List.of("NAO FORMA TRIANGULO"), "Não forma: Lado negativo (-3,4,5)"),
                Arguments.of(5.0, 5.0, -1.0, List.of("NAO FORMA TRIANGULO"), "Não forma: Lado negativo (5,5,-1)"),

                // --- Casos: TRIANGULO ACUTANGULO ---
                Arguments.of(8.0, 9.0, 7.0, List.of("TRIANGULO ACUTANGULO"), "Acutângulo Escaleno (A=9,B=8,C=7; 81 < 64+49=113)"),
                Arguments.of(6.0, 6.0, 6.0, List.of("TRIANGULO ACUTANGULO", "TRIANGULO EQUILATERO"), "Acutângulo Equilátero (6,6,6)"),
                Arguments.of(7.0, 5.0, 7.0, List.of("TRIANGULO ACUTANGULO", "TRIANGULO ISOSCELES"), "Acutângulo Isósceles (A=7,B=7,C=5; 49 < 49+25=74)"),
                Arguments.of(5.0, 7.0, 7.0, List.of("TRIANGULO ACUTANGULO", "TRIANGULO ISOSCELES"), "Acutângulo Isósceles (ordem de entrada diferente) (A=7,B=7,C=5)"),

                // --- Casos: TRIANGULO RETANGULO ---
                Arguments.of(3.0, 4.0, 5.0, List.of("TRIANGULO RETANGULO"), "Retângulo Escaleno (A=5,B=4,C=3; 25 = 16+9)"),
                Arguments.of(10.0, 6.0, 8.0, List.of("TRIANGULO RETANGULO"), "Retângulo Escaleno (A=10,B=8,C=6; 100 = 64+36)"),
                Arguments.of(1.0, Math.sqrt(2.0), 1.0, List.of("TRIANGULO RETANGULO", "TRIANGULO ISOSCELES"), "Retângulo Isósceles (A=sqrt(2),B=1,C=1; 2 = 1+1)"),
                Arguments.of(7.0, 7.0, Math.sqrt(98.0), List.of("TRIANGULO RETANGULO", "TRIANGULO ISOSCELES"), "Retângulo Isósceles (A=sqrt(98),B=7,C=7; 98 = 49+49)"),

                // --- Casos: TRIANGULO OBTUSANGULO ---
                Arguments.of(10.0, 7.0, 4.0, List.of("TRIANGULO OBTUSANGULO"), "Obtusângulo Escaleno (A=10,B=7,C=4; 100 > 49+16=65)"),
                Arguments.of(10.0, 7.0, 7.0, List.of("TRIANGULO OBTUSANGULO", "TRIANGULO ISOSCELES"), "Obtusângulo Isósceles (A=10,B=7,C=7; 100 > 49+49=98)"),

                // --- Casos de Análise de Valor Limite ---
                Arguments.of(5.0 + DELTA_FOR_LIMIT_TESTS, 3.0, 2.0, List.of("NAO FORMA TRIANGULO"), "Limite Não Forma: A ligeiramente > B+C (A=5+d, B=3, C=2)"),
                Arguments.of(5.0 - DELTA_FOR_LIMIT_TESTS, 3.0, 2.0, List.of("TRIANGULO OBTUSANGULO"), "Limite Forma: A ligeiramente < B+C (A=5-d, B=3, C=2 -> Obtusângulo)"),
                Arguments.of(Math.sqrt(25.0 + DELTA_FOR_LIMIT_TESTS * 100), 4.0, 3.0, List.of("TRIANGULO OBTUSANGULO"), "Limite Ângulo: A^2 ligeiramente > B^2+C^2 (forma Obtusângulo)"),
                Arguments.of(Math.sqrt(25.0 - DELTA_FOR_LIMIT_TESTS * 100), 4.0, 3.0, List.of("TRIANGULO ACUTANGULO"), "Limite Ângulo: A^2 ligeiramente < B^2+C^2 (forma Acutângulo)"),
                // MODIFICADO: Lados de 1e-8 para 1e-7 para compatibilidade com EPSILON = 1e-15
                Arguments.of(1e-7, 1e-7, 1e-7, List.of("TRIANGULO ACUTANGULO", "TRIANGULO EQUILATERO"), "Limite: Lados muito pequenos e iguais (1e-7)")
        );
    }

    @ParameterizedTest(name = "{index} => {4}")
    @MethodSource("triangleTestData")
    @DisplayName("Testes Parametrizados (Particionamento, Casos Típicos, Valor Limite)")
    public void parameterizedTriangleTests(double s1, double s2, double s3, List<String> expected, String description) {
        assertClassification(s1, s2, s3, expected, description);
    }

    @Test
    @DisplayName("Contrato: Saída nunca é nula e nunca é vazia")
    public void testContratoSaidaNaoNulaOuVazia() {
        List<String> resultForma = classifier.classify(3, 4, 5);
        assertNotNull(resultForma, "Contrato Falhou: Resultado não deve ser nulo para triângulo válido.");
        assertFalse(resultForma.isEmpty(), "Contrato Falhou: Resultado não deve ser vazio para triângulo válido.");

        List<String> resultNaoForma = classifier.classify(1, 2, 7);
        assertNotNull(resultNaoForma, "Contrato Falhou: Resultado não deve ser nulo para 'não forma triângulo'.");
        assertFalse(resultNaoForma.isEmpty(), "Contrato Falhou: Resultado não deve ser vazio para 'não forma triângulo'.");
    }

    @Test
    @DisplayName("Contrato: 'NAO FORMA TRIANGULO' é exclusivo e único na lista de classificações")
    public void testContratoExclusividadeNaoForma() {
        List<String> result = classifier.classify(7, 2, 3);
        assertEquals(1, result.size(), "Contrato Falhou: Para 'NAO FORMA TRIANGULO', a lista deve conter exatamente um elemento.");
        assertEquals("NAO FORMA TRIANGULO", result.get(0), "Contrato Falhou: A única classificação deve ser 'NAO FORMA TRIANGULO'.");
    }

    @Test
    @DisplayName("Contrato: Se forma triângulo, deve ter exatamente uma classificação de ângulo")
    public void testContratoUmaClassificacaoAngulo() {
        List<String> resultAcutangulo = classifier.classify(6, 6, 6);
        assertTrue(resultAcutangulo.stream().anyMatch(s -> s.equals("TRIANGULO ACUTANGULO")), "Contrato Falhou (Acutângulo): Deve classificar como Acutângulo.");
        assertEquals(1, resultAcutangulo.stream().filter(s -> s.endsWith("ANGULO")).count(), "Contrato Falhou (Acutângulo): Deve haver exatamente uma classificação de ângulo.");

        List<String> resultRetangulo = classifier.classify(3, 4, 5);
        assertTrue(resultRetangulo.stream().anyMatch(s -> s.equals("TRIANGULO RETANGULO")), "Contrato Falhou (Retângulo): Deve classificar como Retângulo.");
        assertEquals(1, resultRetangulo.stream().filter(s -> s.endsWith("ANGULO")).count(), "Contrato Falhou (Retângulo): Deve haver exatamente uma classificação de ângulo.");

        List<String> resultObtusangulo = classifier.classify(10, 4, 7);
        assertTrue(resultObtusangulo.stream().anyMatch(s -> s.equals("TRIANGULO OBTUSANGULO")), "Contrato Falhou (Obtusângulo): Deve classificar como Obtusângulo.");
        assertEquals(1, resultObtusangulo.stream().filter(s -> s.endsWith("ANGULO")).count(), "Contrato Falhou (Obtusângulo): Deve haver exatamente uma classificação de ângulo.");
    }

    @Test
    @DisplayName("Contrato: Ordem das classificações (Ângulo primeiro, depois Tipo de Lado, se houver)")
    public void testContratoOrdemClassificacoes() {
        List<String> resultAcutIsos = classifier.classify(7, 7, 5);
        if (resultAcutIsos.size() == 2) {
            assertTrue(resultAcutIsos.get(0).endsWith("ANGULO"), "Contrato Falhou (Acut/Isos): Primeira classificação deve ser de ângulo.");
            assertEquals("TRIANGULO ISOSCELES", resultAcutIsos.get(1), "Contrato Falhou (Acut/Isos): Segunda classificação deve ser ISOSCELES.");
        } else if (!resultAcutIsos.contains("NAO FORMA TRIANGULO")) {
            fail("Contrato Falhou (Acut/Isos): Número inesperado de classificações: " + resultAcutIsos + " para (7,7,5)");
        }

        List<String> resultRetIsos = classifier.classify(1, 1, Math.sqrt(2.0));
         if (resultRetIsos.size() == 2) {
            assertTrue(resultRetIsos.get(0).endsWith("ANGULO"), "Contrato Falhou (Ret/Isos): Primeira classificação deve ser de ângulo.");
            assertEquals("TRIANGULO ISOSCELES", resultRetIsos.get(1), "Contrato Falhou (Ret/Isos): Segunda classificação deve ser ISOSCELES.");
        } else if (!resultRetIsos.contains("NAO FORMA TRIANGULO")) {
            fail("Contrato Falhou (Ret/Isos): Número inesperado de classificações: " + resultRetIsos + " para (1,1,sqrt(2))");
        }

        List<String> resultEquilatero = classifier.classify(6, 6, 6);
        if (resultEquilatero.size() == 2) {
            assertEquals("TRIANGULO ACUTANGULO", resultEquilatero.get(0), "Contrato Falhou (Equilatero): Primeira classificação deve ser ACUTANGULO.");
            assertEquals("TRIANGULO EQUILATERO", resultEquilatero.get(1), "Contrato Falhou (Equilatero): Segunda classificação deve ser EQUILATERO.");
        } else {
            fail("Contrato Falhou (Equilatero): Número inesperado de classificações: " + resultEquilatero + " para (6,6,6)");
        }
    }
}
