package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.CidadeRepository;
import com.algaworks.brewer.repository.ClienteRepository;
import com.algaworks.brewer.service.exception.CpfCpnjClienteCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class CadastroClienteService {
	
	@Autowired
	ClienteRepository clienteRepository;
	
	@Autowired
	CidadeRepository cidadeRepository;
	
	@Transactional
	public void salvar(Cliente cliente) {
		if(cliente.isNovo()) {
			Optional<Cliente> clienteOptional = clienteRepository.findByCpfCnpj(cliente.getCpfCnpjSemMascara());
			if(clienteOptional.isPresent()) {
				throw new CpfCpnjClienteCadastradoException("CPF/CNPJ já cadastrado!");
			}
		}
		clienteRepository.save(cliente);
	}
	
	@Transactional(readOnly = true)
	public void comporDadosEndereco(Cliente cliente) {
		
		/*
		 * com criteria
		 * 		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		criteria.createAlias("estado", "e", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);			
		return (Cidade) criteria.uniqueResult();
		 * 
		 * */
		if (cliente.getEndereco() == null 
				|| cliente.getEndereco().getCidade() == null
				|| cliente.getEndereco().getCidade().getCodigo() == null) {
			return;
		}

		Cidade cidade = this.cidadeRepository.findByCodigoFetchingEstado(cliente.getEndereco().getCidade().getCodigo());

		cliente.getEndereco().setCidade(cidade);
		cliente.getEndereco().setEstado(cidade.getEstado());
	}
	
	@Transactional
	public void excluir(Cliente cliente) {
		try {
			this.clienteRepository.delete(cliente);
			this.clienteRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cliente. Já está atrelado a alguma venda.");
		}
	}

}
