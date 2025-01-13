package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
//Versao 4.2 pra baxio do Spring
//@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
//Versao 4.3 pra cima do Spring

//Retirado para a classe TabelasItensSesssion devido a sessão compartilhada entre duas telas do browser
//@SessionScope
//@Component
class TabelaItensVenda {
	
	private List<ItemVenda> itens = new ArrayList<>();
	//Adicionando atributo uuid para simular escpo de view
	private String uuid;
	
	public String getUuid() {
		return uuid;
	}

	public TabelaItensVenda(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * java* - stream - iterador java 8
	 * Map mapear ( pegar dentro do objeto, qual atributo a ser verificado )
	 * reduce reduzir ( soma-los reduzindo ) - retorna um optional de bigdecimal
	 * orElse - se não retorne 0
	 * @return
	 */
	
	public BigDecimal getValorTotal() {
		return this.itens.stream().map(ItemVenda::getValorTotal)
				              .reduce(BigDecimal::add)
				              .orElse(BigDecimal.ZERO);
	}
	
	public void adicionarItem(Cerveja cerveja, Integer quantidade) {

		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda itemVenda = null;
		if(itemVendaOptional.isPresent()) {
			itemVenda = itemVendaOptional.get();
			itemVenda.setQuantidade(itemVenda.getQuantidade() + quantidade);
		} else {
			itemVenda = new ItemVenda();
			itemVenda.setCerveja(cerveja);
			itemVenda.setQuantidade(quantidade);
			itemVenda.setValorUnitario(cerveja.getValor());
			this.itens.add(itemVenda);
		}
	}
	
	/**
	 * Utilizar o stream para fazer o for
	 * stream dos itens quero um filtro que para cada item dentro
	 * cerveja na lista for igual a cerveja que esta sendo adicionada
	 * e a primeira que encontra 
	 * e retorna um optional
	 */
	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		return itens.stream().filter(i -> i.getCerveja().equals(cerveja)).findAny();
	}
	
	public int total() {
		return this.itens.size();
	}
	
	public List<ItemVenda> getItens() {
		return this.itens;
	}
	
	public void alterarQuantidadeItens(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}
	
	public void excluirItem(Cerveja cerveja) {
		int indice = IntStream.range(0, this.itens.size())
				        .filter(i -> itens.get(i).getCerveja().equals(cerveja))
				        .findAny().getAsInt();
		itens.remove(indice);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TabelaItensVenda other = (TabelaItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
	
	

}
