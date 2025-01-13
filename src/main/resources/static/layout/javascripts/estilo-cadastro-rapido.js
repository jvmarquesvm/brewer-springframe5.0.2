var Brewer = Brewer || {};

Brewer.EstiloCadastroRapido = ( function() {

	function EstiloCadastroRapido() {
		this.modal = $('#modalCadastroRapidoEstilo');
		this.botaoSalvar = this.modal.find('.js-modal-cadastro-estilo-salvar-btn');
		this.containerMenssagemErro = $('.js-mensagem-cadastro-rapido-estilo');
		this.form = this.modal.find('form');
		this.url = this.form.attr('action');
		this.inputNomeEstilo = $('#nomeEstilo');
	}
	
	EstiloCadastroRapido.prototype.iniciar = function (){
	
		this.form.on('submit', function(event) { 
			event.preventDefault() 
		});
		
		this.modal.on('shown.bs.modal', onModalShow.bind(this));
		this.modal.on('hide.bs.modal', onModalClose.bind(this));
		this.botaoSalvar.on('click', onBotaoSalvarClick.bind(this));
	}
	
	function onModalShow() {
		this.inputNomeEstilo.focus();
	}
	
	function onModalClose() {
	    this.inputNomeEstilo.val('');
	    this.containerMenssagemErro.addClass('hidden');
	    this.form.find(".form-group").removeClass("has-error");
	}
	
	function onBotaoSalvarClick() {
		var nomeEstilo = this.inputNomeEstilo.val().trim();
		console.log(nomeEstilo);
		
		$.ajax({
			url: this.url,
			method: "post",
			contentType: "application/json",
			data: JSON.stringify({ nome: nomeEstilo}),
			error: onErroSalvandoEstilo.bind(this),
			success: onEstiloSalvo.bind(this)
		});
	}
	
	function onErroSalvandoEstilo(obj) {
		var mensagemErro = obj.responseText;
		this.containerMenssagemErro.removeClass('hidden');
		this.containerMenssagemErro.html('<span>' + mensagemErro + '</span>');
		console.log(mensagemErro);
		
		this.form.find('.form-group').addClass('has-error');
	}

	function onEstiloSalvo(estilo) {
		var comboEstilo = $('#estilo');
		comboEstilo.append("<option value=" + estilo.codigo + ">" + estilo.nome + "</option>");
		comboEstilo.val(estilo.codigo);
		this.modal.modal('hide');
	}
	
	return EstiloCadastroRapido;
})();


$(function() {
	var estiloCadastroRapido = new Brewer.EstiloCadastroRapido();
	estiloCadastroRapido.iniciar();
	
//	var modal = $('#modalCadastroRapidoEstilo');
//	var botaoSalvar = modal.find('.js-modal-cadastro-estilo-salvar-btn');
//	var containerMenssagemErro = $('.js-mensagem-cadastro-rapido-estilo');
//	
//	var form = modal.find('form');
//	form.on('submit', function(event) { 
//		event.preventDefault() 
//	});
//	
//	var url = form.attr('action');
//	var inputNomeEstilo = $('#nomeEstilo');
//	
//	modal.on('shown.bs.modal', onModalShow);
//	modal.on('hide.bs.modal', onModalClose);
//	
//	botaoSalvar.on('click', onBotaoSalvarClick);
//	
//	function onModalShow() {
//		inputNomeEstilo.focus();
//	}
//	
//	function onModalClose() {
//	    inputNomeEstilo.val('');
//	    containerMenssagemErro.addClass('hidden');
//	    form.find(".form-group").removeClass("has-error");
//	}
//	
//	function onBotaoSalvarClick() {
//		var nomeEstilo = inputNomeEstilo.val().trim();
//		console.log(nomeEstilo);
//		
//		$.ajax({
//			url: url,
//			method: "post",
//			contentType: "application/json",
//			data: JSON.stringify({ nome: nomeEstilo}),
//			error: onErroSalvandoEstilo,
//			success: onEstiloSalvo
//		});
//	}
//	
//	function onErroSalvandoEstilo(obj) {
//		var mensagemErro = obj.responseText;
//		containerMenssagemErro.removeClass('hidden');
//		containerMenssagemErro.html('<span>' + mensagemErro + '</span>');
//		console.log(mensagemErro);
//		
//		form.find('.form-group').addClass('has-error');
//	}
//	
//	function onEstiloSalvo(estilo) {
//		var comboEstilo = $('#estilo');
//		comboEstilo.append("<option value=" + estilo.codigo + ">" + estilo.nome + "</option>");
//		comboEstilo.val(estilo.codigo);
//		modal.modal('hide');
//	}
	
});