package br.com.lata.velha.domain.repository;

import br.com.lata.velha.domain.model.ServicoOS;

public interface ServicoOSRepository {

    ServicoOS save(ServicoOS servicoOS);

    ServicoOS findById(Long id);

}