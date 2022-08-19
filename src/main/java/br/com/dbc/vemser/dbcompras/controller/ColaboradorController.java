package br.com.dbc.vemser.dbcompras.controller;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraListDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraRelatorioDTO;
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
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/colaborador")
public class ColaboradorController {
    private final CompraService compraService;

    @PostMapping("/nova-compra")
    public ResponseEntity<CompraDTO> create (@Valid @RequestBody CompraCreateDTO compraCreateDTO) throws UsuarioException, RegraDeNegocioException {
        return ResponseEntity.status(HttpStatus.CREATED).body(compraService.create(compraCreateDTO));
    }

    @PutMapping("/compra/{id}")
    public ResponseEntity<CompraDTO> update (@PathVariable Integer id , @Valid @RequestBody CompraCreateDTO compraUpdateDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        return ResponseEntity.status(HttpStatus.OK).body(compraService.update(id, compraUpdateDTO));
    }

    @GetMapping("/compras")
    public ResponseEntity<List<CompraListDTO>> listCompras(@RequestParam(required = false) Integer idCompra) throws UsuarioException {
        return ResponseEntity.ok(compraService.listColaborador(idCompra));
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

    @GetMapping("/compras-id")
    public ResponseEntity<List<CompraRelatorioDTO>> listarComprasPorId (@RequestParam(required = false) Integer idCompra){
        return ResponseEntity.ok(compraService.relatorioCompras(idCompra));
    }

}
