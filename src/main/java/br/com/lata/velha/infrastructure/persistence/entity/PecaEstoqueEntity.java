package br.com.lata.velha.infrastructure.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Table(name = "PECA_ESTOQUE")
@Data
public class PecaEstoqueEntity {

    @Id
    @Column(name = "PECA_ID", nullable = false)
    private Long pecaId;

    @Column(name = "QUANTIDADE_ARMAZENADA", nullable = false)
    private Integer quantidadeArmazenada;
}