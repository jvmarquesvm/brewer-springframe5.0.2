package com.algaworks.brewer.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GeracaoSenha {
	
	public static void main(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("admin"));
		//$2a$10$W3lTCwQK0/0q/oJNKCBnbO8x6eKlCvucTAP9ielvRRWR0CGwAi.AK
	}

}
