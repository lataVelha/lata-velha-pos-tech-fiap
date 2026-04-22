package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDEM_SERVICO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdemServicoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "PROPRIETARIO_ID", nullable = false)
    private Long proprietarioId;

    @Column(name = "VEICULO_ID", nullable = false)
    private Long veiculoId;

    @Column(name = "RECLAMACAO_CLIENTE")
    private String reclamacaoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", length = 30)
    private StatusOrdemServico status;

    @Column(name = "CRIADO_EM")
    private LocalDateTime criadoEm;

    @Column(name = "INICIADO_EM")
    private LocalDateTime iniciadoEm;

    @Column(name = "FINALIZADO_EM")
    private LocalDateTime finalizadoEm;

    @Column(name = "ENTREGUE_EM")
    private LocalDateTime entregueEm;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    @Column(name = "ATENDENTE_INICIO_ID", nullable = false)
    private Long atendenteInicioId;

    @Column(name = "MECANICO_RESPONSAVEL_ID")
    private Long mecanicoResponsavelId;

    @Column(name = "VALOR_TOTAL", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "OS_ID")
    private List<ExecucaoServicoEntity> servicos;

    public static OrdemServicoEntity fromDomain(OrdemServico domain) {
        var servicos = domain.getExecucaoServicos().stream()
                .map(ExecucaoServicoEntity::fromDomain)
                .toList();
        return new OrdemServicoEntity(
                domain.getId(),
                domain.getProprietarioId(),
                domain.getVeiculoId(),
                domain.getReclamacaoCliente(),
                domain.getStatus(),
                domain.getCriadoEm(),
                domain.getIniciadoEm(),
                domain.getFinalizadoEm(),
                domain.getEntregueEm(),
                domain.getAtualizadoEm(),
                domain.getAtendenteInicioId(),
                domain.getMecanicoResponsavelId(),
                domain.calcularValorTotal(),
                servicos
        );
    }

    public OrdemServico toDomain() {
        var servicos = this.servicos.stream()
                .map(ExecucaoServicoEntity::toDomain)
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        return new OrdemServico(
                id,
                proprietarioId,
                veiculoId,
                reclamacaoCliente,
                status,
                criadoEm,
                iniciadoEm,
                finalizadoEm,
                entregueEm,
                atualizadoEm,
                atendenteInicioId,
                mecanicoResponsavelId,
                servicos
        );
    }
}