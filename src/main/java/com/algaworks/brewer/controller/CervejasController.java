package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.CervejaEstilo;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.Origem;
import com.algaworks.brewer.model.Sabor;
import com.algaworks.brewer.model.Usuario;

import com.algaworks.brewer.repository.CervejasRepository;
import com.algaworks.brewer.repository.EstiloRepository;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.service.CervejaService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.CadastroEstiloService;

@Controller
@RequestMapping( value = "/cerveja")
public class CervejasController {
	private static final Logger logger = LoggerFactory.getLogger(CervejasController.class);
	@Autowired
	private CervejasRepository cervejasRepository;
	@Autowired
	private EstiloRepository estiloRepository;
	@Autowired
	private CervejaService cervejaService;
//	@Autowired
//	private CadastroEstiloService estiloService;
	
	@RequestMapping(value =  "/novo", method = RequestMethod.GET)
	//public String novo(Model model) {
	//public String novo(Cerveja cerveja) {
	public ModelAndView novo(Cerveja cerveja) {
		System.out.println("encaminhando(forward) para cerveja/CadastroCerveja");
		logger.error("log de error");
		logger.info("log de info");
		//model.addAttribute(new Cerveja());
		//cervejaRepository.findAll();
		
		ModelAndView mv = new ModelAndView("cerveja/CadastroCerveja");
		mv.addObject("sabores", Sabor.values() );
		mv.addObject("estilos", estiloRepository.findAll() );
		mv.addObject("origens", Origem.values() );
		return mv;
	}
	
//	@RequestMapping(value =  "/usuario/novo", method = RequestMethod.GET)
//	//public String novo(Model model) {
//	public String novo(Usuario usuario) {
//		System.out.println("encaminhando(forward) para usuario/CadastroUsuario");
//		//model.addAttribute(new Usuario());
//		return "usuario/CadastroUsuario";
//	}
	
//	@RequestMapping(value =  "/cidade/novo", method = RequestMethod.GET)
//	//public String novo(Model model) {
//	public String novo(Cidade cidade) {
//		System.out.println("encaminhando(forward) para cidade/CadastroCidade");
//		//model.addAttribute(new Cidade());
//		return "cidade/CadastroCidade";
//	}
	
//	@RequestMapping(value =  "/cerveja/estilo/novo", method = RequestMethod.GET)
//	//public String novo(Model model) {
//	public ModelAndView novoEstilo(Estilo estilo) {
//		System.out.println("encaminhando(forward) para cerveja/CadastroEstilo");
//		//model.addAttribute(new CervejaEstilo());
//		return new ModelAndView("cerveja/CadastroEstilo");
//	}
//	
//	@RequestMapping(value =  "/cerveja/estilo/novo", method = RequestMethod.POST)
//	//public String novo(Model model) {
//	public ModelAndView cadastro(@Valid Estilo estilo, BindingResult result, RedirectAttributes atrib) {
//		System.out.println("cadastrando estilo " + estilo.getNome());
//		//model.addAttribute(new CervejaEstilo());
//		if( result.hasErrors() ) {
//			System.out.println("erro bean validation");
//			//model.addAttribute(cerveja);
//			//model.addAttribute("message", "Erro no formulário");	
//			//return "cerveja/CadastroCerveja";
//			return novoEstilo(estilo);
//		}
//		estiloService.salvar(estilo);
//		atrib.addFlashAttribute("message", "Estilo salvo com sucesso!");
//		return new ModelAndView("redirect:/cerveja/estilo/novo");
//	}
	
	@RequestMapping( value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	//public String cadastro(@Valid Cerveja cerveja, BindingResult result, Model model, RedirectAttributes atrib ) {
	public ModelAndView cadastro(@Valid Cerveja cerveja, BindingResult result, Model model, RedirectAttributes atrib ) {
		
		/*
		 * System.out.println(">>> Cadastro cerveja: sku:> " + cerveja.getSku());
		 * System.out.println(">>> Cadastro cerveja: nome:> " + cerveja.getNome());
		 * System.out.println(">>> Cadastro cerveja: descricao:> " +
		 * cerveja.getDescricao()); System.out.println(">>> Cadastro cerveja: estilo:> "
		 * + cerveja.getEstilo());
		 * System.out.println(">>> Cadastro cerveja: estilo:codigo> " +
		 * cerveja.getEstilo().getCodigo());
		 */
		
		if( result.hasErrors() ) {
			System.out.println("erro bean validation");
			//model.addAttribute(cerveja);
			//model.addAttribute("message", "Erro no formulário");	
			//return "cerveja/CadastroCerveja";
			//throw new RuntimeException(); //para teste
			return novo(cerveja);
		}
		cervejaService.salvar(cerveja);
		atrib.addFlashAttribute("message", "Cerveja salva com sucesso!");
		return new ModelAndView( "redirect:/cerveja/novo" );
	}
	
	/**
	 * @GetMappin somente a partir do Spring 4.3
	 * @return
	 * 
	 *  para usar o pageable deve configar no webconfig - @EnableSpringDataWebSupport
	 *  pageSize default 20
	 */
	@GetMapping
	public ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult result, 
			                                    @PageableDefault(size = 2) Pageable pageable,
			                                       HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView("cerveja/PesquisaCerveja");
		System.out.println(pageable);
		
		mv.addObject("sabores", Sabor.values() );
		mv.addObject("estilos", estiloRepository.findAll() );
		mv.addObject("origens", Origem.values() );
		//mv.addObject("cervejas", cervejasRepository.filtrar(cervejaFilter, pageable));
		//Page<Cerveja> pagina =  cervejasRepository.filtrar(cervejaFilter, pageable);
		
		PageWrapper<Cerveja> paginaWrapper =  new PageWrapper<>( cervejasRepository.filtrar(cervejaFilter, pageable),
				                                                                                request );
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
//	@GetMapping("/filtro")
//	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome){
//		return cervejasRepository.porSkuOuNome(skuOuNome);
//	}
	
	//Necessário passar na chamada o contentType application/json
	@RequestMapping( consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<CervejaDTO> pesquisar(String skuOuNome){
		return cervejasRepository.porSkuOuNome(skuOuNome);
	}
	
	@DeleteMapping("/{codigo}")
	//public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Cerveja cerveja) {
		try {
			cervejaService.excluir(cerveja);
		} catch(ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cerveja cerveja) {
		ModelAndView mv = novo(cerveja);
		mv.addObject(cerveja);
		return mv;
	}
}
