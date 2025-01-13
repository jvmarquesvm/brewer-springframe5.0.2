Brewer.Venda = ( function(){

	function Venda(tabelaItens) {
		this.tabelaItens = tabelaItens;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		
		//Tem que iniciar esses valores vindo do servidor
		this.valorTotalItens = this.tabelaItens.valorTotal();
		this.valorFrete = numeral(this.valorFreteInput.data("valor")).value();
		this.valorDesconto = numeral(this.valorDescontoInput.data("valor")).value();
		this.valorTotalBoxContainer = $('.js-valor-total-box-container');
	}
	
	Venda.prototype.iniciar = function() {
		this.tabelaItens.on('tabela-itens-atualizada', onTabelaItensAtualizada.bind(this));
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));
		
		this.tabelaItens.on('tabela-itens-atualizada', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}
	
	function onTabelaItensAtualizada(evento, valorTotalItens) {
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens * 1;
		console.log("Valor Total Itens:", valorTotalItens);
		console.log("Valor Total Itens:", typeof valorTotalItens);
		console.log("Valor Total Itens da classe:", typeof this.valorTotalItens);
		//this.valorTotalBox.html( Brewer.formatarMoeda(valorTotalItens) );
	}
	
	function onValorFreteAlterado(evento) {
		console.log("Valor do Frete", $(evento.target).val() ); 
		this.valorFrete = Brewer.recuperarValor( $(evento.target).val() );
		console.log("Valor do Frete valor", this.valorFrete ); 
	}
	
	function onValorDescontoAlterado(evento) {
		console.log("Valor do Desconto", $(evento.target).val() ); 
		this.valorDesconto = Brewer.recuperarValor( $(evento.target).val()  );
		console.log("Valor do Desconto valor", this.valorDesconto ); 
	}
	
	function onValoresAlterados() {
		console.log("Valor Total Itens ValorFrete ValorDesconto", this.valorTotalItens, this.valorFrete, this.valorDesconto);
		console.log("Valor Total Itens ValorFrete ValorDesconto", typeof this.valorTotalItens, typeof this.valorFrete, typeof  this.valorDesconto); 
		
		//Valores as vezes vem como string e pode dar erro
		var valorTotal = this.valorTotalItens + this.valorFrete - this.valorDesconto;
		//Não funcionou
		//var valorTotal = numeral(this.valorTotalItens) + numeral(this.valorFrete) - numeral(this.valorDesconto);
		
		console.log("Valor Total:", valorTotal);
		this.valorTotalBoxContainer.toggleClass("negativo", valorTotal < 0);
		valorTotal = this.valorTotalBox.html( Brewer.formatarMoeda(valorTotal));
	}
	
	return Venda;
}());


//Venda.js vai fazer a parte de inicialização
//Venda.js depende da venda-tabela-itens.js
//Pois vai ouvir eventos da tabela de itens quando adicionar/alterar alguma cerveja
$( function() {
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var tabelaItens = new Brewer.TabelaItens(autocomplete);
	tabelaItens.iniciar();
	
	var venda = new Brewer.Venda(tabelaItens);
	venda.iniciar();
});