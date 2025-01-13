package com.algaworks.brewer.repository.helper.cerveja;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.UsuarioFilter;

public interface UsuarioRepositoryQueries {

	public Optional<Usuario> porEmailAtivo(String email);
	public List<String> permissoes(Usuario usuario);
	public List<Usuario> filtrar(UsuarioFilter usuarioFilter);
	public Page<Usuario> filtrar(UsuarioFilter usuarioFilter, Pageable pageable);
	public Usuario buscarComGrupos(Long codigo);
}
