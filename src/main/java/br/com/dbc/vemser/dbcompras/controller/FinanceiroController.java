package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoFinanceiroDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.CompraService;
import br.com.dbc.vemser.dbcompras.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/financeiro")
public class FinanceiroController {

   private final CompraService compraService;

   @GetMapping("/listar-compras")
   public List<CompraWithValorItensDTO> compras (@RequestParam(name = "idCompra", required = false) Integer idCompra) throws UsuarioException {
       return compraService.list(idCompra);
   }

   @PutMapping("aprovar-reprovar-compra/{idCompra}")
   public CompraWithValorItensDTO aprovarcompraOuReprovar (@PathVariable("idCompra") Integer idCompra , StatusCompra statusCompra) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {

       return compraService.aprovarReprovarCompra(idCompra, statusCompra);

    }

}
