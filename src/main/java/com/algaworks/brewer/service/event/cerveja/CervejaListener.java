package com.algaworks.brewer.service.event.cerveja;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.algaworks.brewer.controller.storage.FotoStorage;

@Component
public class CervejaListener {
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@EventListener(condition = "#evento.temFoto() and #evento.isNovaFoto()")
	public void cervejaSalva(CervejaSalvaEvent evento) {
		System.out.println("Nova Cerveja Salva - " + evento.getCerveja().getNome() );
		
		fotoStorage.salvar(evento.getCerveja().getFoto());
	}
}
