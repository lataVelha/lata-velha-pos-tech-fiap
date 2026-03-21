package br.com.lata.velha.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ROLE")
@Data
public class Role {

    @Id
    @GeneratedValue
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "NOME", nullable = false)
    private String nome;

}
