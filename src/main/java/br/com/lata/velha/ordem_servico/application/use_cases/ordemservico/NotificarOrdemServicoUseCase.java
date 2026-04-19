package br.com.lata.velha.ordem_servico.application.use_cases.ordemservico;

import br.com.lata.velha.ordem_servico.application.ports.EmailProvider;
import br.com.lata.velha.ordem_servico.application.ports.EmailTemplateProvider;
import br.com.lata.velha.ordem_servico.application.ports.OrdemServicoEmailBuilderPort;
import br.com.lata.velha.ordem_servico.domain.entities.OrdemServico;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.repositories.ProprietarioRepository;
import br.com.lata.velha.ordem_servico.domain.repositories.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificarOrdemServicoUseCase {

    private final EmailProvider emailProvider;
    private final EmailTemplateProvider templateProvider;
    private final ProprietarioRepository proprietarioRepository;
    private final VeiculoRepository veiculoRepository;
    private final OrdemServicoEmailBuilderPort emailBuilder;

    public void execute(OrdemServico os) {
        try {
            Proprietario proprietario = proprietarioRepository.findActiveById(os.getProprietarioId());
            Veiculo veiculo = veiculoRepository.findActiveById(os.getVeiculoId());

            Map<String, Object> variables = emailBuilder.buildVariables(os, proprietario, veiculo);
            String assunto = emailBuilder.getAssunto(os.getStatus());

            String html = templateProvider.render("os-notificacao", variables);
            emailProvider.send(proprietario.getEmail(), assunto, html);
        } catch (Exception e) {
            log.error("Falha ao enviar email para OS: {} status: {}", os.getId(), os.getStatus(), e);
        }
    }
}