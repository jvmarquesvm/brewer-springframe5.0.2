package com.algaworks.brewer.controller.storage.s3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.controller.storage.FotoStorage;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;

import net.coobird.thumbnailator.Thumbnails;

//Anotation para usar o S3 e localmente
@Profile("prod")
@Component
public class FotoStorageS3 implements FotoStorage {
	
	@Autowired
	private AmazonS3 amazons3;
	
	private static final String BUCKET = "awbrewerjvm";
	private static final Logger logger = LoggerFactory.getLogger(FotoStorageS3.class);

	@Override
	public String salvaFotostemporario(MultipartFile[] files) {
		return null;
	}

	private ObjectMetadata  enviarFoto( String novoNome, MultipartFile arquivo,
			                                     AccessControlList acl) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(arquivo.getContentType());
		metadata.setContentLength(arquivo.getSize());
		
		amazons3.putObject(new PutObjectRequest(BUCKET, novoNome, 
				               arquivo.getInputStream(), metadata)
				                         .withAccessControlList(acl));
		return metadata;
	}

	private void  enviarFotoThumb(String novoNome, MultipartFile arquivo, AccessControlList acl) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Thumbnails.of(arquivo.getInputStream()).size(40, 68).toOutputStream(os);
		byte[] byteArray = os.toByteArray();
		InputStream is = new ByteArrayInputStream(byteArray);
		
		ObjectMetadata thumbMetadata = new ObjectMetadata();
		thumbMetadata.setContentType(arquivo.getContentType());
		thumbMetadata.setContentLength(byteArray.length);
		
		amazons3.putObject(new PutObjectRequest(BUCKET, THUMB_PREFIX + novoNome, is, 
				                                    thumbMetadata)
				               .withAccessControlList(acl));
	}

	@Override
	public byte[] recuperarFotoTemporaria(String nomeFoto) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void salvar(String foto) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public byte[] recuperar(String foto) {
		InputStream is = amazons3.getObject(BUCKET, foto).getObjectContent();
		try {
			return IOUtils.toByteArray(is);
		} catch (IOException e) {
			logger.error("Não conseguiu recuperar foto do S3", e);
		}
		return null;
	}

	@Override
	public byte[] recuperarThumbnail(String foto) {
		return recuperar(FotoStorage.THUMB_PREFIX + foto);
	}

	@Override
	public void excluir(String foto) {
		amazons3.deleteObjects(new DeleteObjectsRequest(BUCKET).withKeys(foto, THUMB_PREFIX + foto));
	}
	
	//Implementação para o S3
	@Override
	public String salvar(MultipartFile[] files) {
		String novoNome = null;
		
		if (files != null && files.length > 0) {
			MultipartFile arquivo = files[0];
			novoNome = renomearArquivo(arquivo.getOriginalFilename());
			try {
				AccessControlList acl = new AccessControlList();
				acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
				
				enviarFoto(novoNome, arquivo, acl);
				enviarFotoThumb(novoNome, arquivo, acl);
			} catch (IOException e) {
				throw new RuntimeException("Erro salvando no S3", e );
			}
		}
		return novoNome;
	}

	@Override
	public String getUrl(String foto) {
		if(!StringUtils.isEmpty(foto)) {
			return "https://awbrewerjvm.s3.us-east-2.amazonaws.com/" + foto;
		}
		return null;
	}
	

	

}
