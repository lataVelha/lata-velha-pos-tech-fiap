package br.com.lata.velha.ordem_servico.application.use_cases.servico;

import br.com.lata.velha.ordem_servico.application.dtos.response.ServicoResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.ServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BuscarServicosUseCase {

    private final ServicoRepository repository;

    public PaginatedResult<ServicoResponse> execute(int page, int size) {
        return PaginatedResult.map(repository.findAllActivePaginated(page, size), ServicoResponse::from);
    }
}
