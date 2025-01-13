package com.algaworks.brewer.controller.storage;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public interface FotoStorage {
	
	public String salvaFotostemporario(MultipartFile[] files);

	public byte[] recuperarFotoTemporaria(String nomeFoto);
	
	public void salvar(String foto);

	public byte[] recuperar(String nomeFoto);
	
	public byte[] recuperarThumbnail(String fotoCerveja);

	public void excluir(String foto);
	
	//S3
	public String salvar(MultipartFile[] files);
	
	public String getUrl(String foto);
	
	public final String THUMB_PREFIX = "thumbnail.";
	
	//Método trazido do FotoStorageLocal
	default String renomearArquivo(String nomeOriginal) {
		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
		
		//logger.info(String.format("Nome Original %s,  novo nome: %s", nomeOriginal, novoNome));
		
		return novoNome;
	}
	
}
