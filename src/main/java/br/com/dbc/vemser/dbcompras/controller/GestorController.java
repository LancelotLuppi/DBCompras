package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.compra.ComprasComCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/gestor")
public class GestorController {


    private final CotacaoService cotacaoService;



    @PostMapping("/aprovarReprovar/{idCotacao}/status")
    public CotacaoDTO aprovarOuReprovarCotacao(@PathVariable("idCotacao") Integer idCotacao,
                                                @RequestParam(value = "status") EnumAprovacao aprovacao) throws EntidadeNaoEncontradaException, RegraDeNegocioException {
         return cotacaoService.aprovarOuReprovarCotacao(idCotacao, aprovacao);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<ComprasComCotacaoDTO>> listarCompras () {
        return ResponseEntity.ok(cotacaoService.cotacaoComCompraList());
    }


}
