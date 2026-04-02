package br.com.lata.velha.application.usecase.ordemservico;

import br.com.lata.velha.domain.model.OrdemServico;
import br.com.lata.velha.domain.repository.FuncionarioRepository;
import br.com.lata.velha.domain.repository.OrdemServicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IniciarDiagnosticoUseCase {

    private final OrdemServicoRepository repository;

    private final FuncionarioRepository funcionarioRepository;

    public void executar(Long idOs, Long idMecanico) {

        OrdemServico os = repository.findById(idOs);

        var mecanico = funcionarioRepository.findById(idMecanico);

        os.iniciarDiagnostico(os.getId(),mecanico.getId());

        repository.save(os);
    }
}