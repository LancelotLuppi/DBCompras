package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompraService {

    private final ObjectMapper objectMapper;

    private final CompraRepository compraRepository;

    private final UsuarioService usuarioService;


    public CompraDTO create (CompraCreateDTO compraCreateDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        CompraEntity compra = converterCompraCreateDTOToCompraEntity(compraCreateDTO);
        compra.setDataCompra(LocalDateTime.now());
        compra.setUsuario(usuario);
        compraRepository.save(compra);

        return converterCompraEntityToCompraDTO(compra);

    }

    public CompraEntity findById (Integer id){
        return objectMapper.convertValue(compraRepository.findById(id), CompraEntity.class);
    }

    public CompraDTO update (Integer idCompra , CompraUpdateDTO compraDTO, StatusCotacoes status) {


        CompraEntity compra = findById(idCompra);

        if(compraDTO.getName() != null){
            compra.setName(compraDTO.getName());
        }

        if(compraDTO.getValor() != null){
            compra.setValor(compraDTO.getValor());
        }

        compraRepository.save(compra);
        return converterCompraEntityToCompraDTO(compra);

    }

    public List<CompraDTO> list() {

        return compraRepository.findAll()
                .stream()
                .map(this::converterCompraEntityToCompraDTO)
                .toList();

    }

    public void delete (Integer id) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();
        compraRepository.deleteById(id);

    }

    public void verificarCompraDoUserLogado(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();
        List<Integer> idCompras = usuario.getCompras().stream()
                .map(CompraEntity::getIdCompra).toList();
        if(!idCompras.contains(idCompra)) {
            throw new RegraDeNegocioException("Esta compra não é sua!");
        }
    }

    private CompraDTO converterCompraEntityToCompraDTO(CompraEntity compra) {
        return objectMapper.convertValue(compra, CompraDTO.class);
    }

    private CompraEntity converterCompraCreateDTOToCompraEntity(CompraCreateDTO compraCreateDTO) {
        return objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
    }
}
