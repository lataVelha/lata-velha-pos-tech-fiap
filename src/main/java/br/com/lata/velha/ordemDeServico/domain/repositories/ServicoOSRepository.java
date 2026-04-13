package br.com.lata.velha.ordemDeServico.domain.repositories;

import br.com.lata.velha.ordemDeServico.domain.entities.ServicoOS;

public interface ServicoOSRepository {

    ServicoOS save(ServicoOS servicoOS);

    ServicoOS findById(Long id);

}