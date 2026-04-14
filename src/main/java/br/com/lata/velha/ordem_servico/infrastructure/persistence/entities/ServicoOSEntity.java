package br.com.lata.velha.ordem_servico.infrastructure.persistence.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusServico;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SERVICO_OS")
@Data
public class ServicoOSEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ATENDENTE_ID")
    private Long atendenteId;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS_SERVICO", length = 30)
    private StatusServico statusServico;

    @ManyToOne
    @JoinColumn(name = "SERVICO_ID", nullable = false)
    private ServicoEntity servico;

    @ManyToOne
    @JoinColumn(name = "OS_ID", nullable = false)
    private OrdemServicoEntity ordemServico;

    @Column(name = "INICIADO_EM")
    private LocalDateTime iniciadoEm;

    @Column(name = "TERMINADO_EM")
    private LocalDateTime terminadoEm;

    @Column(name = "MECANICO_RESPONSAVEL_ID")
    private Long mecanicoResponsavelId;

    @Column(name = "VALOR_MAO_DE_OBRA", precision = 10, scale = 2)
    private BigDecimal valorMaoDeObra;

    @Column(name = "ATUALIZADO_EM")
    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "servicoOS", cascade = CascadeType.ALL)
    private List<PecaAlocadaEntity> pecas;
}