Brewer.TabelaItens = ( function(){
	
	function TabelaItens(autocomplete) {
		this.autocomplete = autocomplete;
		this.tabelaCervejaContainer = $('.js-tabela-cerveja-container');
		this.uuid = $('#uuid').val(); //pegando o uuid para simular um escopo de view para tabela de venda
		//Criando emissor de evento quando alter/adicio
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	TabelaItens.prototype.iniciar = function(){
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		
		//Iniciando Quantidade e tabela de itens
		bindQuantidade.call(this);
		bindTabelaItem.call(this);
	}
	//Colocando valor total dos itens para recuperar externamente
	TabelaItens.prototype.valorTotal = function(){
		return this.tabelaCervejaContainer.data("valor");
	}
	
	function onItemSelecionado(evento, item){
		console.log('Item selecionado trigger', item);
		
		var resposta = $.ajax({
			url: 'item',
			method: 'post',
			data: {
				codigoCerveja: item.codigo,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAdicionadoNoServidor.bind(this));
	}
	
	function onItemAdicionadoNoServidor(dataHtml){
		console.log('retorno', dataHtml);
		this.tabelaCervejaContainer.html(dataHtml);
		
		//Removendo tratamento para um método separado e poder ser inicializado
		//Tratamento campo quantidade
		//var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		//quantidadeItemInput.on('change', onQtdItemAlterado.bind(this));
		//quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
		bindQuantidade.call(this);
		
		//Removendo tratamento para um método separado e poder ser inicializado
		//$('.js-tabela-item').on('dblclick', onDoubleClick);
		//$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		
		//Quando um item for atual/adicio emitir um evento para quem quiser ouvir
		//var tabelaItem = $('.js-tabela-item');
		var tabelaItem = bindTabelaItem.call(this);
		this.emitter.trigger('tabela-itens-atualizada', tabelaItem.data("valor-total"));
	}
	
	function onQtdItemAlterado(evento) {
		var input = $(evento.target);
		var quantidade = input.val();
		console.log('novaQuantidade', quantidade);
		var codigoCerveja = input.data('codigoCerveja');
		
		//Tratamento campo quantidade
		if (quantidade <= 0) {
			input.val(1);
			quantidade = 1;
		}
		
		var resposta = $.ajax({
			url: 'item/' + codigoCerveja,
			method: 'put',
			data : {
				quantidade: quantidade,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAdicionadoNoServidor.bind(this));
	}
	
	function onDoubleClick(evento) {
		var item = $(evento.currentTarget);
		//Função toggleClass do jQuery verifica se já tem a classe solicitando-exclusao
		//Se já tiver tira, se não tiver, põe
		item.toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItemClick(evento) {
		var codigoCerveja = $(evento.target).data('codigo-cerveja');
		var resposta = $.ajax({
			url: 'item/'+ this.uuid + '/' + codigoCerveja,
			method: 'delete'
		});
		
		resposta.done(onItemAdicionadoNoServidor.bind(this));
	}
	
	function bindQuantidade(){
		var quantidadeItemInput = $('.js-tabela-cerveja-quantidade-item');
		quantidadeItemInput.on('change', onQtdItemAlterado.bind(this));
		quantidadeItemInput.maskMoney({ precision: 0, thousands: '' });
	}
	
	function bindTabelaItem(){
		$('.js-tabela-item').on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		
		//Quando um item for atual/adicio emitir um evento para quem quiser ouvir
		var tabelaItem = $('.js-tabela-item');
		
		return tabelaItem;
	}
	
	return TabelaItens;
}());

//Inicialização Transferida para venda.js
//$( function() {
	//var autocomplete = new Brewer.Autocomplete();
	//autocomplete.iniciar();
	
	//var tabelaItens = new Brewer.TabelaItens(autocomplete);
	//tabelaItens.iniciar();
//});