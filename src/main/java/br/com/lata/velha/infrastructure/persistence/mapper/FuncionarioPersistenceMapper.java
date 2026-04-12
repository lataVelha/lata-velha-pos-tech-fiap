package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.authentication.domain.services.PasswordHasher;
import br.com.lata.velha.authentication.domain.valueObjects.Credential;
import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.authentication.domain.entities.Role;
import br.com.lata.velha.infrastructure.persistence.entity.CargoEntity;
import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import br.com.lata.velha.infrastructure.persistence.entity.RoleEntity;
import br.com.lata.velha.shared.domain.valueObjects.RoleId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioPersistenceMapper {

    private final PasswordHasher passwordHasher;

    // --- Entity → Domain ---

    public Funcionario toDomain(FuncionarioEntity entity) {
        if (entity == null) return null;

        Credential credential = Credential.fromHash(entity.getPassword(), passwordHasher);

        return new Funcionario(
                entity.getId(),
                entity.getNome(),
                entity.getUsername(),
                credential,
                toDomain(entity.getCargo()),
                entity.isAtivo()
        );
    }

    public Cargo toDomain(CargoEntity entity) {
        if (entity == null) return null;

        var roles = entity.getRoles().stream()
                .map(this::toDomain)
                .collect(Collectors.toSet());

        return new Cargo(entity.getId(), entity.getNome(), roles);
    }

    public Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        UUID roleUuid = UUID.nameUUIDFromBytes(("role:" + entity.getId()).getBytes());
        return new Role(RoleId.create(roleUuid), entity.getNome());
    }

    // --- Domain → Entity ---

    public FuncionarioEntity toEntity(Funcionario model) {
        if (model == null) return null;

        var entity = new FuncionarioEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        entity.setUsername(model.getUsername());

        if (model.getCredential() != null) {
            entity.setPassword(model.getCredential().getHash());
        }

        entity.setCargo(toEntity(model.getCargo()));
        entity.setAtivo(model.isAtivo());

        return entity;
    }

    public CargoEntity toEntity(Cargo model) {
        if (model == null) return null;
        var entity = new CargoEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        return entity;
    }
}
