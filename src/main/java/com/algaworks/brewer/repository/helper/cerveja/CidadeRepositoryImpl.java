package com.algaworks.brewer.repository.helper.cerveja;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class CidadeRepositoryImpl implements CidadeRepositoryQueries {

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	PaginacaoUtil paginacaoUtil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public  Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable ) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		
		/* PaginacaoUtil.class
		 * 
		 * //Paginação //Configurando a Paginação int totalRegistrosPorPagina =
		 * pageable.getPageSize(); int paginaAtual = pageable.getPageNumber(); int
		 * primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		 * 
		 * criteria.setFirstResult(primeiroRegistro);
		 * criteria.setMaxResults(totalRegistrosPorPagina);
		 * 
		 * //Ordenação Sort sort = pageable.getSort();
		 * System.out.println("Ordenação Tipo: " + sort);
		 * 
		 * if(sort != null) { //Ordenando apenas por 1 campo, mas pode ser vários
		 * Sort.Order order = sort.iterator().next(); String property =
		 * order.getProperty();
		 * 
		 * criteria.addOrder(order.isAscending() ? Order.asc(property) :
		 * Order.desc(property) ); }
		 */
		paginacaoUtil.preparar(criteria, pageable);
		
		adicionarFiltro(filtro, criteria);
		criteria.createAlias("estado", "e");
		
		/*
		 * if (filtro != null) { if (!StringUtils.isEmpty(filtro.getNome())) {
		 * criteria.add(Restrictions.ilike("nome", filtro.getNome(),
		 * MatchMode.ANYWHERE)); } }
		 */
		
		//return criteria.list();
		return new PageImpl<>(  criteria.list(), pageable, total(filtro) ) ;
	}
	
	private Long total(CidadeFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cidade.class);
		//Contando o total de registro para o filtro especificado
		// Isso vai fazer o paginador mostrar corretamente o numero de pagina
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}
	
	private void adicionarFiltro(CidadeFilter filtro, Criteria criteria) {
		if (filtro != null) {			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteria.add(Restrictions.ilike("nome", filtro.getNome(), MatchMode.ANYWHERE));
			}
			
			if ( filtro.getEstado() != null ) {
				criteria.add(Restrictions.eq("estado", filtro.getEstado() )); 
			}
			 
		}
	}
}
