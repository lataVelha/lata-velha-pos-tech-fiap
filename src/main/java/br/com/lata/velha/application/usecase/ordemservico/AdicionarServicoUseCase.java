package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.application.assembler.OrdemServicoAssembler;
import br.com.lata.velha.application.dto.request.AddServicoOsRequest;
import br.com.lata.velha.application.dto.response.OrdemServicoResponse;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoAssembler ordemServicoAssembler;

    public OrdemServicoResponse execute(AddServicoOsRequest request) {

        var os = ordemServicoRepository.findById(request.idOs());
        request.servicoOSRequests().stream().forEach(so -> {
            //    var servicoOs =

            so.pecasId().forEach(p -> {
                //   var peca =
                //   adicionarPeca(p)
                // alocar peca
            });
            // os.adicionarServico(servicoOS);
        });

        return ordemServicoAssembler.toResponse(ordemServicoRepository.save(os), null, null);
    }
}