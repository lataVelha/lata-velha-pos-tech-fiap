package br.com.lata.velha.domain.model;

import br.com.lata.velha.domain.enuns.StatusOrdemServico;

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
    private Long mecanicoFinalId;

    private List<ServicoOS> servicos = new ArrayList<>();

    public OrdemServico() {
    }

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
    }

    public void iniciarDiagnostico(Long idOs, Long mecanicoId) {
        if (this.status != StatusOrdemServico.RECEBIDA) {
            throw new IllegalStateException("Ordem não pode ser iniciada");
        }
        this.mecanicoFinalId = mecanicoId;
        this.status = StatusOrdemServico.EM_DIAGNOSTICO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void fimDignostico(List<Long> servicoOsId) {
        if (this.status != StatusOrdemServico.EM_DIAGNOSTICO) {
            throw new IllegalStateException("O Diagnostico não pode ser finalizado");
        }
        this.status = StatusOrdemServico.AGUARDANDO_APROVACAO;
        this.atualizadoEm = LocalDateTime.now();
    }

    public void aprovar(Long atendenteInicioId) {

        if (this.status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Ordem não está aprovada");
        }

        this.atendenteInicioId = atendenteInicioId;
        this.status = StatusOrdemServico.EM_EXECUCAO;
        this.atualizadoEm = LocalDateTime.now();

    }

    public void reprovar(Long atendenteInicioId) {

        if (this.status != StatusOrdemServico.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Ordem não está aprovada");
        }

        this.atendenteInicioId = atendenteInicioId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();

    }


    public void finalizar(Long mecanicoId) {
        if (this.status != StatusOrdemServico.EM_EXECUCAO) {
            throw new IllegalStateException("Ordem não está em andamento");
        }

        this.mecanicoFinalId = mecanicoId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void entregar() {
        if (this.status != StatusOrdemServico.FINALIZADA) {
            throw new IllegalStateException("Ordem deve estar finalizada");
        }

        this.status = StatusOrdemServico.ENTREGUE;
        this.entregueEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public void adicionarServico(ServicoOS servico) {
        if (servico == null) {
            throw new IllegalArgumentException("Serviço não pode ser nulo");
        }

        servicos.add(servico);
        atualizadoEm = LocalDateTime.now();
    }

    public double calcularValorTotal() {
        return servicos.stream()
                .mapToDouble(ServicoOS::calcularTotal)
                .sum();
    }

    public Long getId() {
        return id;
    }

    public Long getProprietarioId() {
        return proprietarioId;
    }

    public Long getVeiculoId() {
        return veiculoId;
    }

    public String getReclamacaoCliente() {
        return reclamacaoCliente;
    }

    public StatusOrdemServico getStatus() {
        return status;
    }

    public LocalDateTime getIniciadoEm() {
        return iniciadoEm;
    }

    public LocalDateTime getFinalizadoEm() {
        return finalizadoEm;
    }

    public LocalDateTime getEntregueEm() {
        return entregueEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public Long getAtendenteInicioId() {
        return atendenteInicioId;
    }

    public Long getMecanicoFinalId() {
        return mecanicoFinalId;
    }

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

    public List<ServicoOS> getServicos() {
        return servicos;
    }

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