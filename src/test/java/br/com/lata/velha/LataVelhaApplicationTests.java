package br.com.lata.velha;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import br.com.lata.velha.ordemDeServico.domain.repositories.PecaAlocadaRepository;

@SpringBootTest
@ActiveProfiles("test")
class LataVelhaApplicationTests {

	@MockBean
	private PecaAlocadaRepository pecaAlocadaRepository;

	@Test
	void contextLoads() {
	}

}
