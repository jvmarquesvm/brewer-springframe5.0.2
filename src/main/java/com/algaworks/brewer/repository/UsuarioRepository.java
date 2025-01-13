package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.helper.cerveja.UsuarioRepositoryQueries;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioRepositoryQueries {

	public Optional<Usuario> findByEmail(String email);
	//public Optional<Usuario> findByEmailIgnoreCaseAndAditivoTrue(String email);

	public List<Usuario> findByCodigoIn(Long[] codigos);


}
