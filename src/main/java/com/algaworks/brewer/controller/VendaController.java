package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.CervejasRepository;
import com.algaworks.brewer.repository.VendaRepository;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.CadastroVendaService;
import com.algaworks.brewer.service.CervejaService;
import com.algaworks.brewer.session.TabelasItensSession;

@Controller
@RequestMapping("/venda")
public class VendaController {
	
	@Autowired
	private CervejasRepository cervejasRepository;
	
	//Mudando  de tabelaItensVenda para tabela de sessão - uuid - 
	//Simular um escopo de view
	@Autowired
	private TabelasItensSession tabelasItensSession;
	
	@Autowired
	private CadastroVendaService cadastroVendaService;
	
	@Autowired
	private VendaRepository vendaRepository;
	
	@Autowired
	private Mailer mailer;
	
	/**
	 * 
	 * Substituido para utilizar o uuid
	 * @return
	 */
//	@GetMapping("/novo")
//	public String nova() {
//		return "venda/CadastroVenda";
//	}
	/**
	 * metodo feito antes de mapear a tela com o objeto
	 * @return
	 */
//	@GetMapping("/novo")
//	public ModelAndView nova() {
//		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
//		mv.addObject("uuid", UUID.randomUUID().toString());
//		return mv;
//	}
	
	@GetMapping("/novo")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/CadastroVenda");
		//mv.addObject("uuid", UUID.randomUUID().toString());
		//venda.setUuid( UUID.randomUUID().toString());
		
		//Fazendo validação pois está sendo chamado após o método salvar
		//Refatorando
		//if(StringUtils.isEmpty(venda.getUuid())) {
		//	venda.setUuid( UUID.randomUUID().toString());
		//}
		setUuid(venda);
		
		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", this.tabelasItensSession.getValorTotal(venda.getUuid()));
		return mv;
	}
	
	/*@PostMapping("/item")
	public @ResponseBody String adicionarItem(Long codigoCerveja) {
		Cerveja cerveja =  cervejasRepository.findOne(codigoCerveja);
		tabelaItensVenda.adicionarItem(cerveja, 1);
		System.out.println("Total de Itens " + tabelaItensVenda.total());
		return "Item Adicionado";
	}*/
	
	//Retornando um html renderizado em Ajax
	@PostMapping("/item")
	public ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja =  cervejasRepository.findOne(codigoCerveja);
		this.tabelasItensSession.adicionarItem(uuid, cerveja, 1);
		System.out.println("Total de Itens " + this.tabelasItensSession.getItens(uuid).size());
		
		return mvTabelaItensVenda(uuid);
	}
	
	@PutMapping("/item/{codigoCerveja}")
	public ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, 
			                               Integer quantidade, String uuid) {
		Cerveja cerveja =  cervejasRepository.findOne(codigoCerveja);
		this.tabelasItensSession.alterarQuantidadeItens(uuid, cerveja, quantidade);
		
		System.out.println("Itens " + this.tabelasItensSession.getItens(uuid).get(0).toString());
		
		return mvTabelaItensVenda(uuid);
	}
	
//	@DeleteMapping("/item/{codigoCerveja}")
//	public ModelAndView excluirItem(@PathVariable Long codigoCerveja) {
//		Cerveja cerveja = cervejasRepository.findOne(codigoCerveja);
//		tabelaItensVenda.excluirItem(cerveja);
//		
//		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
//		mv.addObject("itens", tabelaItensVenda.getItens());
//		return mv;
//	}
//	
	/**
	 * 
	 * @param cerveja
	 * Integração direta MVC e SpringDataJPA
	 * para isso tem que ir no WebConfig e configurar o Bean
	 * DomainClassConverter<>
	 * com isso já retira o findOne
	 * 
	 */
	//@DeleteMapping("/item/{uudi}/{codigoCerveja}")
	@RequestMapping(value="/item/{uudi}/{codigoCerveja}", method={RequestMethod.DELETE})
	public ModelAndView excluirItem( @PathVariable("uudi") String uuid, 
			                            @PathVariable("codigoCerveja") Cerveja cerveja ) {
		this.tabelasItensSession.excluirItem(uuid, cerveja);
		return mvTabelaItensVenda(uuid);
	}

	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/TabelaItensVenda");
		mv.addObject("itens", this.tabelasItensSession.getItens(uuid));
		mv.addObject("valorTotal", tabelasItensSession.getValorTotal(uuid));
		System.out.println("valorTotal: " + tabelasItensSession.getValorTotal(uuid));
		return mv;
	}
	
	//Mesmo mapeamento com parametros diferentes
	@PostMapping( value="/novo", params = "salvar")      //Validando com o Validator .... se colocar o bindingresult com ultimo parâmetro dá erro 400 na chamada
	public ModelAndView salvar(/*@Valid*/ Venda venda,  BindingResult result, RedirectAttributes attributes, 
			            @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		//Refatorando
		validarVenda(venda, result);
		
		if ( result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		//venda.setItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		this.cadastroVendaService.salvar(venda);
		attributes.addFlashAttribute("message", "Venda salva com sucesso!!");
		return new ModelAndView("redirect:/venda/novo");
	}
	
	@Autowired
	private VendaValidator vendaValidator;
	
	//Vai ser chamado na hora de criar o controlador e quando encontrar alguem com @Valid, será executado
	@InitBinder("venda")
	public void inicializarValidator(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
	}
	
	@PostMapping( value="/novo", params = "emitir")      //Validando com o Validator .... se colocar o bindingresult com ultimo parâmetro dá erro 400 na chamada
	public ModelAndView emitir(/*@Valid*/ Venda venda,  BindingResult result, RedirectAttributes attributes, 
			            @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		//Inserindo os itens de venda em venda antes da validação
		venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//Adicioando o calculo do valor total que está na classe venda
		venda.calcularValorTotal();
		vendaValidator.validate(venda, result);
		
		if ( result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		//venda.setItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		this.cadastroVendaService.emitir(venda);
		attributes.addFlashAttribute("message", "Venda emitida com sucesso!!");
		return new ModelAndView("redirect:/venda/novo");
	}
	
	@PostMapping( value="/novo", params="enviarEmail")      //Validando com o Validator .... se colocar o bindingresult com ultimo parâmetro dá erro 400 na chamada
	public ModelAndView enviarEmail(/*@Valid*/ Venda venda,  BindingResult result, RedirectAttributes attributes, 
			            @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		
		//Inserindo os itens de venda em venda antes da validação
		venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//Adicioando o calculo do valor total que está na classe venda
		venda.calcularValorTotal();
		vendaValidator.validate(venda, result);
		
		if ( result.hasErrors()) {
			return nova(venda);
		}
		
		venda.setUsuario(usuarioSistema.getUsuario());
		//venda.setItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		venda = this.cadastroVendaService.salvar(venda);
		this.mailer.enviar(venda);
		System.out.println(">> logo após a chamada do metodo enviar email");
		attributes.addFlashAttribute("message", String.format("Venda nº %d salva com sucesso e e-mail enviado!!", venda.getCodigo()) );
		return new ModelAndView("redirect:/venda/novo");
	}
	
	private void validarVenda(Venda venda, BindingResult result) {
		//Inserindo os itens de venda em venda antes da validação
		venda.adicionarItens(this.tabelasItensSession.getItens(venda.getUuid()));
		//Adicionando o calculo do valor total que está na classe venda
		venda.calcularValorTotal();
		vendaValidator.validate(venda, result);
	}
	
	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, 
			@PageableDefault(size = 15) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/PesquisaVenda");
		mv.addObject("todosStatus", StatusVenda.values());
		System.out.println("passou no pesquisar venda");
		
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendaRepository.filtrar(vendaFilter, pageable)
				, httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}
	
	@GetMapping(value="/{codigo}")
	public ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Venda venda = vendaRepository.buscarComItens(codigo);
		
		setUuid(venda);
		for(ItemVenda item : venda.getItens()) {
			tabelasItensSession.adicionarItem(venda.getUuid(),item.getCerveja(), item.getQuantidade());
		}
		
		ModelAndView mv = nova(venda);
		mv.addObject(venda);
		return mv;
	}

	private void setUuid(Venda venda) {
		if(StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid( UUID.randomUUID().toString());
		}
	}
	
	@PostMapping(value="/novo", params="cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result,
			                      RedirectAttributes attributes,
			                        @AuthenticationPrincipal UsuarioSistema usuarioSistema ) {
		try {
			this.cadastroVendaService.cancelar(venda);
		} catch(AccessDeniedException e) {
			return new ModelAndView("/403");
		}
		
		attributes.addFlashAttribute("message", "Venda cancelada com sucesso");
		return new ModelAndView("redirect:/venda/" + venda.getCodigo());
	}
	
	@GetMapping(value = "/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes(){
		return vendaRepository.totalPorMes();
	}
	
	@GetMapping(value = "/porOrigem")
	public @ResponseBody List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendaRepository.totalPorOrigem();
	}
}
