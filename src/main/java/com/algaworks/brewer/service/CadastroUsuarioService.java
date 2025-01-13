package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.StatusUsuario;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.UsuarioRepository;
import com.algaworks.brewer.service.exception.EmailUsuarioCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Service
public class CadastroUsuarioService {
	
	@Autowired
	UsuarioRepository usuarioRepository;
	
	@Autowired
	private PasswordEncoder passwordEconder;
	
	@Transactional
	public void salvar(Usuario usuario) {
		Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(usuario.getEmail());
		
		if(usuarioOptional.isPresent() && !usuarioOptional.get().equals(usuario)) {
			throw new EmailUsuarioCadastradoException("Email já cadastrado!");
		}
		
		if(usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())) {
			throw new SenhaObrigatoriaUsuarioException("Senha é obrigatória para novo usuário");
		}
		
		if(usuario.isNovo()) {
			usuario.setSenha(this.passwordEconder.encode( usuario.getSenha()) );
			usuario.setConfirmacaoSenha(usuario.getSenha());
		}
		
		if(usuario.isEdicao() && StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(usuarioOptional.get().getSenha());
			usuario.setConfirmacaoSenha(usuarioOptional.get().getSenha());
		}
		
		if(usuario.isEdicao() && usuario.getAtivo() == null ) {
			usuario.setAtivo(usuarioOptional.get().getAtivo());
		}
		
		
		usuarioRepository.save(usuario);
	}
	
	@Transactional
	public void alterarStatus(Long[] codigos, StatusUsuario status) {
//		if (status.equals("ATIVAR")) {
//			usuarioRepository.findByCodigoIn(codigos).forEach(u -> u.setAtivo(true));
//		}
//		if (status.equals("DESATIVAR")) {
//			usuarioRepository.findByCodigoIn(codigos).forEach(u -> u.setAtivo(false));
//		}
		status.executar(codigos, usuarioRepository);
	}

	@Transactional
	public void excluir(Usuario usuario) {
		try {
			this.usuarioRepository.delete(usuario);
			this.usuarioRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar usuário.");
		}
	}

}
