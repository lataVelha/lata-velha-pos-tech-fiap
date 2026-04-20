package br.com.lata.velha.ordem_servico.application.use_cases.pecaalocada;

import br.com.lata.velha.ordem_servico.application.dtos.response.PecaAlocadaResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.PecaAlocadaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarPecasAlocadasUseCase {

    private final PecaAlocadaRepository pecaAlocadaRepository;

    public PaginatedResult<PecaAlocadaResponse> execute(Long servicoOsId, int page, int size) {
        return PaginatedResult.map(
                pecaAlocadaRepository.findByServicoOsId(servicoOsId, page, size),
                PecaAlocadaResponse::from
        );
    }
}
