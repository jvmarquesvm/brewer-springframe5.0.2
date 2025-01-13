package com.algaworks.brewer.model;

import com.algaworks.brewer.repository.UsuarioRepository;

public enum StatusUsuario {
	
	ATIVAR {
		@Override
		public void executar(Long[] codigos, UsuarioRepository usuario) {
			usuario.findByCodigoIn(codigos).forEach(u -> u.setAtivo(true));
		}
		
	},	
	DESATIVAR{
		@Override
		public void executar(Long[] codigos, UsuarioRepository usuario) {
			usuario.findByCodigoIn(codigos).forEach(u -> u.setAtivo(false));
		}
	};
	
	public abstract void executar(Long[] codigos, UsuarioRepository usuario);

}
