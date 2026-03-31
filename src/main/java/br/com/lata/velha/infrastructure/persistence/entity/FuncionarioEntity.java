package br.com.lata.velha.infrastructure.persistence.entity;
 
import jakarta.persistence.*;
import lombok.Data;
 
@Entity
@Table(name = "FUNCIONARIO")
@Data
public class FuncionarioEntity {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;
 
    @Column(name = "NOME", nullable = false)
    private String nome;
 
    @Column(name = "USER_NAME", nullable = false)
    private String username;
 
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ATIVO", nullable = false)
    private Boolean ativo = true;

    @ManyToOne
    @JoinColumn(name = "cargo_id")
    private CargoEntity cargo;
}
 