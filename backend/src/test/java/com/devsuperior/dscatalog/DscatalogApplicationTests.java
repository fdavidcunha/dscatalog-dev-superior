package com.devsuperior.dscatalog;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DscatalogApplicationTests {

	@Test
	void contextLoads() {
		
		// Um contexto no springboot corresponde a todos componentes e infraestrutura básica de um projeto spring.
		// É onde são carregados os componentes, as injeções de dependência. Toda a infraestrutura do projeto.
		// Se o contexto tiver que ser carregado em todo o teste que for feito, deixará a aplicação muito lenta.
		// ---
		// @SpringBootTest -> Carrega o contexto da aplicação (teste de integração). 
		//                    Teste lento. Vai precisar carregar muita coisa para o teste rodar.
		//                    Quando usar? Nos testes de integração. Quando quiser carregar propositalmente todos os componentes do sistema.
		// ---
		// @SpringBootTest 
		// @AutoConfigureMockMvc -> A combinação dessas duas anotation carrega o contexto da aplicação mas não sobe o serviço do Tomcat. 
		//                          Quando usar? Nos testes de integração e WEB.
		// ---
		// @WebMvcTest(Class.class) -> Carrega o contexto, porém somente da camada WEB. Não carrega repositoty e service, por exemplo.
		//                             Quando usar? Nos testes de controlador.
		// ---
		// @ExtendWith(SpringExtension.class) -> Não carrega o contexto, mas permite usar os recursos do Spring com JUnit.
		//                                       Quando usar? Nos testes de unidade: service/component.
		// ---
		// @DataJpaText -> Carrega somente os componentes relacionados ao Spring Data JPA.
		//                 Cada teste é transacional e dá rollback ao final.
		//                 Quando usar? Nos testes de repository.
	}
	
	// FIXTURES -> É uma forma de organizar melhor o código dos testes e evitar repetições.
	// 
	// |------------------------------------------------------------------------------------------------------|
	// | JUnit 5        |    JUnit 4      |     Objetivo                                                      |
	// |------------------------------------------------------------------------------------------------------|
	// | @BeforeAll     | @BeforeClass    | Preparação antes de todos os testes da classe (método estático).  |
	// | @AfterAll      | @AfterClass     | Preparação depois de todos os testes da classe (método estático). |
	// | @BeforeEach    | @Before         | Preparação antes de cada teste da classe.                         |
	// | @AfterEach     | @After          | Preparação depois de cada teste da classe.                        |
	// |------------------------------------------------------------------------------------------------------|
	
	// Mockito x @MockBean
	//
	// |------------------------------------------------------------------------------------------------------|
	// | @Mock                                  | Usar quando a classe de teste não carrega o contexto da     |
	// | private MyComp myComp;                 | aplicação. É mais rápido e enxuto.                          |
	// | ou                                     | Quando a classe usar a anotação @ExtendWith                 |
	// | myComp = Mockito.mock(MyComp.class);   |                                                             |
	// |------------------------------------------------------------------------------------------------------|
	// | @MockBean                              | Usar quando a classe de teste carrega o contexto da         |
	// | private MyComp myComp;                 | aplicação e precisa mockar algum bean do sistema.           |
	// |                                        | Quando a classe usar as anotações @WebMvcTest e             |
	// |                                        | @SpringBootTest                                             |
	// |------------------------------------------------------------------------------------------------------|
	
	
	
}
