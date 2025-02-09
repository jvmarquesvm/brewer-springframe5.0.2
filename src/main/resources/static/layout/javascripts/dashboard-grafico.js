var Brewer = Brewer || {};

Brewer.GraficoVendasPorMes = (function(){
	function GraficoVendasPorMes(){
		this.ctx = $('#graficoVendasPorMes');
	}
	
	GraficoVendasPorMes.prototype.iniciar = function(){
		$.ajax({
			url: 'venda/totalPorMes',
			method: 'get',
			success: onDadosRecebidos.bind(this)
		});
	}
	
	//unshift insere os dados no início
	function onDadosRecebidos(vendaMes){
		var meses = [];
		var valores = [];
		
		console.log("vendaMes: ", vendaMes);
		
		vendaMes.forEach(function(obj){
			meses.unshift(obj.mes);
			valores.unshift(obj.total);
		});
		  
		var graficoVendasPorMes = new Chart(this.ctx, {
		    type: 'line',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Vendas por mês',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                pointBorderColor: "rgba(26,179,148,1)",
	                pointBackgroundColor: "#fff",
	                data: valores
		    	}]
		    },
		});
	}
	return GraficoVendasPorMes;
}());

Brewer.GraficoVendaPorOrigem = (function() {
	
	function GraficoVendaPorOrigem() {
		this.ctx = $('#graficoVendasPorOrigem');
	}
	
	GraficoVendaPorOrigem.prototype.iniciar = function() {
		$.ajax({
			url: 'venda/porOrigem',
			method: 'get', 
			success: onDadosRecebidos.bind(this)
		});
	}
	
	function onDadosRecebidos(vendaOrigem) {
		var meses = [];
		var cervejasNacionais = [];
		var cervejasInternacionais = [];
		
		console.log("vendaOrigem", vendaOrigem);
		
		vendaOrigem.forEach(function(obj) {
			meses.unshift(obj.mes);
			cervejasNacionais.unshift(obj.totalNacional);
			cervejasInternacionais.unshift(obj.totalInternacional)
		});
		
		var graficoVendasPorOrigem = new Chart(this.ctx, {
		    type: 'bar',
		    data: {
		    	labels: meses,
		    	datasets: [{
		    		label: 'Nacional',
		    		backgroundColor: "rgba(220,220,220,0.5)",
	                data: cervejasNacionais
		    	},
		    	{
		    		label: 'Internacional',
		    		backgroundColor: "rgba(26,179,148,0.5)",
	                data: cervejasInternacionais
		    	}]
		    },
		});
	}
	return GraficoVendaPorOrigem;
}());

$(function(){
	var graficoVendasPorMes = new Brewer.GraficoVendasPorMes();
	graficoVendasPorMes.iniciar();
	
	var graficoVendaPorOrigem = new Brewer.GraficoVendaPorOrigem();
	graficoVendaPorOrigem.iniciar();
});
