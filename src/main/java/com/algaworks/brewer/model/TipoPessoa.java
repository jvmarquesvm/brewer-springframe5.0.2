package com.algaworks.brewer.model;

import com.algaworks.brewer.model.validation.*;

public enum TipoPessoa {
	
	FISICA("Física","CPF","000.000.000-00", CpfGroup.class) {
		@Override
		public String formartar(String cpfCnpj) {
			return cpfCnpj.replaceAll("(\\d{3})(\\d{3})(\\d{3})", "$1.$2.$3-");
		}
	},	
	JURIDICA("Jurídica","CNPJ","00.000.000/0000-00" , CnpjGroup.class) {
		@Override
		public String formartar(String cpfCnpj) {
			return cpfCnpj.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})", "$1.$2.$3/$4-");
		}
	};
	
	private String descricao;
	private String documento;
	private String mascara;
	private Class<?> grupo;
	
	TipoPessoa(String descricao, String documento,  String mascara, Class<?> grupo) {
		this.descricao = descricao;
		this.documento = documento;
		this.mascara = mascara;
		this.grupo = grupo;
	}
	
	public abstract String formartar(String cpfCnpj );

	public String getDescricao() {
		return descricao;
	}
	public String getDocumento() {
		return documento;
	}
	public String getMascara() {
		return mascara;
	}
	public Class<?> getGrupo() {
		return grupo;
	}
	public static String removerMascara(String cpfCpnj) {
		return cpfCpnj.replaceAll("\\.|-|/", "");
	}
	
}
