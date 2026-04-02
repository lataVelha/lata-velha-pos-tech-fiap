package br.com.lata.velha.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "PECA_ESTOQUE")
@Data
public class PecaEstoqueEntity {

    @Id
    private Long pecaId;

    private Integer quantidadeArmazenada;
}