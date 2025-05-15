package com.example.triangle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Classe responsável por classificar triângulos com base nos comprimentos de seus lados,
 * seguindo as regras do problema 1045 do BeeCrowd.
 */
public class TriangleClassifier {

    // Constante para tolerância em comparações de números de ponto flutuante (double).
    // Este valor (1e-15) é crucial para a correção dos testes de limite,
    // especialmente para o caso do triângulo retângulo isósceles com Math.sqrt(2.0).
    private static final double EPSILON = 1e-15;

    /**
     * Classifica um triângulo com base nos comprimentos de seus três lados.
     *
     * @param s1 Comprimento do lado 1.
     * @param s2 Comprimento do lado 2.
     * @param s3 Comprimento do lado 3.
     * @return Uma lista de strings contendo as classificações do triângulo.
     * A ordem das classificações é: tipo de ângulo (se formar triângulo),
     * seguido por tipo de lado (equilátero ou isósceles, se aplicável).
     */
    public List<String> classify(double s1, double s2, double s3) {
        List<String> classifications = new ArrayList<>();

        // Cria um array com os lados para facilitar a ordenação.
        Double[] sidesArray = {s1, s2, s3};
        // Ordena os lados em ordem decrescente.
        // O maior lado será 'a', seguido por 'b' e 'c'.
        Arrays.sort(sidesArray, Collections.reverseOrder());
        double a = sidesArray[0]; // Maior lado
        double b = sidesArray[1];
        double c = sidesArray[2]; // Menor lado

        // Passo 1: Verificar se os lados formam um triângulo.
        // Se o maior lado 'a' for não positivo (considerando EPSILON),
        // ou se a desigualdade triangular (a < b+c) não for satisfeita (a >= b+c, considerando EPSILON).
        if (a <= EPSILON || a >= (b + c) - EPSILON) {
            classifications.add("NAO FORMA TRIANGULO");
        } else {
            // Passo 2: Classificar quanto aos ângulos (apenas se formar triângulo).
            double a2 = a * a; // Quadrado do maior lado
            double b2PlusC2 = (b * b) + (c * c); // Soma dos quadrados dos outros dois lados

            // Compara a^2 com b^2 + c^2 usando EPSILON para tratar igualdade de ponto flutuante.
            if (Math.abs(a2 - b2PlusC2) < EPSILON) { 
                classifications.add("TRIANGULO RETANGULO");
            } else if (a2 > b2PlusC2 + EPSILON) { // Verifica se a2 é significativamente maior que b2PlusC2
                classifications.add("TRIANGULO OBTUSANGULO");
            } else { // Caso contrário (a2 é significativamente menor que b2PlusC2)
                classifications.add("TRIANGULO ACUTANGULO");
            }

            // Passo 3: Classificar quanto aos lados (apenas se formar triângulo).
            // Verifica igualdade entre os lados usando EPSILON.
            boolean aEqualsB = Math.abs(a - b) < EPSILON;
            boolean bEqualsC = Math.abs(b - c) < EPSILON;

            if (aEqualsB && bEqualsC) { 
               classifications.add("TRIANGULO EQUILATERO");

            } else if (aEqualsB || bEqualsC) { 
                classifications.add("TRIANGULO ISOSCELES");
            }
        }
        return classifications;
    }
}
