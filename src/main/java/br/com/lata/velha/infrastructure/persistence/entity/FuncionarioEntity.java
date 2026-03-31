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
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARGO_ID", nullable = false)
    private CargoEntity cargo;
}
 