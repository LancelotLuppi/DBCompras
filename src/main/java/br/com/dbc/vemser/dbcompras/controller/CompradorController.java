package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.ComprasComCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.CompraService;
import br.com.dbc.vemser.dbcompras.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comprador")
public class CompradorController {

    private final CotacaoService cotacaoService;
    private final CompraService compraService;

    @PostMapping("/cotar")
    public ResponseEntity<Void> create(Integer idCompra, @Valid @RequestBody CotacaoCreateDTO cotacaoCreateDTO) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        cotacaoService.create(idCompra, cotacaoCreateDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ComprasComCotacaoDTO>> listarCompras (@RequestParam(name = "idCompra", required = false) Integer idCompra) {
        return ResponseEntity.ok(cotacaoService.listarCompraComCotacao(idCompra));
    }

    @PutMapping("/concluir-cotacao")
    public ResponseEntity<CompraDTO> finalizarCotacao(Integer idCompra) throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        return ResponseEntity.ok(compraService.finalizarCotacao(idCompra));
    }
}
