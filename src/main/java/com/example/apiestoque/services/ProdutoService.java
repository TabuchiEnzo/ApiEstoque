package com.example.apiestoque.services;

import com.example.apiestoque.models.Produto;
import com.example.apiestoque.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public List<Produto> buscarTodosProdutos() {
        return produtoRepository.findAll();
    }

    public Produto salvarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public boolean atualizarProduto(Long id, Produto produtoAtualizado) {
        Optional<Produto> produtoExistenteOptional = produtoRepository.findById(id);
        if (produtoExistenteOptional.isPresent()) {
            Produto produtoExistente = produtoExistenteOptional.get();
            produtoExistente.setNome(produtoAtualizado.getNome());
            produtoExistente.setDescricao(produtoAtualizado.getDescricao());
            produtoExistente.setPreco(produtoAtualizado.getPreco());
            produtoExistente.setQuantidadeEstoque(produtoAtualizado.getQuantidadeEstoque());
            produtoRepository.save(produtoExistente);
            return true;
        } else {
            return false;
        }
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeIgnoreCase(nome);
    }

    public Optional<Produto> buscarProdutoPorId(Long id) {
        return produtoRepository.findById(id);
    }

    public boolean excluirProduto(Long id) {
        Optional<Produto> produtoExistenteOptional = produtoRepository.findById(id);
        if (produtoExistenteOptional.isPresent()) {
            produtoRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    // criando o metodo findByNomeIgnoreCaseAndPrecoLessThan para buscar por nome e preco
    public List<Produto> buscarPorNomeEPrecoMenorQue(String nome, BigDecimal preco) {
        return produtoRepository.findByNomeIgnoreCaseAndPrecoLessThan(nome, preco);
    }
}