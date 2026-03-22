package br.com.lata.velha.domain.valueObject;

import java.util.Objects;

public class Documento {

    private final String valor;
    private final Tipo tipo;

    public enum Tipo { CPF, CNPJ }

    private Documento(String valor, Tipo tipo) {
        this.valor = valor;
        this.tipo = tipo;
    }

    public static Documento of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Documento não pode ser vazio");
        }

        String limpo = valor.replaceAll("[^\\dA-Za-z]", "").toUpperCase();

        // CPF: 11 dígitos numéricos
        if (limpo.matches("\\d{11}")) {
            if (!cpfValido(limpo)) {
                throw new IllegalArgumentException("CPF inválido");
            }
            return new Documento(limpo, Tipo.CPF);
        }

        // CNPJ antigo: 14 dígitos numéricos (ex: 12345678000199)
        // CNPJ novo: 14 caracteres alfanuméricos (ex: AB1CD234EF5G67)
        if (limpo.length() == 14 && limpo.matches("[A-Z0-9]{14}")) {
            if (limpo.matches("\\d{14}") && !cnpjValido(limpo)) {
                throw new IllegalArgumentException("CNPJ inválido");
            }
            return new Documento(limpo, Tipo.CNPJ);
        }

        throw new IllegalArgumentException(
                "Documento inválido. CPF: 11 dígitos. CNPJ: 14 caracteres (numérico ou alfanumérico)");
    }

    // -------------------- VALIDAÇÃO CPF --------------------

    private static boolean cpfValido(String cpf) {
        // rejeita sequências iguais (111.111.111-11, etc)
        if (cpf.chars().distinct().count() == 1) return false;

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += (cpf.charAt(i) - '0') * (10 - i);
        }
        int primeiro = 11 - (soma % 11);
        if (primeiro > 9) primeiro = 0;
        if (primeiro != (cpf.charAt(9) - '0')) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += (cpf.charAt(i) - '0') * (11 - i);
        }
        int segundo = 11 - (soma % 11);
        if (segundo > 9) segundo = 0;
        return segundo == (cpf.charAt(10) - '0');
    }

    // -------------------- VALIDAÇÃO CNPJ --------------------

    private static boolean cnpjValido(String cnpj) {
        // rejeita sequências iguais
        if (cnpj.chars().distinct().count() == 1) return false;

        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};

        int soma = 0;
        for (int i = 0; i < 12; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos1[i];
        }
        int primeiro = soma % 11;
        primeiro = primeiro < 2 ? 0 : 11 - primeiro;
        if (primeiro != (cnpj.charAt(12) - '0')) return false;

        soma = 0;
        for (int i = 0; i < 13; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos2[i];
        }
        int segundo = soma % 11;
        segundo = segundo < 2 ? 0 : 11 - segundo;
        return segundo == (cnpj.charAt(13) - '0');
    }

    // -------------------- GETTERS --------------------

    public String getValor() {
        return valor;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public String getFormatado() {
        if (tipo == Tipo.CPF) {
            return valor.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
        }
        return valor.substring(0, 2) + "." +
               valor.substring(2, 5) + "." +
               valor.substring(5, 8) + "/" +
               valor.substring(8, 12) + "-" +
               valor.substring(12, 14);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(valor, ((Documento) o).valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return getFormatado();
    }
}