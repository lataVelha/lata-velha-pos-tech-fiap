package br.com.lata.velha.domain.valueObject;

import java.util.Objects;

public class NumeroCelular {

    private final String valor;

    private NumeroCelular(String valor) {
        this.valor = valor;
    }

    public static NumeroCelular of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Número de celular não pode ser vazio");
        }

        String limpo = valor.replaceAll("[^\\d]", "");

        if (limpo.length() < 10 || limpo.length() > 11) {
            throw new IllegalArgumentException(
                    "Número de celular inválido. Informe DDD + número (10 ou 11 dígitos)");
        }

        return new NumeroCelular(limpo);
    }

    public String getValor() {
        return valor;
    }

    public String getFormatado() {
        if (valor.length() == 11) {
            return valor.replaceAll("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
        }
        return valor.replaceAll("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NumeroCelular that = (NumeroCelular) o;
        return Objects.equals(valor, that.valor);
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