package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraListCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoRelatorioDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CotacaoService {


    private final ObjectMapper objectMapper;
    private final CotacaoRepository cotacaoRepository;
    private final ItemRepository itemRepository;

    private final CompraRepository compraRepository;
    private final CotacaoXItemRepository cotacaoXItemRepository;


    public void create (Integer idCompra, CotacaoCreateDTO cotacaoDTO) throws EntidadeNaoEncontradaException {
        CompraEntity compraCotada = compraRepository.findById(idCompra).orElseThrow(() -> new EntidadeNaoEncontradaException("Compra inexistente"));
        CotacaoEntity cotacao = new CotacaoEntity();

        cotacao.setNome(cotacaoDTO.getNome());
        cotacao.setStatus(StatusCotacoes.EM_ABERTO.getSituacaoCompra());
        cotacao.setLocalDate(LocalDateTime.now());
        cotacao.setAnexo(Base64.getDecoder().decode(cotacaoDTO.getAnexo()));
        cotacao.setValor(0.0);
        cotacao.setCompra(compraCotada);
        CotacaoEntity cotacaoSalva = cotacaoRepository.save(cotacao);

        cotacaoDTO.getListaDeValores()
                .forEach(item -> {
                    CotacaoXItemEntity cotacaoXItem = new CotacaoXItemEntity();
                    CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
                    ItemEntity itemEntity = itemRepository.findById(item.getIdItem()).get();
                    cotacaoXItemPK.setIdCotacao(cotacaoSalva.getIdCotacao());
                    cotacaoXItemPK.setIdItem(item.getIdItem());
                    cotacaoXItem.setCotacaoXItemPK(cotacaoXItemPK);
                    cotacaoXItem.setCotacao(cotacaoSalva);
                    cotacaoXItem.setItem(itemEntity);
                    cotacaoXItem.setValorDoItem(item.getValorDoItem());
                    cotacaoXItem.setValorTotal(item.getValorDoItem() * itemEntity.getQuantidade());
                    cotacaoXItemRepository.save(cotacaoXItem);
                    cotacaoSalva.setValor(cotacaoSalva.getValor() + cotacaoXItem.getValorTotal());
                });
        cotacaoRepository.save(cotacaoSalva);
    }

    public List<CotacaoDTO> listarCotacoes(Integer idCotacao) {
        List<CotacaoRelatorioDTO> cotacoes = cotacaoRepository.listCotacoes(idCotacao);

        return cotacoes.stream()
                .map(relatorio -> {
                    CotacaoDTO cotacao = objectMapper.convertValue(relatorio, CotacaoDTO.class);
                    cotacao.setAnexo(Base64.getEncoder().encodeToString(relatorio.getAnexo()));
                    CompraListCotacaoDTO compraRelatorioDTO = compraRepository.listCompraByIdCotacao(cotacao.getIdCotacao());
                    CompraWithValorItensDTO compraDTO = new CompraWithValorItensDTO();
                    compraDTO.setIdCompra(compraRelatorioDTO.getIdCompra());
                    compraDTO.setDataCompra(compraRelatorioDTO.getDataCompra());
                    compraDTO.setStatus(compraRelatorioDTO.getStatus());
                    compraDTO.setValor(compraRelatorioDTO.getValorTotal());
                    compraDTO.setDescricao(compraRelatorioDTO.getDescricao());
                    compraDTO.setName(compraRelatorioDTO.getName());
                    List<ItemValorizadoDTO> itensComValorDTO = itemRepository.listItensComValorByIdCompra(compraDTO.getIdCompra());

                    compraDTO.setItens(itensComValorDTO);
                    cotacao.setCompraDTO(compraDTO);
                    return cotacao;
                })
                .toList();
    }

}
