package com.algaworks.brewer.controller.storage;

import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;

public class FotoStorageRunnable implements Runnable {
	
	private MultipartFile[] files;
	private DeferredResult<FotoDTO> resultado;
	private FotoStorage fotoStorage;
	
	public FotoStorageRunnable( MultipartFile[] files, DeferredResult<FotoDTO> resultado, FotoStorage fotoStorage) {
		this.files = files;
		this.resultado = resultado;
		this.fotoStorage = fotoStorage;
	}

	@Override
	public void run() {
		System.out.println(files.length);
		System.out.println(files[0].getSize());
		System.out.println(files[0].getContentType());
		
		//String nome = files[0].getOriginalFilename();
		//Comentado devido o uso do S3
		//String nomeFoto = this.fotoStorage.salvaFotostemporario(files);
		String nomeFoto = this.fotoStorage.salvar(files);
		String contentType = files[0].getContentType();
		
		resultado.setResult(new FotoDTO( nomeFoto, contentType, this.fotoStorage.getUrl(nomeFoto)));
	}

}
