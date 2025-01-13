package com.algaworks.brewer.repository.paginacao;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginacaoUtil {
	
	public void preparar(Criteria criteria, Pageable pageable) {
		//Paginação
		//Configurando a Paginação
		int totalRegistrosPorPagina = pageable.getPageSize();
		int paginaAtual = pageable.getPageNumber();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
		criteria.setFirstResult(primeiroRegistro);
		criteria.setMaxResults(totalRegistrosPorPagina);
		
		//Ordenação
		Sort sort = pageable.getSort();
		System.out.println("Ordenação Tipo: " + sort);
		
		if(sort != null) {
			//Ordenando apenas por 1 campo, mas pode ser vários
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property) );
		}
	}

}
