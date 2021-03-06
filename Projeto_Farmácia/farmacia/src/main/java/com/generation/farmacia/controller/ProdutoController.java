package com.generation.farmacia.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.generation.farmacia.model.Produto;
import com.generation.farmacia.repository.CategoriaRepository;
import com.generation.farmacia.repository.ProdutoRepository;

@RestController
@RequestMapping("/produto")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {
	
	@Autowired
	private ProdutoRepository produtorepository;
	
	@Autowired
	private CategoriaRepository categoriarepository;
	
	//listar todos
	@GetMapping 
	public ResponseEntity <List <Produto>> getAll()
	{
		return ResponseEntity.ok(produtorepository.findAll());
	}
	
	//buscar por nome
	@GetMapping("/nome/{nome}")
	public ResponseEntity <List <Produto>> getByNome(@PathVariable String nome)
	{
		return ResponseEntity.ok(produtorepository.findAllByNomeContainingIgnoreCase(nome));
	}
	
	//cadastrar novo
	@PostMapping
	public ResponseEntity<Produto> postProduto(@Valid @RequestBody Produto produto)
	{

		if (categoriarepository.existsById(produto.getCategoria().getId()))
		{
		return ResponseEntity.status(HttpStatus.CREATED).
				body(produtorepository.save(produto));
		}
		else
		{
			return ResponseEntity.notFound().build();
		}
	}
	
	//atualizar
	@PutMapping
	public ResponseEntity<Produto> putProduto(@Valid @RequestBody Produto produto) {
					
		if (produtorepository.existsById(produto.getId())){

			return categoriarepository.findById(produto.getCategoria().getId())
					.map(resposta -> {
						return ResponseEntity.status(HttpStatus.OK)
						.body(produtorepository.save(produto));
					})
					.orElse(ResponseEntity.badRequest().build());
		}		
		
		return ResponseEntity.notFound().build();

	}
	
	//deletar
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePostagem(@PathVariable Long id)
	{
		return produtorepository.findById(id).map(resposta -> 
		{
			produtorepository.deleteById(id);
				return ResponseEntity.noContent().build();
		}).orElse(ResponseEntity.notFound().build());
	}
	
	// Consulta pelo pre??o maior do que o pre??o digitado emm ordem crescente
	@GetMapping("/preco_maior/{preco}")
	public ResponseEntity<List<Produto>> getPrecoMaiorQue(@PathVariable BigDecimal preco){ 
		return ResponseEntity.ok(produtorepository.findByPrecoGreaterThanOrderByPreco(preco));
	}
	
	// Consulta pelo pre??o menor do que o pre??o digitado em ordem decrescente
	@GetMapping("/preco_menor/{preco}")
	public ResponseEntity<List<Produto>> getPrecoMenorQue(@PathVariable BigDecimal preco){ 
		return ResponseEntity.ok(produtorepository.findByPrecoLessThanOrderByPrecoDesc(preco));
	}
	
	@GetMapping("/porpreco/{inicial}/{segundo}")
	public ResponseEntity<List<Produto>> findByPrecoBetween(@PathVariable BigDecimal inicial, @PathVariable BigDecimal segundo)
	{
		return ResponseEntity.ok(produtorepository.findByPrecoBetween(inicial, segundo));
	}
	
	//buscar por nome
	@GetMapping("/nome-laboratorio/{nome}/{laboratorio}")
	public ResponseEntity<List <Produto>> getByNomeAndLaboratorio(@PathVariable String nome, @PathVariable String laboratorio)
	{
		return ResponseEntity.ok(produtorepository.
				findByNomeContainingIgnoreCaseAndLaboratorioContainingIgnoreCase(nome, laboratorio));
	}

}
