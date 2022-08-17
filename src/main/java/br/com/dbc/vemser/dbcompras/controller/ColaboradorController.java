package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.service.CompraService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/colaborador")
public class ColaboradorController {
    private final CompraService compraService;

    @PostMapping("/nova-compra")
    public ResponseEntity<CompraDTO> create (@Valid @RequestBody CompraCreateDTO compraCreateDTO) throws UsuarioException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(compraService.create(compraCreateDTO));
    }

    @PutMapping("/compra/{id}")
    public ResponseEntity<CompraDTO> update (@PathVariable Integer id , @Valid @RequestBody CompraCreateDTO compraUpdateDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(compraService.update(id, compraUpdateDTO));
    }

    @DeleteMapping("/compra/{id}")
    public ResponseEntity<Void> delete (@PathVariable("id") Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        compraService.delete(idCompra);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/item/{idCompra}/{idItem}")
    public ResponseEntity<Void> deleteItem (@PathVariable("idCompra") Integer idCompra,
                                             @PathVariable("idItem") Integer idItem) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException, RegraDeNegocioException {
        compraService.removerItensDaCompra(idCompra, idItem);
        return ResponseEntity.noContent().build();
    }

}
