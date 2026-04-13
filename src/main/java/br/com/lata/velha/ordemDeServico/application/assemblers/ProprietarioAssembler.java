package br.com.lata.velha.ordemDeServico.application.assemblers;

import br.com.lata.velha.ordemDeServico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordemDeServico.application.dtos.response.EnderecoResponse;
import br.com.lata.velha.ordemDeServico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordemDeServico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordemDeServico.domain.entities.Proprietario;
import br.com.lata.velha.ordemDeServico.domain.entities.Veiculo;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Documento;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.Endereco;
import br.com.lata.velha.ordemDeServico.domain.valueObjects.NumeroCelular;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProprietarioAssembler {

    public Proprietario toDomain(ProprietarioRequest req) {
        Endereco endereco = req.endereco() != null
                ? new Endereco(req.endereco().rua(), req.endereco().cep(), req.endereco().numeroCasa())
                : null;
        return new Proprietario(null, req.nome(), req.email(),
                Documento.of(req.documento()), NumeroCelular.of(req.numeroCelular()), endereco);
    }

    public void updateDomain(Proprietario existente, ProprietarioRequest req) {
        existente.setNome(req.nome());
        existente.setEmail(req.email());
        existente.setDocumento(Documento.of(req.documento()));
        existente.setNumeroCelular(NumeroCelular.of(req.numeroCelular()));
        if (req.endereco() != null) {
            existente.setEndereco(new Endereco(
                    req.endereco().rua(), req.endereco().cep(), req.endereco().numeroCasa()));
        }
    }

    public ProprietarioResponse toResponse(Proprietario p) {
        EnderecoResponse endResp = p.getEndereco() != null
                ? new EnderecoResponse(p.getEndereco().getRua(), p.getEndereco().getCep(), p.getEndereco().getNumeroCasa())
                : null;
        List<VeiculoResponse> veicResp = p.getVeiculos().stream().map(this::toVeiculoResponse).toList();
        return new ProprietarioResponse(p.getId(), p.getNome(), p.getEmail(),
                p.getDocumento().getFormatted(), p.getNumeroCelular().getFormatted(), endResp, veicResp);
    }

    private VeiculoResponse toVeiculoResponse(Veiculo v) {
        return new VeiculoResponse(v.getId(), v.getProprietarioId(), v.getPlaca().getFormatted(),
                v.getMarca(), v.getModelo(), v.getAno(), v.getCor());
    }
}