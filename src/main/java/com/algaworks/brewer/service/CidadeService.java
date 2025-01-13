package com.algaworks.brewer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.CidadeRepository;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeCidadeException;

import java.util.Optional;

import javax.persistence.PersistenceException;

@Service
public class CidadeService {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	@Transactional
	public void salvar(Cidade cidade) {
		Optional<Cidade> cidadeExistente = cidadeRepository.findByNomeAndEstado(cidade.getNome(), cidade.getEstado());
		if(cidadeExistente.isPresent()) {
			throw new NomeCidadeException("Nome de cidade já cadastrada!");
		}
		
		cidadeRepository.save(cidade);
		
	}
	
	@Transactional
	public void excluir(Cidade cidade) {
		try {
			cidadeRepository.delete(cidade);
			cidadeRepository.flush();
		} catch(PersistenceException p) {
			throw new ImpossivelExcluirEntidadeException("Impossível excluir cidade. Talvez esteja sendo utilizada em algum cadastro.");
		}
	}

}
