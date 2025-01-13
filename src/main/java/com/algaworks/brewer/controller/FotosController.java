package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.algaworks.brewer.controller.storage.FotoStorageRunnable;
import com.algaworks.brewer.dto.FotoDTO;

@RestController
@RequestMapping(value = "/fotos")
public class FotosController {
	
	@Autowired
	private FotoStorage fotoStorage;
	
	//@RequestMapping(method = RequestMethod.POST)
//	@PostMapping
//	public String upload( @RequestParam("files[]") MultipartFile[] files ) {
//		System.out.println(files.length);
//		System.out.println(files[0].getSize());
//		return "OK!";
//	}
	
	
	//@RequestMapping(method = RequestMethod.POST)
	@PostMapping
	public DeferredResult<FotoDTO> upload( @RequestParam("files[]") MultipartFile[] files ) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<FotoDTO>();
		
		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage ));
		thread.start();

		return resultado;
	}
	
	@GetMapping("/temp/{nome:.*}")
	public byte[] recuperarFotoTemporario(@PathVariable("nome") String nomeFoto) {
		return fotoStorage.recuperarFotoTemporaria(nomeFoto);
	}
	
	@GetMapping("/{nome:.*}")
	public byte[] recuperar(@PathVariable("nome") String nomeFoto) {
		return fotoStorage.recuperar(nomeFoto);
	}

}
