package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Peca;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import br.com.lata.velha.shared.domain.value_objects.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FinalizarDiagnosticoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;

    public OrdemServicoResponse execute(Long idOs, UserId userId){
        var ordemServico = ordemServicoRepository.getById(idOs);
        var mecanico = funcionarioRepository.getByUserId(userId);

        ordemServico.finalizarDiagnostico();

        var saved = ordemServicoRepository.save(ordemServico);
        notificarUseCase.execute(saved);

        var atendente = funcionarioRepository.getById(saved.getAtendenteInicioId());
        var proprietario = proprietarioRepository.getActiveById(saved.getProprietarioId());
        var veiculo = veiculoRepository.getActiveById(saved.getVeiculoId());

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
                atendente.getNome(),
                mecanico.getNome(),
                proprietario.getNome(),
                veiculo.getMarca() + " " + veiculo.getModelo(),
                null, null, null,
                mecanicoNomes, pecaMap);
    }
}
