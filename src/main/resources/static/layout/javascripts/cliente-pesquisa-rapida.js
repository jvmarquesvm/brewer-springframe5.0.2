Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function(){
	function PesquisaRapidaCliente() {
		this.pesquisaRapidaCliente = $('#pesquisaRapidaCliente');
		this.nomeInput = $('#nomeClienteModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaCliente');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-rapida-cliente').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('js-mensagem-erro');
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function(){
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
		this.pesquisaRapidaCliente.on('shown.bs.modal', onModalShow.bind(this));
	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onPesquisaRapidaClicado(event){
		event.preventDefault();
	
		$.ajax({
			url: this.pesquisaRapidaCliente.find('form').attr('action'),
			method: 'get',
			contentType: 'application/json',
			data: {
				nome: this.nomeInput.val()
			},
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado){
		var html = this.template(resultado);
		//console.log('Resultado:', resultado);
		this.containerTabelaPesquisa.html(html);
		this.mensagemErro.addClass('hidden');
		
		var tabelaClientePesquisaRapida = new Brewer.TabelaClientePesquisaRapida(this.pesquisaRapidaCliente);
		tabelaClientePesquisaRapida.iniciar();
	}
	
	function onErroPesquisa(){
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaCliente;
	
}());

Brewer.TabelaClientePesquisaRapida = ( function(){
	function TabelaClientePesquisaRapida(modal) {
		this.cliente = $('.js-cliente-pesquisa-rapida');
		this.modalCliente = modal;
	}
	
	TabelaClientePesquisaRapida.prototype.iniciar = function(){
		this.cliente.on('click', onClienteSelecionado.bind(this));
	}
	
	function onClienteSelecionado(evento) {
		var clienteSelecionado = $(evento.currentTarget);
		console.log('Codigo', clienteSelecionado.data('codigo') );
		this.modalCliente.modal('hide');
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#codigoCliente').val(clienteSelecionado.data('codigo'));
	}
	
	return TabelaClientePesquisaRapida;
}());

$(function(){
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
});