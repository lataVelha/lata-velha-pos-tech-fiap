package br.com.lata.velha.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SERVICO")
@Data
public class ServicoEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String nome;
    private String descricao;
}