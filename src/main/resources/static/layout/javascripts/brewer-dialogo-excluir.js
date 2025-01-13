Brewer = Brewer || {};

Brewer.DialogoExcluir = (function() {

	function DialogoExcluir() {
		this.exclusaoBtn = $(".js-exclusao-btn");
	}

	DialogoExcluir.prototype.iniciar = function(){
		this.exclusaoBtn.on("click", onExcluirClicado.bind(this));
		
		//Verificar se tem o parametro excluido na url
		if(window.location.search.indexOf('excluido') > -1){
			swal('Pronto', 'Excluído com sucesso', 'success');
		}
	}
	
	function onExcluirClicado(evento) {
		evento.preventDefault(); //nao fazer o comportamento padrao
		var botaoClicado = $(evento.currentTarget);
		var url = botaoClicado.data("url");
		var objeto = botaoClicado.data("objeto");
		
		swal({
			title: "Tem certeza?",
			text: "Excluir '" + objeto + "'? Você não poderá recuperar depois.",
			showCancelButton: true,
			confirmButtonColor: "#DD6B55",
			confirmButtonText: "Sim, Excluir agora!",
			closeOnConfirm: false
		}, onExcluirConfirmado.bind(this, url)); 
	}
	
	function onExcluirConfirmado(url){
		console.log("url", url);
		$.ajax({
			url: url,
			method: 'delete',
			success: onExcluidoSucesso.bind(this),
			error: onErroExcluir.bind(this)
		});
	}
	
	function onExcluidoSucesso() {
		var atualURL = window.location.href;
		var separador = atualURL.indexOf('?') > -1 ? '&' : '?';
		var novaURL = atualURL.indexOf('excluido') > -1 ? atualURL : atualURL + separador + 'excluido';
		window.location = novaURL;
	}
	
	function onErroExcluir(e){
		swal('Oops!', e.responseText, 'error');
	}
	
	return DialogoExcluir;
}());

$(function(){
	var dialogo = new Brewer.DialogoExcluir();
	dialogo.iniciar();
});