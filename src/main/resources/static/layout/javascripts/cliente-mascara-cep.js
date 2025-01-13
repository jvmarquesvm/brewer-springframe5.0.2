var Brewer = Brewer || {};

Brewer.MascaraCep = ( function() {

	function MascaraCep() {
		this.inputCep = $('.js-cep');
	}
	
	MascaraCep.prototype.iniciar = function() {
		this.inputCep.mask('00000-000');
	}
	
	return MascaraCep;
}());

$( function() {
	var mascaraCep = new Brewer.MascaraCep();
	mascaraCep.iniciar();
});