package br.com.lata.velha.domain.valueObject;

import java.util.Objects;

public class Placa {

    private final String valor;

    private Placa(String valor) {
        this.valor = valor.toUpperCase();
    }

    public static Placa of(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Placa não pode ser vazia");
        }

        String limpa = valor.toUpperCase().replaceAll("[^A-Z0-9]", "");

        // Formato antigo: ABC1234 | Mercosul: ABC1D23
        if (!limpa.matches("[A-Z]{3}\\d[A-Z0-9]\\d{2}")) {
            throw new IllegalArgumentException("Placa inválida. Formatos aceitos: ABC1234 ou ABC1D23");
        }

        return new Placa(limpa);
    }

    public String getValor() {
        return valor;
    }

    public String getFormatado() {
        return valor.substring(0, 3) + "-" + valor.substring(3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placa placa = (Placa) o;
        return Objects.equals(valor, placa.valor);
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