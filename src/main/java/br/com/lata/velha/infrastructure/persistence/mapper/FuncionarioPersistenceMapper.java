package br.com.lata.velha.infrastructure.persistence.mapper;

import br.com.lata.velha.domain.model.Cargo;
import br.com.lata.velha.domain.model.Funcionario;
import br.com.lata.velha.domain.model.Role;
import br.com.lata.velha.domain.valueObject.Senha;
import br.com.lata.velha.infrastructure.persistence.entity.CargoEntity;
import br.com.lata.velha.infrastructure.persistence.entity.FuncionarioEntity;
import br.com.lata.velha.infrastructure.persistence.entity.RoleEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class FuncionarioPersistenceMapper {

    private final PasswordEncoder passwordEncoder;

    public FuncionarioPersistenceMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // --- Entity → Domain ---

    public Funcionario toDomain(FuncionarioEntity entity) {
        if (entity == null) return null;

        Senha senha = Senha.fromHash(
                entity.getPassword(),
                (plana, hash) -> passwordEncoder.matches(plana, hash)
        );

        return new Funcionario(
                entity.getId(),
                entity.getNome(),
                entity.getUsername(),
                senha,
                toDomain(entity.getCargo()),
                entity.getAtivo() != null ? entity.getAtivo() : true
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
        return new Role(entity.getId(), entity.getNome());
    }

    // --- Domain → Entity ---

    public FuncionarioEntity toEntity(Funcionario model) {
        if (model == null) return null;

        var entity = new FuncionarioEntity();
        entity.setId(model.getId());
        entity.setNome(model.getNome());
        entity.setUsername(model.getUsername());
        
        if (model.getSenha() != null) {
            entity.setPassword(model.getSenha().getHash());
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