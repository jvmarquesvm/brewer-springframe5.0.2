Brewer = Brewer || {};

Brewer.Autocomplete = ( function(){
	function Autocomplete() {
		this.skuOuNomeInput = $('.js-sku-nome-cerveja-input');
		var htmlTemplateAutocomplete = $("#template-autocomplete-cerveja").html();
		this.template = Handlebars.compile(htmlTemplateAutocomplete);
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	//Autocomplete.prototype.iniciar = function() {
	//	var options = {
		//	url: "/brewer/cerveja/filtro?skuOuNome=",
		//	getValue: 'nome'			
	//};

	Autocomplete.prototype.iniciar = function() {
		var options = {
			url: function (skuOuNomeInput) { 
			             //return "/brewer/cerveja?skuOuNome=" + skuOuNomeInput
			             return this.skuOuNomeInput.data("url") + "?skuOuNome=" + skuOuNomeInput;
			}.bind(this),
			getValue: 'nome',
			minCharNumber: 3,
			ajaxSettings: {
				contentType: "application/json"
			},
			requestDelay: 300,
			template: {
				type: "custom",
				method: template.bind(this)
			},
			list: {
				onChooseEvent: onItemSelecionado.bind(this)
			}
		};
		
		this.skuOuNomeInput.easyAutocomplete(options);
	}
	
	function template(nome, cerveja){
		cerveja.valorFormatado = Brewer.formatarMoeda(cerveja.valor);
		//console.log(arguments); // tem que ser arguments
		return this.template(cerveja);
	}
	
	function onItemSelecionado() {
		console.log('Selecionou', this.skuOuNomeInput.getSelectedItemData());
		this.emitter.trigger('item-selecionado', this.skuOuNomeInput.getSelectedItemData());
		this.skuOuNomeInput.val('');
		this.skuOuNomeInput.focus();
	}
	
	return Autocomplete;
}());

//$( function() {
	//var autocomplete = new Brewer.Autocomplete();
	//autocomplete.iniciar();
//})