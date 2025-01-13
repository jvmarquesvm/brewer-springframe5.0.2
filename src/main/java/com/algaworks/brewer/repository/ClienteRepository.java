package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.helper.cerveja.ClienteRepositoryQueries;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, ClienteRepositoryQueries{

	public Optional<Cliente> findByCpfCnpj(String cpfCnpj);

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
	
	//@Query("select c from Cliente c join rigth Endereco e on c.endereco = e.codigo  where c.codigo = :codigo")
	//public Cliente findClientByCodigoFetchingCidade(@Param("codigo") Long codigo);
	
}
