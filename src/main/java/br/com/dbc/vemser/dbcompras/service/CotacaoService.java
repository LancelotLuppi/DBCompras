package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoItemPk;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoItemRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CotacaoService {


    private final ObjectMapper objectMapper;
    private final CotacaoRepository cotacaoRepository;
    private final ItemRepository itemRepository;
    private final UsuarioServiceUtil usuarioServiceUtil;

    private final CompraRepository compraRepository;

    private final CompraServiceUtil compraServiceUtil;

    private final CotacaoItemRepository cotacaoItemRepository;

    private CotacaoEntity converterCotacaoCreateDTOToCotacaoEntity(CotacaoCreateDTO cotacaoDTO) {
        return objectMapper.convertValue(cotacaoDTO, CotacaoEntity.class);
    }

    private CotacaoDTO converterCotacaoEntityToCotacaoDTO(CotacaoEntity cotacao) {
        CotacaoDTO cotacaoDTO = objectMapper.convertValue(cotacao, CotacaoDTO.class);
        cotacaoDTO.setItens(cotacao.getItens()
                .stream()
                .map(itemEntity -> objectMapper.convertValue(itemEntity, ItemDTO.class)).collect(Collectors.toSet()));
        return cotacaoDTO;
    }

    public CotacaoDTO create (CotacaoCreateDTO cotacaoDTO, Integer idCompra) throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CotacaoEntity cotacao = converterCotacaoCreateDTOToCotacaoEntity(cotacaoDTO);
        cotacao.setStatus(false);
        cotacao.setUsuario(usuario);
        cotacao.setLocalDate(LocalDateTime.now());
        Set<ItemEntity> itens = cotacaoDTO.getItens().stream()
                .map(item -> objectMapper.convertValue(item, ItemEntity.class))
                .map(itemRepository::save)
                .collect(Collectors.toSet());
        cotacao.setItens(itens);
        cotacao.setCompras(compraRepository.findById(idCompra).get());
        cotacaoRepository.save(cotacao);

        for(ItemEntity item : itens){
            System.out.println(item);
            criarCotacaoItemEntity(item, cotacao, item.getPreco());

        }

        return converterCotacaoEntityToCotacaoDTO(cotacao);
    }

    public CotacaoItemEntity criarCotacaoItemEntity (ItemEntity item, CotacaoEntity cotacao, Double valorItem){
        CotacaoItemEntity cotacaoItem = new CotacaoItemEntity();
        cotacaoItem.setCotacao(cotacao);
        cotacaoItem.setItem(item);
        cotacaoItem.setValorDoItem(valorItem);
        CotacaoItemPk cotacaoItemPk = new CotacaoItemPk(cotacao.getIdCotacao(), item.getIdItem());
        cotacaoItem.setCotacaoItemPk(cotacaoItemPk);
        cotacaoItemRepository.save(cotacaoItem);
        return cotacaoItem;
    }

    public List<CotacaoDTO> list() throws UsuarioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();

        return cotacaoRepository.findByUsuario(usuario.getIdUser())
                .stream()
                .map(this::converterCotacaoEntityToCotacaoDTO)
                .toList();

    }

    public CotacaoEntity findById (Integer idCotacao) throws EntidadeNaoEncontradaException, UsuarioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();

        return cotacaoRepository.findById(idCotacao)
                .orElseThrow(() -> new  EntidadeNaoEncontradaException("Essa cotação não existe!"));

    }

    public CotacaoDTO update (CotacaoCreateDTO cotacaoUpdateDTO, Integer idCotacao) throws UsuarioException, EntidadeNaoEncontradaException {

        CotacaoEntity cotacao = findById(idCotacao);
        cotacao.setIdCotacao(idCotacao);

        if(cotacaoUpdateDTO.getNome() != null){
            cotacao.setNome(cotacaoUpdateDTO.getNome());
        }

        if(cotacaoUpdateDTO.getAnexo() != null){
            cotacao.setAnexo(cotacaoUpdateDTO.getAnexo());
        }

        if(cotacaoUpdateDTO.getItens() != null){

            Set<ItemEntity> itensRecuperados = cotacao.getItens();
            itensRecuperados = cotacaoUpdateDTO.getItens().stream()
                    .map(itemDTO -> objectMapper.convertValue(itemDTO, ItemEntity.class))
                    .collect(Collectors.toSet());
            cotacao.getItens().clear();
            cotacao.setItens(itensRecuperados);

        }

        CotacaoEntity cotacaoAtualizada = cotacaoRepository.save(cotacao);
        return converterCotacaoEntityToCotacaoDTO(cotacaoAtualizada);

    }

    public void deleteCotacao (Integer idCotacao) throws EntidadeNaoEncontradaException, UsuarioException {

        CotacaoEntity cotacao = findById(idCotacao);
        cotacaoRepository.delete(cotacao);

    }

    public CotacaoDTO cotacaoAprovada(Integer idCotacao, StatusCotacoes status) throws EntidadeNaoEncontradaException, UsuarioException {

        CotacaoEntity cotacao = findById(idCotacao);
        cotacao.setStatus(status.getSituacaoCompra());
        cotacaoRepository.save(cotacao);
        return converterCotacaoEntityToCotacaoDTO(cotacao);

    }


}
