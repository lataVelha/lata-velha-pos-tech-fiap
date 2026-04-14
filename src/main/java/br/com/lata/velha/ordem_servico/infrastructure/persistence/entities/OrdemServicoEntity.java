package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORDEM_SERVICO")
@Data
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

    @Column(name = "TERMINADO_EM")
    private LocalDateTime terminadoEm;

    @Column(name = "MECANICO_FINAL_ID")
    private Long mecanicoFinalId;

    @Column(name = "VALOR_TOTAL", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    private List<ServicoOSEntity> servicos;
}