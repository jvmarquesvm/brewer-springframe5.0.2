Brewer = Brewer || {};

Brewer.MultiSelecao = ( function(){
	
	function MultiSelecao() {
		this.statusBtn = $('.js-status-btn');
		this.selecaoChk = $('.js-selecao');
		this.selecaoTodosChk = $('.js-selecao-todos');
	}

	MultiSelecao.prototype.iniciar = function() {
		this.statusBtn.on('click', onStatusBtnClicado.bind(this));
		this.selecaoTodosChk.on('click', onSelecaoTodosClicado.bind(this));
		this.selecaoChk.on('click', onSelecaoClicado.bind(this));
	}

	function onStatusBtnClicado(event){
		var btnClicado = $(event.currentTarget);
		console.log(btnClicado.data('status'));
		var url = btnClicado.data('url');
		
		var selecionadosChk = this.selecaoChk.filter(':checked');
		var codigos = $.map( selecionadosChk, function(c){
			return $(c).data('codigo');
		});
		
		var status = btnClicado.data('status');
		
		if (codigos.length > 0 ) {
			$.ajax({
				url: url,
				method: 'put',
				data: { 
					codigos: codigos,
					status: status
				},
				success: function() {
					window.location.reload();
				}
			});
		}
	}
	
	function onSelecaoTodosClicado() {
		var status = this.selecaoTodosChk.prop('checked');
		console.log('status', status);
		this.selecaoChk.prop('checked', status);
		statusBotaoAcao.call(this, status );
	}
	
	function onSelecaoClicado() {
		var selecaoChecadosChk = this.selecaoChk.filter(':checked');
		console.log('Checados', selecaoChecadosChk.length);
		this.selecaoTodosChk.prop('checked', selecaoChecadosChk.length >= this.selecaoChk.length );
		statusBotaoAcao.call(this, selecaoChecadosChk.length  );
	}
	
	function statusBotaoAcao(ativar) {
		ativar ? this.statusBtn.removeClass('disabled')  :  this.statusBtn.addClass('disabled');  
	}

	return MultiSelecao;
}());

$(function() {
	var multiSelecao = new Brewer.MultiSelecao();
	multiSelecao.iniciar();
});