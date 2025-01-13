package com.algaworks.brewer.service;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.VendaRepository;
import com.algaworks.brewer.service.event.venda.VendaEvent;

@Service
public class CadastroVendaService {
	
	@Autowired
	VendaRepository vendaRepository;
	
	@Autowired
	private ApplicationEventPublisher publisher;
	
	@Transactional
	public Venda salvar(Venda venda) {
		
		if(venda.isSalvarProibido()) {
			throw new RuntimeException("Usuário tentando salvar uma venda cancelada");
		}
		
		//Adicionando o else devido a edição de uma venda
		if(venda.isNova()) {
			venda.setDataCriacao(LocalDateTime.now());
		} else {
			Venda vendaExistente = vendaRepository.getOne(venda.getCodigo());
			venda.setDataCriacao(vendaExistente.getDataCriacao());
		}
		
		//Modificado para classe venda para o valortotal ser recuperado
//		BigDecimal valorTotalItens = venda.getItens().stream().map(ItemVenda::getValorTotal)
//				                                     .reduce(BigDecimal::add).get();
//		BigDecimal valorTotal = calcularValorTotal( valorTotalItens, venda.getValorFrete(), venda.getValorDesconto());
//		venda.setValorTotal(valorTotal);
		
		if(venda.getDataEntrega() != null) {
			venda.setDataHoraEntrega(LocalDateTime.of(venda.getDataEntrega(), venda.getHorarioEntrega() != null ? venda.getHorarioEntrega() : LocalTime.NOON));
		}
		
		//Mudança feita para retorna objeto Venda
		//vendaRepository.save(venda);
		return vendaRepository.saveAndFlush(venda);
	}
	
	@Transactional
	public void emitir(Venda venda) {
		venda.setStatus(StatusVenda.EMITIDA);
		salvar(venda);
		
		publisher.publishEvent(new VendaEvent(venda));
	}

//	private BigDecimal calcularValorTotal(BigDecimal valorTotalItens, BigDecimal valorFrete, BigDecimal valorDesconto) {
//		return  valorTotalItens.add(Optional.ofNullable(valorFrete).orElse(BigDecimal.ZERO))
//				                               .subtract(Optional.ofNullable(valorDesconto).orElse(BigDecimal.ZERO));
//	}
	
	@Transactional
	@PreAuthorize("#venda.usuario == principal.usuario or hasAuthority('CANCELAR_VENDA')")
	public void cancelar(Venda venda) {
		Venda vendaExistente = vendaRepository.getOne(venda.getCodigo());
		vendaExistente.setStatus(StatusVenda.CANCELADA);
		this.vendaRepository.save(vendaExistente);
	}
	

}
