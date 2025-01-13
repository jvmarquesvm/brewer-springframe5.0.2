package com.algaworks.brewer.repository.helper.cerveja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;

public interface CidadeRepositoryQueries {
	
	public Page<Cidade> filtrar(CidadeFilter cidadeFilter, Pageable pageable);

}
