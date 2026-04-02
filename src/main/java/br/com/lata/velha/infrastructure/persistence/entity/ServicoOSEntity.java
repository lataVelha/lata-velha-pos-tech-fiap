package br.com.lata.velha.infrastructure.persistence.entity;

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
    @GeneratedValue
    private Long id;
    private Long atendenteId;

    private String statusServico;

    @ManyToOne
    @JoinColumn(name = "SERVICO_ID")
    private ServicoEntity servico;

    @ManyToOne
    @JoinColumn(name = "OS_ID")
    private OrdemServicoEntity ordemServico;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;

    private Long mecanicoResponsavelId;

    private BigDecimal valorMaoDeObra;

    private LocalDateTime atualizadoEm;

    @OneToMany(mappedBy = "servicoOS", cascade = CascadeType.ALL)
    private List<PecaAlocadaEntity> pecas;
}