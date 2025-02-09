package com.algaworks.brewer.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.algaworks.brewer.dto.PeriodoRelatorio;
import com.algaworks.brewer.service.RelatorioService;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {
	
	@Autowired
	RelatorioService relatorioService;

	@GetMapping( value = "/vendasEmitidas")
	public ModelAndView relatorioVendasEmitidas() {
		ModelAndView mv = new ModelAndView("relatorio/RelatorioVendasEmitidas");
		mv.addObject(new PeriodoRelatorio());
		return mv;
	}
	
	@PostMapping(value = "/vendasEmitidas")
	public ResponseEntity<byte[]> gerarRelatorioVendasEmitidas(PeriodoRelatorio periodo) throws Exception {
		byte[] relatorioVendasEmitidas = relatorioService.gerarRelatorioVendasEmitidas(periodo);
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE).body(relatorioVendasEmitidas);	
	}
}
