package com.example.apiestoque.controllers;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.services.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.FieldError;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final Validator validator;

    @Autowired
    public ProdutoController(ProdutoService produtoService, Validator validator) {
        this.produtoService = produtoService;
        this.validator = validator;
    }

    @GetMapping("/selecionar")
    public List<Produto> listarProdutos() {
        return produtoService.buscarTodosProdutos();
    }

    @PostMapping("/inserir")
    public ResponseEntity<String> inserirProduto(@Valid @RequestBody Produto produto) {
        produtoService.salvarProduto(produto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Produto inserido com sucesso");
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<String> excluirProduto(@PathVariable Long id) {
        boolean excluido = produtoService.excluirProduto(id);
        if (excluido) {
            return ResponseEntity.ok("Produto excluído com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<String> atualizarProduto(@Valid @RequestBody Produto produto) {
        boolean atualizado = produtoService.atualizarProduto(produto.getId(), produto);
        if (atualizado) {
            return ResponseEntity.ok("Produto atualizado com sucesso");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }

    @PatchMapping("/atualizarParcial/{id}")
    public ResponseEntity<?> atualizarParcialProduto(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Optional<Produto> produtoExistenteOptional = produtoService.buscarProdutoPorId(id);

        if (produtoExistenteOptional.isPresent()) {
            Produto produtoExistente = produtoExistenteOptional.get();

            for (Map.Entry<String, Object> entry : updates.entrySet()) {
                String campo = entry.getKey();
                Object valor = entry.getValue();

                if ("nome".equals(campo) && valor instanceof String) {
                    produtoExistente.setNome((String) valor);
                } else if ("descricao".equals(campo) && valor instanceof String) {
                    produtoExistente.setDescricao((String) valor);
                } else if ("preco".equals(campo) && valor instanceof Double) {
                    produtoExistente.setPreco((Double) valor);
                } else if ("quantidadeEstoque".equals(campo) && valor instanceof Integer) {
                    produtoExistente.setQuantidadeEstoque((Integer) valor);
                }
            }

            DataBinder binder = new DataBinder(produtoExistente);
            binder.setValidator(validator);
            binder.validate();
            BindingResult result = binder.getBindingResult();
            if (result.hasErrors()) {
                return ResponseEntity.badRequest().body(validarProduto(result));
            } else {
                produtoService.salvarProduto(produtoExistente);
                return ResponseEntity.ok("Produto atualizado parcialmente com sucesso");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto não encontrado");
        }
    }

    // chamando o metodo buscarPorNome para buscar o nome
    @GetMapping("/buscarPorNome")
    public List<Produto> buscarPorNome(@Valid @RequestParam String nome) {
        return produtoService.buscarPorNome(nome);
    }

    // chamando o metodo findByNomeIgnoreCaseAndPrecoLessThan para buscar o nome e o preco
    @GetMapping("/buscarPorNomeEPrecoMenorQue/{nome}/{preco}")
    public List<Produto> buscarPorNomeEPrecoMenorQue(@Valid @RequestParam String nome, @RequestParam BigDecimal preco) {
        return produtoService.buscarPorNomeEPrecoMenorQue(nome, preco);
    }

    // metodo validar produto para validar o produto
    private Map<String, String> validarProduto(BindingResult result) {
        Map<String, String> erros = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            erros.put(error.getField(), error.getDefaultMessage());
        }
        return erros;
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException manve) {
//        Map<String, String> errors = new HashMap<>();
//        for (FieldError error : manve.getBindingResult().getFieldErrors()) {
//            errors.put(error.getField(), error.getDefaultMessage());
//        }
//        return errors;
//    }
}

// Sem o Service

//@RestController
//@RequestMapping("/api/produtos")
//public class ProdutoController {
//    private final ProdutoRepository produtoRepository;
//
//    public ProdutoController(ProdutoRepository produtoRepository){
//        this.produtoRepository = produtoRepository;
//    }
//
//    @GetMapping("/selecionar")
//    public List<Produto> listarProduto(){
//        return produtoRepository.findAll();
//    }
//
//    @PostMapping("/inserir")
//    public ResponseEntity<String> inserirProduto(@RequestBody Produto produto) {
//        produtoRepository.save(produto);
//        if (produtoRepository.existsById(produto.getId())) {
//            return ResponseEntity.ok("Produto inserido com sucesso");
//        }else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @DeleteMapping("/excluir/{id}")
//    public ResponseEntity<String> excluirProduto(@PathVariable Long id){
////        //4 consultas
////        if (produtoRepository.existsById(id)) {
////            produtoRepository.deleteById(id);
////            return ResponseEntity.ok("Produto excluído com sucesso");
////        }else {
////            return ResponseEntity.status(404).body("Produto não encontrado");
////        }
//
//        //Apenas 2 consultas
//        Optional<Produto> produto = produtoRepository.findById(id);
//        if (produto.isPresent()){
//            produtoRepository.deleteById(id);
//            return ResponseEntity.ok("Produto excluido com sucesso");
//        }else {
//            return ResponseEntity.status(404).body("Este id não existe");
//        }
//
//    }
//
//    @PutMapping("/atualizar/{id}")
//    public ResponseEntity<String> atualizarProduto(@PathVariable Long id, @RequestBody Produto produtoAtualizado){
//        Optional<Produto> produtoExistente = produtoRepository.findById(id);
//        if (produtoExistente.isPresent()){
//            Produto produto = produtoExistente.get();
//            produto.setNome(produtoAtualizado.getNome());
//            produto.setDescricao(produtoAtualizado.getDescricao());
//            produto.setPreco(produtoAtualizado.getPreco());
//            produto.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
//            produtoRepository.save(produto);
//            return ResponseEntity.ok("Produto atualizado com sucesso");
//        }else {
//            return ResponseEntity.notFound().build();
//        }
//    }
//
//    @PatchMapping("/atualizarParcial/{id}")
//    public ResponseEntity<String> atualizarProdutoParcial(@PathVariable Long id, @RequestBody Map<String, Object> updates){
//        Optional<Produto> produtoExistente = produtoRepository.findById(id);
//        if (produtoExistente.isPresent()){
//            Produto produto = produtoExistente.get();
//
//            //Apenas os campos que foram passados
//            if (updates.containsKey("nome")){
//                produto.setNome((String) updates.get("nome"));
//            }
//            if (updates.containsKey("descricao")){
//                produto.setDescricao((String) updates.get("descricao"));
//            }
//            if (updates.containsKey("preco")){
//                produto.setPreco((double) updates.get("preco"));
//            }
//            if (updates.containsKey("quantidadeEstoque")){
//                produto.setQuantidadeEstoque((int) updates.get("quantidadeEstoque"));
//            }
//            produtoRepository.save(produto);
//            return ResponseEntity.ok("Produto atualizado parcialmente com sucesso");
//        }else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Produto com ID " + id + " não encontrado");
//        }
//
//    }
//}
