package com.algaworks.brewer.model;

import javax.validation.constraints.Size;

public class CervejaEstilo {
	@Size(max = 50, min = 1, message = "Descrição deve estar entre 1 e 50")
	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	

}
