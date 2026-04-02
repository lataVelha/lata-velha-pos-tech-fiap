package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.model.ServicoOS;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdicionarServicoUseCase {

    private final OrdemServicoRepository repository;

    public void executar(Long ordemId, Long servicoOS) {

        OrdemServico os = repository.findById(ordemId);

       // os.adicionarServico(servicoOS);

        repository.save(os);
    }
}