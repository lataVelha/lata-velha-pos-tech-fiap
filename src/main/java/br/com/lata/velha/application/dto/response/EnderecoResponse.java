package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
 
@Schema(description = "Dados do endereço")
public record EnderecoResponse(
        @Schema(example = "Rua das Flores") String rua,
        @Schema(example = "01234567") String cep,
        @Schema(example = "123") String numeroCasa
) {}
 