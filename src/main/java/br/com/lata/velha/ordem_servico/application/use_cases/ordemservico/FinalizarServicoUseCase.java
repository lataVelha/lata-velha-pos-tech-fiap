package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.assemblers.OrdemServicoAssembler;
import br.com.lata.velha.ordem_servico.application.dtos.response.OrdemServicoResponse;
import br.com.lata.velha.ordem_servico.domain.enums.StatusOrdemServico;
import br.com.lata.velha.ordem_servico.domain.enums.StatusPecaAlocada;
import br.com.lata.velha.ordem_servico.domain.enums.StatusServico;
import br.com.lata.velha.ordem_servico.domain.repositories.FuncionarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.OrdemServicoRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.shared.domain.exceptions.ResourceAlreadyExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FinalizarServicoUseCase {

    private final OrdemServicoAssembler ordemServicoAssembler;
    private final OrdemServicoRepository ordemServicoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final PecaAlocadaRepository pecaAlocadaRepository;
    private final ProprietarioRepository proprietarioRepository;
    private final NotificarOrdemServicoUseCase notificarUseCase;


    public OrdemServicoResponse execute(Long idOs, Long idMecanico) {

        var os = ordemServicoRepository.findById(idOs);
        var mecanico = funcionarioRepository.getById(idMecanico);

        if (!StatusOrdemServico.EM_EXECUCAO.equals(os.getStatus())) {
            throw new ResourceAlreadyExistsException(
                    "Esta Ordem de Serviço não pode ser Finalizada: " + os.getId());
        }

        os.getServicos().forEach(sos -> {

            if (!StatusServico.EM_EXECUCAO.equals(sos.getStatus())) {
                throw new ResourceAlreadyExistsException(
                        "Este Serviço não pode ser Finalizado: " + sos.getId());
            }

            sos.getPecas().forEach(p -> {

                var peca = pecaAlocadaRepository
                        .findByPecaIdAndServicoOS_Id(p.getPecaId(), sos.getId());

                if (!StatusPecaAlocada.RESERVADA.equals(peca.getStatus())) {
                    throw new ResourceAlreadyExistsException(
                            "Peça não reservada para finalizar serviço");
                }

                peca.instalada(peca.getQuantidadeReservada());

            });

            sos.finalizado(mecanico.getId());

        });

        os.finalizar(mecanico.getId());
        notificarUseCase.execute(os);

        return ordemServicoAssembler.toResponse(
                ordemServicoRepository.save(os),
                null,
                null,
                null,
                null,
                null
        );
    }


}