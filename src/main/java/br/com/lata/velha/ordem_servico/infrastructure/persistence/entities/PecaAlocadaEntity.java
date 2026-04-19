package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.PecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PECA_ALOCADA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PecaAlocadaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name  = "PECA_ID", nullable = false)
    private Long pecaId;

    @Column(name = "EXECUCAO_SERVICO_ID", nullable = false)
    private Long execucaoServicoId;

    @Column(name = "QTD_SOLICITADA", nullable = false)
    private Integer quantidadeSolicitada;

    @Column(name = "QTD_RESERVADA", nullable = false)
    private Integer quantidadeReservada = 0;

    @Column(name = "QTD_ENCOMENDADA", nullable = false)
    private Integer quantidadeEncomendada = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30, nullable = false)
    private StatusPecaAlocada status;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizado;

    public static PecaAlocadaEntity fromDomain(PecaAlocada domain) {
        if (domain == null) return null;
        return new PecaAlocadaEntity(
                domain.getId(),
                domain.getPecaId(),
                domain.getExecucaoServicoId(),
                domain.getQuantidadeSolicitada(),
                domain.getQuantidadeReservada(),
                domain.getQuantidadeEncomendada(),
                domain.getStatus(),
                domain.getAtualizado()
        );
    }

    public PecaAlocada toDomain() {
        return new PecaAlocada(
                id,
                pecaId,
                execucaoServicoId,
                quantidadeSolicitada,
                quantidadeReservada,
                quantidadeEncomendada,
                status,
                atualizado
        );
    }
}