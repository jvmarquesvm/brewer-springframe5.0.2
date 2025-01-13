package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.CervejasRepository;
import com.algaworks.brewer.service.event.cerveja.CervejaSalvaEvent;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CervejaService {
	
	@Autowired
	private CervejasRepository cervejaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja) {
		cervejaRepository.save(cerveja);
		//Comentado para implementar o S3
		//publisher.publishEvent(new CervejaSalvaEvent(cerveja));
	}
	
	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();
			cervejaRepository.delete(cerveja);
			cervejaRepository.flush();
			
			fotoStorage.excluir(foto);
		} catch(PersistenceException p) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cerveja. Já foi usada em alguma venda");
		}
	}
}
