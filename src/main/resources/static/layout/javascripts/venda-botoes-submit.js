Brewer = Brewer || {};

Brewer.BotaoSubmit = (function(){
	function BotaoSubmit() {
		this.submitBtn = $('.js-submit-btn');
		this.formulario = $('.js-formulario-principal');
	}
	
	BotaoSubmit.prototype.iniciar = function (){
		this.submitBtn.on('click', onSubmit.bind(this));
		
	}
	
	function onSubmit(evento) {
		//Acao padrao é submeter e carregar a página - isso é parado com o preventDefault()
		evento.preventDefault();
		
		var botaoClicado = $(evento.target);
		var acao = botaoClicado.data('acao');
		
		console.log(acao);
		
		var acaoInput = $('<input>');
		acaoInput.attr('name', acao);
		
		this.formulario.append(acaoInput);
		console.log(this.formulario);
		this.formulario.submit();
		
	}
	
	return BotaoSubmit;
}());

$( function(){
	var botaoSubmit = new Brewer.BotaoSubmit();
	botaoSubmit.iniciar();
});