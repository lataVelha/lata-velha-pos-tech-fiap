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
                toDomain(entity.getCargo())
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
}