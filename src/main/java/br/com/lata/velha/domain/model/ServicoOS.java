package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.enuns.StatusServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServicoOS {

    private Long id;
    private StatusServico status;
    private Servico servico;
    private Long atendenteId;

    private LocalDateTime iniciadoEm;
    private LocalDateTime terminadoEm;
    private Long mecanicoResponsavelId;

    private List<PecaAlocada> pecas = new ArrayList<>();

    private BigDecimal valorMaoDeObra;
    private LocalDateTime atualizadoEm;

    public ServicoOS() {
    }

    //TODO remove this constructor, not having it is breaking tests
    public ServicoOS(Long id, Servico servico, BigDecimal valorMaoDeObra) {
        this.id = id;
        setServico(servico);
        setValorMaoDeObra(valorMaoDeObra);
        this.status = StatusServico.PENDENTE;
        this.iniciadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public ServicoOS(Servico servico, BigDecimal valorMaoDeObra) {
        setServico(servico);
        setValorMaoDeObra(valorMaoDeObra);
        this.status = StatusServico.PENDENTE;
        this.iniciadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /* ===================== REGRAS ===================== */

    public void aprovado(Long atendente) {

        if (status != StatusServico.PENDENTE) {
            throw new IllegalStateException(
                    "Serviço não pode ser aprovado no status: " + status
            );
        }

        this.status = StatusServico.APROVADO;
        this.atendenteId = atendente;
        touch();
    }

    public void recusado(Long atendente) {

        if (status == StatusServico.FINALIZADO) {
            throw new IllegalStateException("Serviço já finalizado");
        }

        this.status = StatusServico.RECUSADO;
        this.atendenteId = atendente;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void instalarPeca(Long pecaId, int quantidade) {

        if (status != StatusServico.APROVADO) {
            throw new IllegalStateException("Serviço deve estar aprovado para instalar peças");
        }

        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade inválida");
        }

        PecaAlocada peca = pecas.stream()
                .filter(p -> pecaId.equals(p.getPecaId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Peça não encontrada"));

        peca.instalar(quantidade);

        touch();
    }

    public void finalizado(Long mecanicoId) {

        if (status != StatusServico.APROVADO) {
            throw new IllegalStateException(
                    "Serviço só pode ser finalizado se estiver aprovado"
            );
        }

        boolean possuiNaoInstalada = pecas.stream()
                .anyMatch(p -> !p.totalmenteInstalada());

        if (possuiNaoInstalada) {
            throw new IllegalStateException(
                    "Existem peças não instaladas no serviço"
            );
        }

        this.status = StatusServico.FINALIZADO;
        this.mecanicoResponsavelId = mecanicoId;
        this.terminadoEm = LocalDateTime.now();
        touch();
    }

    public void adicionarPeca(PecaAlocada peca) {

        if (status == StatusServico.FINALIZADO) {
            throw new IllegalStateException("Serviço já finalizado");
        }

        if (peca == null)
            throw new IllegalArgumentException("Peça inválida");

        boolean jaExiste = pecas.stream()
                .anyMatch(p -> p.getPecaId().equals(peca.getPecaId()));

        if (jaExiste) {
            throw new IllegalStateException(
                    "Peça já adicionada ao serviço"
            );
        }

        pecas.add(peca);
        touch();
    }

    public BigDecimal calcularTotal() {

        BigDecimal totalPecas = pecas.stream()
                .map(p -> BigDecimal.ZERO) // sem valor na peça no seu modelo atual
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return totalPecas.add(valorMaoDeObra != null ? valorMaoDeObra : BigDecimal.ZERO);
    }

    /* ===================== HELPERS ===================== */

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    /* ===================== GETTERS ===================== */

    public Long getId() { return id; }
    public StatusServico getStatus() { return status; }
    public Servico getServico() { return servico; }
    public LocalDateTime getIniciadoEm() { return iniciadoEm; }
    public LocalDateTime getTerminadoEm() { return terminadoEm; }
    public Long getMecanicoResponsavelId() { return mecanicoResponsavelId; }
    public List<PecaAlocada> getPecas() { return pecas; }
    public BigDecimal getValorMaoDeObra() { return valorMaoDeObra; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Long getAtendenteId() { return atendenteId; }

    /* ===================== SETTERS CONTROLADOS ===================== */

    public void setId(Long id) {
        this.id = id;
    }

    public void setStatus(StatusServico status) {
        this.status = status;
    }

    public void setServico(Servico servico) {
        if (servico == null)
            throw new IllegalArgumentException("Serviço obrigatório");
        this.servico = servico;
    }

    public void setValorMaoDeObra(BigDecimal valorMaoDeObra) {
        if (valorMaoDeObra == null || valorMaoDeObra.doubleValue() < 0)
            throw new IllegalArgumentException("Valor inválido");
        this.valorMaoDeObra = valorMaoDeObra;
    }

    public void setMecanicoResponsavelId(Long mecanicoResponsavelId) {
        this.mecanicoResponsavelId = mecanicoResponsavelId;
    }

    public void setIniciadoEm(LocalDateTime iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public void setTerminadoEm(LocalDateTime terminadoEm) {
        this.terminadoEm = terminadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public boolean isFinalizado() {
        return StatusServico.FINALIZADO.equals(this.status);
    }

    public boolean isAprovado() {
        return StatusServico.APROVADO.equals(this.status);
    }

    public boolean isRecusado() {
        return StatusServico.RECUSADO.equals(this.status);
    }
}