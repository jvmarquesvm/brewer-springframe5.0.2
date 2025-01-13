package com.algaworks.brewer.session;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.session.TabelaItensVenda;

public class TabelaItensVendaTest {
	
	private TabelaItensVenda tabelaItensVenda;
	
	@Before
	public void setup() {
		this.tabelaItensVenda = new TabelaItensVenda("1");
	}
	
	@Test
	public void deveCalcularValorTotalSemIntes() throws Exception {
		assertEquals(BigDecimal.ZERO, tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveCalcularValorTotalcomUmItem() throws Exception {
		Cerveja cerveja = new Cerveja();
		cerveja.setValor(new BigDecimal("8.90"));
		
		this.tabelaItensVenda.adicionarItem(cerveja, 1);
		assertEquals(cerveja.getValor(), this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveCalcularValorTotalcomVariosItens() throws Exception {
		Cerveja cerveja1 = new Cerveja();
		cerveja1.setCodigo(1L);
		cerveja1.setValor(new BigDecimal("8.90"));
		
		Cerveja cerveja2 = new Cerveja();
		cerveja2.setCodigo(2L);
		cerveja2.setValor(new BigDecimal("4.50"));
		
		this.tabelaItensVenda.adicionarItem(cerveja1, 4);
		this.tabelaItensVenda.adicionarItem(cerveja2, 4);
		assertEquals(new BigDecimal("53.60"), this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveManterTamanhoListaMesmasCervejvas()  throws Exception {
		Cerveja cerveja1 = new Cerveja();
		cerveja1.setCodigo(1L);
		cerveja1.setValor(new BigDecimal("4.50"));
		
		this.tabelaItensVenda.adicionarItem(cerveja1, 1);
		this.tabelaItensVenda.adicionarItem(cerveja1, 1);
		
		assertEquals(1, this.tabelaItensVenda.total());
	}
	
	@Test
	public void deveAlterarQuantidadeItem()  throws Exception {
		Cerveja cerveja1 = new Cerveja();
		cerveja1.setCodigo(1L);
		cerveja1.setValor(new BigDecimal("4.50"));
		
		this.tabelaItensVenda.adicionarItem(cerveja1, 1);
		this.tabelaItensVenda.alterarQuantidadeItens(cerveja1, 3);
		
		assertEquals(new BigDecimal("13.50"), this.tabelaItensVenda.getValorTotal());
	}
	
	@Test
	public void deveExcluiritem() throws Exception {
		Cerveja cerveja1 = new Cerveja();
		cerveja1.setCodigo(1L);
		cerveja1.setValor(new BigDecimal("8.90"));
		
		Cerveja cerveja2 = new Cerveja();
		cerveja2.setCodigo(2L);
		cerveja2.setValor(new BigDecimal("4.50"));
		
		Cerveja cerveja3 = new Cerveja();
		cerveja3.setCodigo(3L);
		cerveja3.setValor(new BigDecimal("4.50"));
		
		this.tabelaItensVenda.adicionarItem(cerveja1, 1);
		this.tabelaItensVenda.adicionarItem(cerveja2, 2);
		this.tabelaItensVenda.adicionarItem(cerveja3, 1);
		
		this.tabelaItensVenda.excluirItem(cerveja2);
		
		assertEquals(new BigDecimal("13.40"), this.tabelaItensVenda.getValorTotal());
		assertEquals(2, tabelaItensVenda.total());
	}

}
