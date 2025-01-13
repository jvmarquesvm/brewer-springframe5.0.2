package com.algaworks.brewer.repository.helper.cerveja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClienteFilter;

public interface ClienteRepositoryQueries {
	
	public Page<Cliente> filtrar(ClienteFilter clienteFilter, Pageable pageable);
	 
}
