package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoFinanceiroDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
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

   private final CotacaoService cotacaoService;

   @GetMapping("/listar-cotacoes")
   public List<CotacaoFinanceiroDTO> cotacoes () throws UsuarioException {
       return cotacaoService.list();
   }

   @PutMapping("aprovar-reprovar-cotacao/{idCotacao}")
   public CotacaoDTO aprovarCotacaoOuReprovar (@PathVariable("idCotacao") Integer idCotacao , StatusCotacoes statusCotacoes) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {

       return cotacaoService.cotacaoAprovada(idCotacao, statusCotacoes);

    }

}
