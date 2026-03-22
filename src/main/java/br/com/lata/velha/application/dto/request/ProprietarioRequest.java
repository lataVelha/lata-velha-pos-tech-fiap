package br.com.lata.velha.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados para criação/atualização de proprietário")
public record ProprietarioRequest(
        @NotBlank(message = "Nome é obrigatório") @Schema(example = "João da Silva") String nome,
        @NotBlank(message = "Email é obrigatório") @Email(message = "Email inválido") @Schema(example = "joao@email.com") String email,
        @NotBlank(message = "Documento é obrigatório") @Schema(description = "CPF ou CNPJ", example = "123.456.789-00") String documento,
        @NotBlank(message = "Número de celular é obrigatório") @Schema(example = "(11) 99999-9999") String numeroCelular,
        @Valid @Schema(description = "Endereço do proprietário") EnderecoRequest endereco
) {}