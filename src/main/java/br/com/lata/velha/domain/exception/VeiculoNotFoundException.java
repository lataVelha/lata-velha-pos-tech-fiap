package br.com.lata.velha.domain.exception;

public class VeiculoNotFoundException extends DomainException {

    public VeiculoNotFoundException(Long id) {
        super("Veículo não encontrado com id: " + id);
    }

    public VeiculoNotFoundException(String placa) {
        super("Veículo não encontrado com placa: " + placa);
    }
}