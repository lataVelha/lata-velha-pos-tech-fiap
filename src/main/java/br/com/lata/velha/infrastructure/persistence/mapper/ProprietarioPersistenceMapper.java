package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.Proprietario;
import br.com.lata.velha.domain.model.Veiculo;
import br.com.lata.velha.domain.valueObject.Documento;
import br.com.lata.velha.domain.valueObject.Endereco;
import br.com.lata.velha.domain.valueObject.NumeroCelular;
import br.com.lata.velha.domain.valueObject.Placa;
import br.com.lata.velha.infrastructure.persistence.entity.EnderecoEmbeddable;
import br.com.lata.velha.infrastructure.persistence.entity.ProprietarioEntity;
import br.com.lata.velha.infrastructure.persistence.entity.VeiculoEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProprietarioPersistenceMapper {

    // --- Entity → Domain ---

    public Proprietario toDomain(ProprietarioEntity entity) {
        if (entity == null) return null;

        Endereco endereco = null;
        if (entity.getEndereco() != null) {
            endereco = new Endereco(
                    entity.getEndereco().getRua(),
                    entity.getEndereco().getCep(),
                    entity.getEndereco().getNumeroCasa()
            );
        }

        Proprietario proprietario = new Proprietario(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                Documento.of(entity.getDocumento()),
                NumeroCelular.of(entity.getNumeroCelular()),
                endereco
        );
        proprietario.setAtivo(entity.isAtivo());

        if (entity.getVeiculos() != null) {
            List<Veiculo> veiculos = entity.getVeiculos().stream()
                    .map(this::veiculoToDomain)
                    .toList();
            proprietario.setVeiculos(veiculos);
        }

        return proprietario;
    }

    private Veiculo veiculoToDomain(VeiculoEntity entity) {
        Veiculo veiculo = new Veiculo(
                entity.getId(),
                entity.getProprietario().getId(),
                Placa.of(entity.getPlaca()),
                entity.getMarca(),
                entity.getModelo(),
                entity.getAno(),
                entity.getCor()
        );
        veiculo.setAtivo(entity.isAtivo());
        return veiculo;
    }

    // --- Domain → Entity ---

    public ProprietarioEntity toEntity(Proprietario domain) {
        if (domain == null) return null;

        ProprietarioEntity entity = new ProprietarioEntity();
        entity.setId(domain.getId());
        entity.setNome(domain.getNome());
        entity.setEmail(domain.getEmail());
        entity.setDocumento(domain.getDocumento().getValor());
        entity.setNumeroCelular(domain.getNumeroCelular().getValor());
        entity.setAtivo(domain.isAtivo());

        if (domain.getEndereco() != null) {
            EnderecoEmbeddable end = new EnderecoEmbeddable();
            end.setRua(domain.getEndereco().getRua());
            end.setCep(domain.getEndereco().getCep());
            end.setNumeroCasa(domain.getEndereco().getNumeroCasa());
            entity.setEndereco(end);
        }

        return entity;
    }
}