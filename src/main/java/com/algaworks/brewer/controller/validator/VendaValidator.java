package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Venda;

@Component
public class VendaValidator implements Validator {

	//É para validar qual classe?
	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	//método que vai fazer a validação
	@Override
	public void validate(Object target, Errors errors) {
		//1ª forma utilizando utilitários
		ValidationUtils.rejectIfEmpty(errors, "cliente.codigo", "", "Selecione um cliente na pesquisa rápida");
	
		Venda venda = (Venda) target;
		validarInformacaoApenasEntrega(errors, venda);
		validarInformacaoItens(errors, venda);
		validarValorTotalNegativo(errors, venda);
	}

	private void validarValorTotalNegativo(Errors errors, Venda venda) {
		if ( venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			errors.reject("", "Valor Total não pode ser negativo");
		}
	}

	private void validarInformacaoItens(Errors errors, Venda venda) {
		if (venda.getItens().isEmpty()) {
			errors.reject("", "Adicione pelo menos uma cerveja na venda");
		}
	}

	private void validarInformacaoApenasEntrega(Errors errors, Venda venda) {
		if( venda.getHorarioEntrega() != null  && venda.getDataEntrega() == null ) {
			errors.rejectValue("dataEntrega", "", "Informe uma data de entrega para um horário");
		}
	}

}
