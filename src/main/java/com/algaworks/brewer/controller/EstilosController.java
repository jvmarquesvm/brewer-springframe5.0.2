package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.EstiloRepository;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.service.CadastroEstiloService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeEstiloCadastroException;

@Controller
@RequestMapping(value = "/estilo")
public class EstilosController {
	
	@Autowired
	private CadastroEstiloService cadastroEstiloService;
	
	@Autowired
	private EstiloRepository estiloRepository;
	
	@RequestMapping(value =  "/novo", method = RequestMethod.GET)
	public ModelAndView novoEstilo(Estilo estilo) {
		return new ModelAndView("cerveja/CadastroEstilo");
	}
	
	//@RequestMapping(value =  "/novo", method = RequestMethod.POST,  consumes = { MediaType.APPLICATION_JSON_VALUE })
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	public ModelAndView cadastro(@Valid Estilo estilo, BindingResult result, RedirectAttributes atrib) {
		
		System.out.println("cadastrando estilo " + estilo.getNome());
		
		if( result.hasErrors() ) {
			System.out.println("erro bean validation");
			return novoEstilo(estilo);
		}
		
		try {
			cadastroEstiloService.salvar(estilo);
		} catch( NomeEstiloCadastroException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novoEstilo(estilo);
		}
		
		atrib.addFlashAttribute("message", "Estilo salvo com sucesso!");
		return new ModelAndView("redirect:/estilo/novo");
	}
	
	//@RequestMapping( method = RequestMethod.POST, consumes = { MediaType.APPLICATION_JSON_VALUE })
	@PostMapping
	public @ResponseBody ResponseEntity<?> salvar( @RequestBody @Valid Estilo estilo, 
			                               BindingResult result, RedirectAttributes atributos) {
		System.out.println(estilo.getNome());
		
		if (result.hasErrors()) {
			//return ResponseEntity.badRequest().body(result.getFieldErrors());
			return ResponseEntity.badRequest().body(result.getFieldError("nome").getDefaultMessage());
		}
		
//		try {
			estilo = cadastroEstiloService.salvar(estilo);
//		} catch(NomeEstiloCadastroException e ) {
//			return ResponseEntity.badRequest().body(e.getMessage());
//		}
		return ResponseEntity.ok(estilo);
	}
	
	@GetMapping
	public ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult result, 
			                    @PageableDefault( size = 2 )  Pageable pageable, 
			                                           HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView("cerveja/PesquisaEstilo");
		System.out.println("Nome Estilo: " + estiloFilter.getNome());
		System.out.println(pageable);
		
		PageWrapper<Estilo> paginaWrapper = new PageWrapper<>( estiloRepository.filtrar(estiloFilter, pageable), request );
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Estilo estilo) {
		ModelAndView mv = this.novoEstilo(estilo);
		Estilo estiloRecuperado = estiloRepository.findByCodigo(estilo.getCodigo());
		mv.addObject(estiloRecuperado);
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public ResponseEntity<?> excluir(@PathVariable("codigo") Estilo estilo) {
		try {
			this.cadastroEstiloService.excluir(estilo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
