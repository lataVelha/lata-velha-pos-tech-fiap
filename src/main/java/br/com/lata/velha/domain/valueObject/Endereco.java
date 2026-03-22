package br.com.lata.velha.domain.valueObject;

import java.util.Objects;

public class Endereco {

    private String rua;
    private String cep;
    private String numeroCasa;

    public Endereco() {
    }

    public Endereco(String rua, String cep, String numeroCasa) {
        setRua(rua);
        setCep(cep);
        setNumeroCasa(numeroCasa);
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        if (rua == null || rua.isBlank()) {
            throw new IllegalArgumentException("Rua não pode ser vazia");
        }
        this.rua = rua;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido. Formato esperado: 00000-000");
        }
        this.cep = cep.replace("-", "");
    }

    public String getNumeroCasa() {
        return numeroCasa;
    }

    public void setNumeroCasa(String numeroCasa) {
        if (numeroCasa == null || numeroCasa.isBlank()) {
            throw new IllegalArgumentException("Número da casa não pode ser vazio");
        }
        this.numeroCasa = numeroCasa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Endereco endereco = (Endereco) o;
        return Objects.equals(rua, endereco.rua)
                && Objects.equals(cep, endereco.cep)
                && Objects.equals(numeroCasa, endereco.numeroCasa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rua, cep, numeroCasa);
    }

    @Override
    public String toString() {
        return rua + ", " + numeroCasa + " - CEP: " + cep;
    }
}