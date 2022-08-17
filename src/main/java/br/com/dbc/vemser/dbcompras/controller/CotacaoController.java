package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cotacao")
public class CotacaoController {

    private final CotacaoService cotacaoService;

    @PostMapping("/create/{idCompra}")
    public ResponseEntity<CotacaoDTO> create (@PathVariable("idCompra") Integer idCompra,
            @Valid @RequestBody CotacaoCreateDTO cotacaoCreateDTO) throws UsuarioException, RegraDeNegocioException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cotacaoService.create(cotacaoCreateDTO, idCompra));
    }

    @GetMapping
    public ResponseEntity<List<CotacaoDTO>> list() throws UsuarioException {
        return ResponseEntity.ok(cotacaoService.list());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CotacaoDTO> update (@Valid @RequestBody CotacaoCreateDTO cotacaoCreateDTO, @PathVariable("id") Integer idCotacao) throws EntidadeNaoEncontradaException, UsuarioException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cotacaoService.update(cotacaoCreateDTO,idCotacao));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Valid> delete (@PathVariable("id") Integer idCotacao) throws EntidadeNaoEncontradaException, UsuarioException {
        cotacaoService.deleteCotacao(idCotacao);
        return ResponseEntity.noContent().build();
    }

}