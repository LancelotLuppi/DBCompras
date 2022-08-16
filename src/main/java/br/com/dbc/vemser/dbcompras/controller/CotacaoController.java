package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.CotacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/cotacao")
public class CotacaoController {

    private final CotacaoService cotacaoService;

    @PostMapping("/create")
    public ResponseEntity<CotacaoDTO> create (@Valid @RequestBody CotacaoCreateDTO cotacaoCreateDTO) throws UsuarioException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(cotacaoService.create(cotacaoCreateDTO));
    }

}
