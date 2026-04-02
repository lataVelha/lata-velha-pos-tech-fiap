package br.com.lata.velha.infrastructure.persistence.entity;

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
    @GeneratedValue
    private Long id;

    private Long proprietarioId;
    private Long veiculoId;

    private String reclamacaoCliente;
    private String status;

    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDateTime entregueEm;
    private LocalDateTime atualizadoEm;

    private Long atendenteInicioId;
    private LocalDateTime terminadoEm;
    private Long mecanicoFinalId;

    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "ordemServico", cascade = CascadeType.ALL)
    private List<ServicoOSEntity> servicos;
}