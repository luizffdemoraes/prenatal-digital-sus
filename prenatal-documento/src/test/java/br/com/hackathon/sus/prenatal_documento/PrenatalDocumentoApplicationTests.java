package br.com.hackathon.sus.prenatal_documento;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PrenatalDocumentoApplicationTests {

	@Test
	@DisplayName("Deve carregar o contexto da aplicação")
	void shouldLoadApplicationContext() {
	}

}
