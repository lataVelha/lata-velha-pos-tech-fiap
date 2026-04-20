package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusExecucaoServico;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.ExecucaoServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReprovarOrdemServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;

    public OrdemServicoResponse execute(Long osId, UUID userId) {
        var ordemServico = ordemServicoRepository.getById(osId);
        var funcionario = funcionarioRepository.getByUserId(userId);

        vailidarStatusOrdem(ordemServico.getStatus(), ordemServico);

        ordemServico.getExecucaoServicos().forEach(execucaoServico -> {
            vailidarStatusServico(execucaoServico.getStatus(), execucaoServico);
            execucaoServico.recusar(funcionario.getId());
        });

        ordemServico.reprovar(funcionario.getId());
        notificarUseCase.execute(ordemServico);

        var saved = ordemServicoRepository.save(ordemServico);
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());
        String mecanicoNome = saved.getMecanicoResponsavelId() != null
                ? funcionarioRepository.getById(saved.getMecanicoResponsavelId()).getNome()
                : null;

        Map<Long, String> mecanicoNomes = saved.getExecucaoServicos().stream()
                .filter(e -> e.getMecanicoResponsavelId() != null)
                .collect(Collectors.toMap(
                        e -> e.getMecanicoResponsavelId(),
                        e -> funcionarioRepository.getById(e.getMecanicoResponsavelId()).getNome(),
                        (a, b) -> a
                ));

        var pecaIds = saved.getExecucaoServicos().stream()
                .flatMap(e -> e.getPecas().stream())
                .map(p -> p.getPecaId())
                .collect(Collectors.toSet());

        Map<Long, Peca> pecaMap = pecaIds.stream()
                .collect(Collectors.toMap(id -> id, pecaRepository::getActiveById));

        return OrdemServicoResponse.from(saved,
                funcionario.getNome(),
                mecanicoNome,
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null,
                mecanicoNomes, pecaMap);
    }

    private void vailidarStatusOrdem(StatusOrdemServico statusOrdemServico, OrdemServico ordemServico) {
        if (statusOrdemServico == null) {
            throw new IllegalStateException("Ordem de Serviço sem status: " + ordemServico.getId());
        }
        switch (statusOrdemServico) {
            case FINALIZADA -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço foi finalizada: " + ordemServico.getId());
            case APROVADA -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço já foi aprovada: " + ordemServico.getId());
            case EM_EXECUCAO -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço está em execução: " + ordemServico.getId());
            case ENTREGUE -> throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço já foi entregue: " + ordemServico.getId());
        }
    }

    private void vailidarStatusServico(StatusExecucaoServico statusExecucaoServico, ExecucaoServico execucaoServico) {
        if (statusExecucaoServico == StatusExecucaoServico.APROVADO) {
            throw new ResourceAlreadyExistsException(
                    "Este serviço foi Aprovado " + execucaoServico.getServico().getNome());
        }
        if (statusExecucaoServico == StatusExecucaoServico.FINALIZADO) {
            throw new ResourceAlreadyExistsException(
                    "Este serviço ja foi realizado " + execucaoServico.getServico().getNome());
        }
    }
}
