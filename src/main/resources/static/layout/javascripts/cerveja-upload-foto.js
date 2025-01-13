/**
 * Função para fazer o upload da foto
 */
var Brewer = Brewer || {};

Brewer.UploadFoto = ( function(){
	
	function UploadFoto(){
		this.inputNomeFoto = $("input[name=foto]");
		this.inputNomeContentType = $("input[name=contentType]");
		this.htmlFotoCervejaTemplate = $("#foto-cerveja").html();
		this.template = Handlebars.compile(this.htmlFotoCervejaTemplate);
		
		this.containerFotoCerveja = $(".js-foto-container-cerveja");
		this.uploadDrop = $("#upload-drop");
		
		this.novaFoto = $("input[name=novaFoto]");
		this.inputUrlFoto = $("input[name=urlFoto]");
		this.imgLoading=$(".js-img-loading ");
	}
	
	UploadFoto.prototype.iniciar = function() {
		var settings = {
			type: "json",
			filelimit: 1,
			allow: "*.(jpg|jpeg|png)",
			action: this.containerFotoCerveja.data("url-fotos"),
			complete: onUploadCompleto.bind(this),
			beforeSend: adicionarCsrfToken,
			loadstart: onLoadStart.bind(this)
		}
		 
		UIkit.uploadSelect($("#upload-select"), settings);
		UIkit.uploadDrop(this.uploadDrop, settings);
		
		if ( this.inputNomeFoto.val()){
			renderizarFoto.call( this, {nome: this.inputNomeFoto.val(), 
			                             contentType: this.inputNomeContentType.val(),
                                          url: this.inputUrlFoto.val()
										});
		}
	}
	
	function onLoadStart(){
		this.imgLoading.removeClass("hidden");
	}
	
	function onUploadCompleto(resposta) {
	    this.novaFoto.val("true");
        this.inputUrlFoto.val(resposta.url);
		this.imgLoading.addClass("hidden");
		renderizarFoto.call(this, resposta);
	}
	
	function renderizarFoto(resposta){
		this.inputNomeFoto.val(resposta.nome);
		this.inputNomeContentType.val(resposta.contentType);
		
		this.uploadDrop.addClass("hidden");
		//Se for uma foto nova, buscar do temp se não buscar do destino final
		//Comentado devido o uso do S3
		//var foto = '';
		//if (this.novaFoto.val() == 'true'){
		//  foto = 'temp/';
		//}
		//foto += resposta.nome;
		
		//Retirado devido a edição da cerveja*/
		//var htmlFotoCerveja = this.template({ nomeFoto: resposta.nome});*/
		//Comentado devido o uso do S3
		//var htmlFotoCerveja = this.template({ foto: foto});
		var htmlFotoCervejaS3 = this.template({ url: resposta.url});
		
		//this.containerFotoCerveja.append(htmlFotoCerveja);
		this.containerFotoCerveja.append(htmlFotoCervejaS3);
		
		$(".js-remove-foto").on("click", onRemoverFoto.bind(this));
	}
	
	function onRemoverFoto() {
		$(".js-foto-cerveja").remove();
		this.uploadDrop.removeClass("hidden");
		this.inputNomeFoto.val("");
		this.inputNomeContentType.val("");
		this.novaFoto.val("false");
	}
	
	/*Adicionar configuração de CSRF*/
	function adicionarCsrfToken(xhr) {
		var token = $("input[name=_csrf]").val();
		var header = $("input[name=_csrf_header]").val();
		
		xhr.setRequestHeader(header, token);
	}
	
	return UploadFoto;
})();

$( function(){
	var uploadFoto = new Brewer.UploadFoto();
	uploadFoto.iniciar();
});