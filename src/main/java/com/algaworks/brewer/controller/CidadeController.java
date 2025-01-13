package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.CidadeRepository;
import com.algaworks.brewer.repository.EstadoRepository;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CidadeService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeCidadeException;

@Controller
@RequestMapping(value = "/cidade")
public class CidadeController {
	
	@Autowired
	private CidadeRepository cidadeRepository;
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private CidadeService cidadeService;
	
	@RequestMapping(value =  "/novo", method = RequestMethod.GET)
	public ModelAndView novaCidade(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/CadastroCidade");
		mv.addObject("estados", estadoRepository.findAll());
		return mv;
	}
	
	@RequestMapping(value =  "/novo", method = RequestMethod.POST)
	/*Invalida todas as entradas*/
	//@CacheEvict( value = "cidades", allEntries = true)
	/*Invalidando somente pela chave
	 * Condição é necessário pois quando vai salvar ele estava validando o codigo do estado*/
	@CacheEvict( value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, RedirectAttributes atributos) {
		if(result.hasErrors()) {
			//System.out.println("Erro BeanValidation em Salvar Cliente Estado: " + cliente.getEndereco().getEstado().getNome());
			return novaCidade(cidade);
		}
		
		try {
			cidadeService.salvar(cidade);
		} catch (NomeCidadeException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novaCidade(cidade);
		}
		
		atributos.addFlashAttribute("message", "Cidade salva com sucesso!!");
		return new ModelAndView("redirect:/cidade/novo");
	}
	
	@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	/*Somente dando nome ao cache*/
	//@Cacheable("cidades")
	/*Configurando o cache com nome e chave*/
	@Cacheable( value = "cidades", key = "#codigoEstado")
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado ( 
			         @RequestParam( name = "estado", defaultValue = "-1") Long codigoEstado ){
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cidadeRepository.findByEstadoCodigo(codigoEstado);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,  
			                         @PageableDefault( size = 2 )  Pageable pageable, 
			                                             HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView("cidade/PesquisaCidade");
		mv.addObject("estados", estadoRepository.findAll());
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>( cidadeRepository.filtrar(cidadeFilter, pageable), request );
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public ResponseEntity<?> excluir(@PathVariable("codigo") Cidade cidade) {
		try {
			this.cidadeService.excluir(cidade);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Cidade cidade) {
		ModelAndView mv = this.novaCidade(cidade);
		mv.addObject(this.cidadeRepository.findByCodigoFetchingEstado(cidade.getCodigo()));
		return mv;
	}

}
