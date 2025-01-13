package com.algaworks.brewer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.helper.cerveja.CervejasRepositoryQueries;

/**
 * 
 * 
 * @author joaovictor
 * CervejasRepository é nome basepath - as implementações deve ter esse nome como base
 * Impl - configuração abaixo
 * @EnableJpaRepositories(basePackageClasses = CervejasRepository.class, enableDefaultTransactions = false) //, repositoryImplementationPostfix = "Impl" )
 */

@Repository
public interface CervejasRepository extends JpaRepository<Cerveja, Long>, CervejasRepositoryQueries {

}
