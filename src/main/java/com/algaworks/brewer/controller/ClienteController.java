package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.repository.ClienteRepository;
import com.algaworks.brewer.repository.EstadoRepository;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.service.CadastroClienteService;
import com.algaworks.brewer.service.exception.CpfCpnjClienteCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping(value = "/cliente")
public class ClienteController {
	
	@Autowired
	private EstadoRepository estadoRepository;
	@Autowired
	private CadastroClienteService clienteService;
	@Autowired
	private ClienteRepository clienteRepository;
	
	//Teve que colocar o retorno para json devido a lib jackson-databind
	@RequestMapping(value = "/novo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView novo(Cliente cliente) {
		ModelAndView mv = new ModelAndView("cliente/CadastroCliente");
		
		mv.addObject("estados", estadoRepository.findAll() );
		//mv.addObject("cidades", cidadeRepository.findAll() );
		mv.addObject("tiposPessoa", TipoPessoa.values());
		
		return mv;
	}
	
	//Teve que colocar o retorno para json devido a lib jackson-databind
	@RequestMapping(value = "/novo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView salvar( @Valid Cliente cliente, BindingResult result, RedirectAttributes atributos) {
		if(result.hasErrors()) {
			//System.out.println("Erro BeanValidation em Salvar Cliente Estado: " + cliente.getEndereco().getEstado().getNome());
			return novo(cliente);
		}
		
		try {
			clienteService.salvar(cliente);
		} catch(CpfCpnjClienteCadastradoException e ) {
			result.rejectValue("cpfCnpj", e.getMessage(), e.getMessage());
			return novo(cliente);
		}
		
		atributos.addFlashAttribute("message", "Cliente Salvo com sucesso!!");
		return new ModelAndView("redirect:/cliente/novo");
	}
	
	//@GetMapping
	//Teve que colocar o retorno para json devido a lib jackson-databind
	@RequestMapping( method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView pesquisar( ClienteFilter clienteFilter, BindingResult result, 
			                              @PageableDefault(size=2) Pageable pageable,
			                                HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView("cliente/PesquisaCliente");
		PageWrapper<Cliente> paginaWrapper = new PageWrapper<>( clienteRepository.filtrar(clienteFilter, pageable), request );
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
    @RequestMapping( consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = MediaType.APPLICATION_JSON_VALUE  )
	public @ResponseBody List<Cliente> pesquisar(String nome){
    	validarTamanhoNome(nome);
    	return clienteRepository.findByNomeStartingWithIgnoreCase(nome);
    }

	private void validarTamanhoNome(String nome) {
		if(StringUtils.isEmpty(nome) || nome.length() < 3 ) {
			throw new IllegalArgumentException();
		}
	}
	
	//Tratando o erro que o IllegalArgumentException deste controller lança
	//Se outro controller lançar, não será capturado
	//O oposto do ControlerAdvice
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException( IllegalArgumentException i){
		return ResponseEntity.badRequest().build();
	}
	
	//@GetMapping("/{codigo}")
	@RequestMapping( value = "/{codigo}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ModelAndView editar(@PathVariable("codigo") Cliente cliente) {
		System.out.println("Edição de Cliente!!");
		ModelAndView mv = this.novo(cliente);
		this.clienteService.comporDadosEndereco(cliente);
		mv.addObject(cliente);
		return mv;
	}
	
	//@DeleteMapping("/{codigo}")
	@RequestMapping( value = "/{codigo}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> excluir(@PathVariable("codigo") Cliente cliente) {
		try {
			this.clienteService.excluir(cliente);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
