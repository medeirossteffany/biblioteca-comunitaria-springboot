package com.biblioteca;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal da aplicação Biblioteca.
 * Responsável por inicializar a aplicação Spring Boot.
 */

@SpringBootApplication
public class BibliotecaApplication {
	public static void main(String[] args) {
		SpringApplication.run(BibliotecaApplication.class, args);
	}
}
