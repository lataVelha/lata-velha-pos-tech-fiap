package br.com.lata.velha.domain.entities;

import br.com.lata.velha.domain.enuns.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrdemServico {

    private Long id;
    private Long proprietarioId;
    private Long veiculoId;
    private String reclamacaoCliente;
    private StatusOrdemServico status;

    private LocalDateTime iniciadoEm;
    private LocalDateTime finalizadoEm;
    private LocalDateTime entregueEm;
    private LocalDateTime atualizadoEm;

    private Long atendenteInicioId;
    private Long mecanicoResponsavelId;

    private List<ServicoOS> servicos = new ArrayList<>();

    public OrdemServico() {}

    public OrdemServico(Long id,
                        Long proprietarioId,
                        Long veiculoId,
                        String reclamacaoCliente,
                        Long atendenteInicioId) {

        this.id = id;
        setProprietarioId(proprietarioId);
        setVeiculoId(veiculoId);
        setReclamacaoCliente(reclamacaoCliente);
        setAtendenteInicioId(atendenteInicioId);

        this.status = StatusOrdemServico.RECEBIDA;
        this.iniciadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /* ================== FLUXO ================== */

    public void iniciarDiagnostico(Long mecanicoId) {
        validarStatus(StatusOrdemServico.RECEBIDA);

        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.EM_DIAGNOSTICO;
        touch();
    }

    public void finalizarDiagnostico(Long mecanicoId) {
        validarStatus(StatusOrdemServico.EM_DIAGNOSTICO);

        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
        touch();
    }

    public void aprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        this.atendenteInicioId = atendenteId;
        this.status = StatusOrdemServico.EM_EXECUCAO;
        touch();
    }

    public void reprovar(Long atendenteId) {
        validarStatus(StatusOrdemServico.AGUARDANDO_APROVACAO);

        this.atendenteInicioId = atendenteId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void finalizar(Long mecanicoId) {
        validarStatus(StatusOrdemServico.EM_EXECUCAO);

        boolean possuiServicoNaoFinalizado = servicos.stream()
                .anyMatch(s -> !s.isFinalizado());

        if (possuiServicoNaoFinalizado) {
            throw new IllegalStateException(
                    "Existem serviços não finalizados"
            );
        }

        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();
        touch();
    }

    public void entregar() {
        validarStatus(StatusOrdemServico.FINALIZADA);

        this.status = StatusOrdemServico.ENTREGUE;
        this.entregueEm = LocalDateTime.now();
        touch();
    }

    /* ================== SERVIÇOS ================== */

    public void adicionarServico(ServicoOS servico) {

        if (servico == null)
            throw new IllegalArgumentException("Serviço inválido");

        if (status == StatusOrdemServico.FINALIZADA ||
                status == StatusOrdemServico.ENTREGUE) {
            throw new IllegalStateException(
                    "Não é possível adicionar serviço"
            );
        }

        boolean jaExiste = servicos.stream()
                .anyMatch(s -> s.getServico().getId()
                        .equals(servico.getServico().getId()));

        if (jaExiste) {
            throw new IllegalStateException(
                    "Serviço já adicionado"
            );
        }

        servicos.add(servico);
        touch();
    }

    public BigDecimal calcularValorTotal() {
        return servicos.stream()
                .map(ServicoOS::calcularTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /* ================== HELPERS ================== */

    private void validarStatus(StatusOrdemServico esperado) {
        if (this.status != esperado) {
            throw new IllegalStateException(
                    "Status inválido. Atual: " + status +
                            " esperado: " + esperado
            );
        }
    }

    private void touch() {
        this.atualizadoEm = LocalDateTime.now();
    }

    /* ================== GETTERS ================== */

    public Long getId() { return id; }
    public Long getProprietarioId() { return proprietarioId; }
    public Long getVeiculoId() { return veiculoId; }
    public String getReclamacaoCliente() { return reclamacaoCliente; }
    public StatusOrdemServico getStatus() { return status; }
    public LocalDateTime getIniciadoEm() { return iniciadoEm; }
    public LocalDateTime getFinalizadoEm() { return finalizadoEm; }
    public LocalDateTime getEntregueEm() { return entregueEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public Long getAtendenteInicioId() { return atendenteInicioId; }
    public Long getMecanicoResponsavelId() { return mecanicoResponsavelId; }
    public List<ServicoOS> getServicos() { return servicos; }

    /* ================== SETTERS CONTROLADOS ================== */

    public void setProprietarioId(Long proprietarioId) {
        if (proprietarioId == null)
            throw new IllegalArgumentException("Proprietário obrigatório");
        this.proprietarioId = proprietarioId;
    }

    public void setVeiculoId(Long veiculoId) {
        if (veiculoId == null)
            throw new IllegalArgumentException("Veículo obrigatório");
        this.veiculoId = veiculoId;
    }

    public void setReclamacaoCliente(String reclamacaoCliente) {
        if (reclamacaoCliente == null || reclamacaoCliente.isBlank())
            throw new IllegalArgumentException("Reclamação obrigatória");
        this.reclamacaoCliente = reclamacaoCliente;
    }

    public void setAtendenteInicioId(Long atendenteInicioId) {
        if (atendenteInicioId == null)
            throw new IllegalArgumentException("Atendente obrigatório");
        this.atendenteInicioId = atendenteInicioId;
    }

    /* ================== OBJECT ================== */

    @Override
    public String toString() {
        return "OrdemServico{id=" + id +
                ", status=" + status +
                ", valorTotal=" + calcularValorTotal() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrdemServico)) return false;
        OrdemServico that = (OrdemServico) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}