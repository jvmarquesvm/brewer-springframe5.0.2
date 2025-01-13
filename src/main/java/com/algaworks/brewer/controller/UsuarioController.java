package com.algaworks.brewer.controller;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.StatusUsuario;
import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.GrupoRepository;
import com.algaworks.brewer.repository.UsuarioRepository;
import com.algaworks.brewer.repository.filter.UsuarioFilter;
import com.algaworks.brewer.service.CadastroUsuarioService;
import com.algaworks.brewer.service.exception.EmailUsuarioCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Controller
@RequestMapping(value = "/usuario")
public class UsuarioController {
	
	@Autowired
	private CadastroUsuarioService service;
	
	@Autowired
	private GrupoRepository grupoRepository;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@RequestMapping(value = "/novo", method = RequestMethod.GET)
	public ModelAndView novo(Usuario usuario) {
		ModelAndView mv = new ModelAndView("usuario/CadastroUsuario");
		mv.addObject("grupos", grupoRepository.findAll());
		return mv;
	}
	
	//url para adicionar um novo usuario ou editar um usuario
	@PostMapping(value = {"/novo" , "{\\d+}"})
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes atributos) {
		if(result.hasErrors()) {
			return novo(usuario);
		}
		
		try {
			service.salvar(usuario);
		} catch(EmailUsuarioCadastradoException e ) {
			result.rejectValue("email", e.getMessage(), e.getMessage());
			return novo(usuario);
		}  catch(SenhaObrigatoriaUsuarioException e ) {
			result.rejectValue("senha", e.getMessage(), e.getMessage());
			return novo(usuario);
		}
		
		atributos.addFlashAttribute("message", "Usuário Salvo com sucesso!!");
		return new ModelAndView("redirect:/usuario/novo");
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView pequisar(UsuarioFilter usuarioFilter,  BindingResult result, 
						            @PageableDefault(size = 1) Pageable pageable,
						                                 HttpServletRequest request ) {
		ModelAndView mv = new ModelAndView("usuario/PesquisaUsuario");
		mv.addObject("grupos", grupoRepository.findAll());

		//mv.addObject("usuarios", usuarioRepository.filtrar(usuarioFilter));
		PageWrapper<Usuario> paginaWrapper =  new PageWrapper<>( usuarioRepository.filtrar(usuarioFilter, pageable),
                                                                                    request );
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@PutMapping( value = "/status")
	@ResponseStatus( HttpStatus.OK )
	public void atualizarStatus (@RequestParam("codigos[]") Long[] codigos,
									@RequestParam("status") StatusUsuario status ) {
		Arrays.asList(codigos).forEach(System.out::println);
		System.out.println("Status: " + status);
		
		service.alterarStatus(codigos, status);
		
	}
	
	//Desta forma deu o erro de LazyInitializationException:
	// em um relacionamento manyTomany o default é não trazer a lista, 
	// mas como é preciso estar inicializada para ser utilizada na tela ela deve 
	// ser buscada - entidade que não foi inicializada
	//@GetMapping(value = "/{codigo}")
	//public ModelAndView editar(@PathVariable("codigo") Usuario usuario) {
	//	ModelAndView mv = novo(usuario);
	//	mv.addObject(usuario);
	//	return mv;
	//}
	
	//Nesse caso não utilizar o domain converter
	@GetMapping(value = "/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Usuario usuario = usuarioRepository.buscarComGrupos(codigo);
		ModelAndView mv = novo(usuario);
		mv.addObject(usuario);
		return mv;
	}
	
	@DeleteMapping("/{codigo}")
	public ResponseEntity<?> excluir(@PathVariable("codigo") Usuario usuario) {
		try {
			service.excluir(usuario);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

}
