var Brewer = Brewer || {};

Brewer.MascaraCpfCnpj = ( function() {

	function MascaraCpfCnpj() {
		this.radioTipoPessoa = $('.js-radio-tipo-pessoa');
		this.labelCpfCnpj = $("[for=cpfCnpj]");
		this.inputCpfCnpj = $("#cpfCnpj");
	}
	
	MascaraCpfCnpj.prototype.iniciar = function() {
		this.radioTipoPessoa.on('change', onTipoPessoaAlterado.bind(this) );
		var tipoPessoaSelecionada = this.radioTipoPessoa.filter(':checked')[0];
		
		if(tipoPessoaSelecionada){
			aplicarMascara.call(this, $(tipoPessoaSelecionada));
		}
	}
	
	function onTipoPessoaAlterado(evento){
		console.log("evento", evento );
		var tipoPessoaSelecionada = $(evento.currentTarget);
		console.log("documento", tipoPessoaSelecionada.data("documento"));
		aplicarMascara.call(this, tipoPessoaSelecionada);
		this.inputCpfCnpj.val("");
	}
	
	function aplicarMascara(tipoPessoaSelecionada){
		this.labelCpfCnpj.text(tipoPessoaSelecionada.data("documento"));
		this.inputCpfCnpj.mask(tipoPessoaSelecionada.data("mascara"));
		this.inputCpfCnpj.removeAttr("disabled");
	}
	
	return MascaraCpfCnpj;
}());

$( function() {
	var mascaraCpfCnpj = new Brewer.MascaraCpfCnpj();
	mascaraCpfCnpj.iniciar();
});