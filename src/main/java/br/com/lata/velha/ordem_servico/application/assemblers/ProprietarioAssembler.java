package br.com.lata.velha.ordem_servico.application.assemblers;

import br.com.lata.velha.ordem_servico.application.dtos.request.ProprietarioRequest;
import br.com.lata.velha.ordem_servico.application.dtos.response.EnderecoResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.ProprietarioResponse;
import br.com.lata.velha.ordem_servico.application.dtos.response.VeiculoResponse;
import br.com.lata.velha.ordem_servico.domain.entities.Proprietario;
import br.com.lata.velha.ordem_servico.domain.entities.Veiculo;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Documento;
import br.com.lata.velha.ordem_servico.domain.valueObjects.Endereco;
import br.com.lata.velha.ordem_servico.domain.valueObjects.NumeroCelular;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProprietarioAssembler {

    public Proprietario toDomain(ProprietarioRequest req) {
        Endereco endereco = req.endereco() != null
                ? new Endereco(
                req.endereco().rua(),
                req.endereco().cep(),
                req.endereco().numeroCasa())
                : null;

        return new Proprietario(
                null,
                req.nome(),
                req.email(),
                Documento.of(req.documento()),
                NumeroCelular.of(req.numeroCelular()),
                endereco
        );
    }

    public void updateDomain(Proprietario existente, ProprietarioRequest req) {
        existente.setNome(req.nome());
        existente.setEmail(req.email());
        existente.setDocumento(Documento.of(req.documento()));
        existente.setNumeroCelular(NumeroCelular.of(req.numeroCelular()));

        if (req.endereco() != null) {
            existente.setEndereco(
                    new Endereco(
                            req.endereco().rua(),
                            req.endereco().cep(),
                            req.endereco().numeroCasa()
                    )
            );
        } else {
            existente.setEndereco(null);
        }
    }

    public ProprietarioResponse toResponse(Proprietario p) {
        EnderecoResponse endResp = p.getEndereco() != null
                ? new EnderecoResponse(
                p.getEndereco().getRua(),
                p.getEndereco().getCep(),
                p.getEndereco().getNumeroCasa()
        )
                : null;

        List<VeiculoResponse> veicResp =
                p.getVeiculos() == null
                        ? List.of()
                        : p.getVeiculos()
                        .stream()
                        .map(this::toVeiculoResponse)
                        .toList();

        return new ProprietarioResponse(
                p.getId(),
                p.getNome(),
                p.getEmail(),
                p.getDocumento().getFormatted(),
                p.getNumeroCelular().getFormatted(),
                endResp,
                veicResp
        );
    }

    private VeiculoResponse toVeiculoResponse(Veiculo v) {
        return new VeiculoResponse(
                v.getId(),
                v.getProprietarioId(),
                v.getPlaca().getFormatted(),
                v.getMarca(),
                v.getModelo(),
                v.getAno(),
                v.getCor()
        );
    }
}