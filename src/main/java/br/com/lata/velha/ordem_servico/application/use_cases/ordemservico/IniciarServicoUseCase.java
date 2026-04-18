package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarServicoUseCase {

    private final OrdemServicoAssembler ordemServicoAssembler;
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;

    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {

        var os = ordemServicoRepository.findById(idOs);
        var mecanico = funcionarioRepository.getById(idMecanico);

        if (!StatusOrdemServico.EM_EXECUCAO.equals(os.getStatus())) {
            throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço não pode ser Iniciada: " + os.getId());
        }
        os.getServicos().forEach(sos -> {

            sos.getPecas().forEach(p -> {
                var temPeca = pecaAlocadaRepository.findByPecaIdAndServicoOsId(p.getPecaId(), sos.getId());

                if (temPeca.getStatus() == StatusPecaAlocada.RESERVADA) {

                    sos.instalarPeca(p.getQuantidadeReservada(), mecanico.getId());
                } else if (temPeca.getStatus() == StatusPecaAlocada.ENCOMENDA || temPeca.getStatus() == StatusPecaAlocada.PARCIAL) {

                    sos.aguardandoPeca(mecanico.getId());
                }

            });

        });

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null,null,null,null);
    }
}
