package br.com.lata.velha.infrastructure.persistence.entity;

import br.com.lata.velha.domain.enuns.StatusPecaAlocada;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "PECA_ALOCADA")
@Data
public class PecaAlocadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "QTD_SOLICITADA", nullable = false)
    private Integer quantidadeSolicitada;

    @Column(name = "QTD_RESERVADA", nullable = false)
    private Integer quantidadeReservada = 0;

    @Column(name = "QTD_ENCOMENDADA", nullable = false)
    private Integer quantidadeEncomendada = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private StatusPecaAlocada status;

    @Column(name = "ATUALIZADO")
    private LocalDateTime atualizado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERVICO_OS_ID", nullable = false)
    private ServicoOSEntity servicoOS;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PECA_ID", nullable = false)
    private PecaEntity peca;
}