package com.algaworks.brewer.controller.storage.local;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.controller.storage.FotoStorage;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;

//Anotation para usar o S3 ou URL local
@Profile("local")
@Component
public class FotoStorageLocal implements FotoStorage  {
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageLocal.class);
	private Path local;
	private Path localTemp;
	
	private static final String THUMBNAIL_PREFIX = "thumbnail.";
	
	public FotoStorageLocal() {
		this( FileSystems.getDefault().getPath(System.getenv("home"), ".brewerfotos") );
		logger.info("Pastas criadas para armazenar as fotos " + this.local);
	}
	
	public FotoStorageLocal(Path path) {
		this.local = path;
		criarPastas();
	}
	
	private void criarPastas() {
		try {
			Files.createDirectories(this.local);
			this.localTemp = FileSystems.getDefault().getPath(this.local.toString(), "temp");
			Files.createDirectories(this.localTemp);
			
			logger.info("Pastas criadas para armazenar as fotos");
			logger.info("Pasta default " + this.local.toAbsolutePath());
			logger.info("Pasta Temporária " + this.localTemp.toAbsolutePath());
			
		} catch (IOException e) {
			throw new RuntimeException("Erro criando pasta para salvar foto", e);
		}
	}

	@Override
	public String salvaFotostemporario(MultipartFile[] files) {
		System.out.println("Salvando Temporariamente");
		String novoNome = null;
		if ( files != null && files.length > 0 ) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename() );
			
			try {
				arquivo.transferTo(new File(this.localTemp.toAbsolutePath().toString() 
						                      + FileSystems.getDefault().getSeparator() 
						                        +  novoNome ));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando foto na pasta temporária", e);
			}
		}
		
		return novoNome;
	}
	
	//Método movido para a interface FotoStorage
	/*private String renomearArquivo(String nomeOriginal) {
		String novoNome = UUID.randomUUID().toString() + "_" + nomeOriginal;
		
		logger.info(String.format("Nome Original %s,  novo nome: %s", nomeOriginal, novoNome));
		
		return novoNome;
	}*/
	
	@Override
	public byte[] recuperarFotoTemporaria(String nomeFoto) {
		try {
			return Files.readAllBytes(this.localTemp.resolve(nomeFoto));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo foto temporaria", e);
		}
	}
	
	@Override
	public byte[] recuperar(String nomeFoto) {
		try {
			return Files.readAllBytes(this.local.resolve(nomeFoto));
		} catch (IOException e) {
			throw new RuntimeException("Erro lendo foto", e);
		}
	}
	
	@Override
	public void salvar(String foto) {
		//Para salvar de fato, mover do local temporário para o local definitivo
		//Comentado para implementar o S3
		///try {
		//	Files.move(this.localTemp.resolve(foto), this.local.resolve(foto) );
		//} catch (IOException e) {
		//	throw new RuntimeException("Erro movendo a foto para o destino final", e);
		//}
		
		try {
			Thumbnails.of(this.local.resolve(foto).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}
	}
	
	@Override
	public byte[] recuperarThumbnail(String fotoCerveja) {
		return recuperar( THUMBNAIL_PREFIX + fotoCerveja );
	}
	
	@Override
	public void excluir(String foto) {
		try {
			Files.deleteIfExists(this.local.resolve(foto));
			Files.deleteIfExists(this.local.resolve(THUMBNAIL_PREFIX + foto));
		} catch (IOException e) {
			logger.warn(String.format("Erro apagando foto '%s'. Mensagem: '%s'",foto, e.getMessage()) );
		}
		
	}
	
	//Método para salvar Localmente
	@Override
	public String salvar(MultipartFile[] files) {
		System.out.println("Salvando com URL Local");
		String novoNome = null;
		if ( files != null && files.length > 0 ) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename() );
			
			try {
				arquivo.transferTo(new File(this.local.toAbsolutePath().toString() 
						                      + FileSystems.getDefault().getSeparator() 
						                        +  novoNome ));
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando foto localmente", e);
			}
		}
		
		try {
			Thumbnails.of(this.local.resolve(novoNome).toString()).size(40, 68).toFiles(Rename.PREFIX_DOT_THUMBNAIL);
		} catch (IOException e) {
			throw new RuntimeException("Erro gerando thumbnail", e);
		}
		
		return novoNome;
	}

	/**
	 * Para uso no S3
	 */
	@Override
	public String getUrl(String foto) {
		return "http://localhost:8080/brewer/fotos/" + foto;
	}
}
