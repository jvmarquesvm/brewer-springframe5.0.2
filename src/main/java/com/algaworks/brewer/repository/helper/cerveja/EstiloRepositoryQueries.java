package com.algaworks.brewer.repository.helper.cerveja;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;

public interface EstiloRepositoryQueries {
	
	//public List<Estilo> filtrar(EstiloFilter estiloFilter, Pageable pageable);
	public Page<Estilo> filtrar(EstiloFilter estiloFilter, Pageable pageable);
	 
}
