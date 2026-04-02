package br.com.lata.velha.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PECA_ALOCADA")
@Data
public class PecaAlocadaEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "SERVICO_OS_ID")
    private ServicoOSEntity servicoOS;

    @ManyToOne
    @JoinColumn(name = "PECA_ID")
    private PecaEntity peca;

    private Integer quantidadeAlocada;
}