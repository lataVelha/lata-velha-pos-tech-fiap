package br.com.lata.velha.ordem_servico.application.use_cases.proprietario;

import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.shared.domain.pagination.PaginatedResult;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ListarProprietariosUseCase {

    private final ProprietarioRepository repository;

    public PaginatedResult<ProprietarioResponse> execute(int page, int size) {
        return PaginatedResult.map(repository.findAllActivePaginated(page, size), ProprietarioResponse::from);
    }
}
