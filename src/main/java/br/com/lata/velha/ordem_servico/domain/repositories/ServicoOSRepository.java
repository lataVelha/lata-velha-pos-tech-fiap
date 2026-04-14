package br.com.lata.velha.ordem_servico.domain.repositories;

import br.com.lata.velha.ordem_servico.domain.entities.ServicoOS;

public interface ServicoOSRepository {

    ServicoOS save(ServicoOS servicoOS);

    ServicoOS findById(Long id);

}