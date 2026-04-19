package br.com.lata.velha.ordem_servico.domain.entities;

import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class OrdemServico {
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

    private final List<ExecucaoServico> execucaoServicos;

    public OrdemServico(Long id, Long proprietarioId, Long veiculoId, String reclamacaoCliente, StatusOrdemServico status, LocalDateTime iniciadoEm, LocalDateTime finalizadoEm, LocalDateTime entregueEm, LocalDateTime atualizadoEm, Long atendenteInicioId, Long mecanicoResponsavelId, List<ExecucaoServico> execucaoServicos) {
        this.id = id;
        this.proprietarioId = proprietarioId;
        this.veiculoId = veiculoId;
        this.reclamacaoCliente = reclamacaoCliente;
        this.status = status;
        this.iniciadoEm = iniciadoEm;
        this.finalizadoEm = finalizadoEm;
        this.entregueEm = entregueEm;
        this.atualizadoEm = atualizadoEm;
        this.atendenteInicioId = atendenteInicioId;
        this.mecanicoResponsavelId = mecanicoResponsavelId;
        this.execucaoServicos = execucaoServicos;
    }

    public static OrdemServico create(Long proprietarioId, Long veiculoId, String reclamacaoCliente, Long atendenteInicioId) {
        return new OrdemServico(null, proprietarioId, veiculoId, reclamacaoCliente, StatusOrdemServico.RECEBIDA, null, null, null, null, atendenteInicioId, null, new ArrayList<>());
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

        boolean existeExecucaoEmAndamento = execucaoServicos.stream()
                .anyMatch(ExecucaoServico::isEmExecucao);

        if (existeExecucaoEmAndamento) {
            throw new IllegalStateException(
                    "Existem serviços em execução"
            );
        }

        boolean existeExecucaoComPecaNaoProcessada = execucaoServicos.stream()
                .filter(s -> !s.isRecusado())
                .anyMatch(s -> s.getPecas().stream().anyMatch(p -> !p.isProcessada()));

        if (existeExecucaoComPecaNaoProcessada) {
            throw new IllegalStateException(
                    "Existem peças não processadas em serviços ativos"
            );
        }

        this.mecanicoResponsavelId = mecanicoId;
        this.status = StatusOrdemServico.FINALIZADA;
        this.finalizadoEm = LocalDateTime.now();

        touch();
    }

    public void entregar(Long atendenteId) {
        validarStatus(StatusOrdemServico.FINALIZADA);

        this.atendenteInicioId =atendenteId;
        this.status = StatusOrdemServico.ENTREGUE;
        this.entregueEm = LocalDateTime.now();
        touch();
    }


    public void adicionarServico(ExecucaoServico servico) {

        if (servico == null)
            throw new IllegalArgumentException("Serviço inválido");

        if (status == StatusOrdemServico.FINALIZADA ||
                status == StatusOrdemServico.ENTREGUE) {
            throw new IllegalStateException(
                    "Não é possível adicionar serviço"
            );
        }

        boolean jaExiste = execucaoServicos.stream()
                .anyMatch(s -> s.getServico().getId()
                        .equals(servico.getServico().getId()));

        if (jaExiste) {
            throw new IllegalStateException(
                    "Serviço já adicionado"
            );
        }

        execucaoServicos.add(servico);
        touch();
    }

    public BigDecimal calcularValorTotal() {
        return execucaoServicos.stream()
                .map(ExecucaoServico::calcularTotal)
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
    public List<ExecucaoServico> getExecucaoServicos() { return execucaoServicos; }

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

    public void setStatus(StatusOrdemServico status) {
        this.status = status;
    }

    public void setIniciadoEm(LocalDateTime iniciadoEm) {
        this.iniciadoEm = iniciadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public void setFinalizadoEm(LocalDateTime finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public void setEntregueEm(LocalDateTime entregueEm) {
        this.entregueEm = entregueEm;
    }

    public void setMecanicoResponsavelId(Long mecanicoResponsavelId) {
        this.mecanicoResponsavelId = mecanicoResponsavelId;
    }
}