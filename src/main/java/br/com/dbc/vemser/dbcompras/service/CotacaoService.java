package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CotacaoService {


    private final ObjectMapper objectMapper;

    private final CotacaoRepository cotacaoRepository;

    private final UsuarioService usuarioService;

    private final ItemRepository itemRepository;
    private CotacaoEntity converterCotacaoCreateDTOToCotacaoEntity(CotacaoCreateDTO cotacaoDTO) {
        return objectMapper.convertValue(cotacaoDTO, CotacaoEntity.class);
    }

    private CotacaoDTO converterCotacaoEntityToCotacaoDTO(CotacaoEntity cotacao) {
        return objectMapper.convertValue(cotacao, CotacaoDTO.class);
    }

    public CotacaoDTO create (CotacaoCreateDTO cotacaoDTO) throws UsuarioException {

        UsuarioEntity usuario = usuarioService.retornarUsuarioEntityLogado();

        CotacaoEntity cotacao = converterCotacaoCreateDTOToCotacaoEntity(cotacaoDTO);
        cotacao.setStatus(false);
        cotacao.setUsuario(usuario);
        cotacao.setLocalDate(LocalDateTime.now());
        Set<ItemEntity> itens = cotacaoDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .peek(itemEntity -> itemEntity.setCotacoes(Set.of(cotacao)))
                .map(itemRepository::save)
                .collect(Collectors.toSet());

        cotacaoRepository.save(cotacao);
        return converterCotacaoEntityToCotacaoDTO(cotacao);
    }

}
