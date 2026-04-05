package br.com.lata.velha.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaServicoResponse {

    @Schema(example = "Id")
    private Long id;
    @Schema(example = "pastilha")
    private String nome;
    @Schema(example = "2")
    private Integer quantidade;
}