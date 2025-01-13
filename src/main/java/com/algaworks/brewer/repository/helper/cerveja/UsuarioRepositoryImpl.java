package com.algaworks.brewer.repository.helper.cerveja;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Grupo;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.model.UsuarioGrupo;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.repository.paginacao.PaginacaoUtil;

public class UsuarioRepositoryImpl implements UsuarioRepositoryQueries {
	
	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	PaginacaoUtil paginacaoUtil;

	@Override
	public Optional<Usuario> porEmailAtivo(String email) {
		// TODO Auto-generated method stub
		return manager.createQuery("select u from Usuario u where lower(u.email) = lower(:email) and u.ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst();
	}

	@Override
	public List<String> permissoes(Usuario usuario) {
		// TODO Auto-generated method stub
		return manager.createQuery("select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario", String.class)
				.setParameter("usuario", usuario)
				.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public List<Usuario> filtrar(UsuarioFilter usuarioFilter) {
		// TODO Auto-generated method stub
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		adicionarFiltro(usuarioFilter, criteria);
		return criteria.list();
	}

	private void adicionarFiltro(UsuarioFilter usuarioFilter, Criteria criteria) {
		if ( usuarioFilter != null ) {
			if(!StringUtils.isEmpty(usuarioFilter.getNome())) {
				criteria.add(Restrictions.ilike("nome", usuarioFilter.getNome(), MatchMode.ANYWHERE));
			}
			
			if (!StringUtils.isEmpty(usuarioFilter.getEmail())) {
				criteria.add(Restrictions.ilike("email", usuarioFilter.getEmail(), MatchMode.START));
			}
			
			//criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
			if(usuarioFilter.getGrupos() != null && !usuarioFilter.getGrupos().isEmpty()  ) {
				List<Criterion> subqueries = new ArrayList<>();
				for (Long codigoGrupo : usuarioFilter.getGrupos().stream().mapToLong(Grupo::getCodigo).toArray()) {
					DetachedCriteria dc = DetachedCriteria.forClass(UsuarioGrupo.class);
					dc.add(Restrictions.eq("id.grupo.codigo", codigoGrupo));
					dc.setProjection(Projections.property("id.usuario"));
					
					subqueries.add(Subqueries.propertyIn("codigo", dc));
				}
				
				Criterion[] criterions = new Criterion[subqueries.size()];
				criteria.add(Restrictions.and(subqueries.toArray(criterions)));
			}
		}
	}
	
	/**
	 * 
	 * Paginação
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(readOnly = true)
	public Page<Usuario> filtrar(UsuarioFilter usuarioFilter, Pageable pageable) {
		// TODO Auto-generated method stub
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		//criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		paginacaoUtil.preparar(criteria, pageable);
		adicionarFiltro(usuarioFilter, criteria);
		
		List<Usuario> filtrados = criteria.list();
		filtrados.forEach( usuario -> Hibernate.initialize(usuario.getGrupos()));
		return  new PageImpl<>(criteria.list(), pageable, total(usuarioFilter));
	}
	
	private Long total(UsuarioFilter filtro) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		//Contando o total de registro para o filtro especificado
		// Isso vai fazer o paginador mostrar corretamente o numero de pagina
		adicionarFiltro(filtro, criteria);
		criteria.setProjection(Projections.rowCount());
		return (Long) criteria.uniqueResult();
	}

	//Buscar uma entidade usuario/grupos com relacionamento many2many
	@Override
	@Transactional( readOnly = true)
	public Usuario buscarComGrupos(Long codigo) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Usuario.class);
		criteria.createAlias("grupos", "g", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("codigo", codigo));
		criteria.setResultTransformer(criteria.DISTINCT_ROOT_ENTITY);
		
		return (Usuario) criteria.uniqueResult();
	}
}
