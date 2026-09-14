package br.edu.fatecpg.backend_pulseira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class BackendPulseiraApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendPulseiraApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void exibirEnderecoDaApi(ApplicationReadyEvent evento) {
		Environment environment = evento.getApplicationContext().getEnvironment();
		String porta = environment.getProperty("local.server.port", "8080");
		String uriMongo = environment.getProperty("spring.mongodb.uri", "mongodb://localhost:27017/pulseira_db");
		boolean usaAtlas = uriMongo.startsWith("mongodb+srv://");

		System.out.println();
		System.out.println("==================================================");
		System.out.println(" API da pulseira iniciada");
		System.out.println(" Endereco: http://localhost:" + porta);
		System.out.println(" Banco de dados: " + (usaAtlas ? "ATLAS" : "LOCAL"));
		System.out.println(" Conexao MongoDB: " + (usaAtlas ? "Atlas configurado" : "localhost:27017"));
		System.out.println("==================================================");
		System.out.println();
	}

}
