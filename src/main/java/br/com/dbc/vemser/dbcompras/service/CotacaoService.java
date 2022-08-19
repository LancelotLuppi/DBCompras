package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraListCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.*;
import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.*;
import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.CotacaoServiceUtil;
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
    private final CompraServiceUtil compraServiceUtil;
    private final CotacaoServiceUtil cotacaoServiceUtil;


    public void create(Integer idCompra, CotacaoCreateDTO cotacaoDTO) throws EntidadeNaoEncontradaException, UsuarioException {
        CompraEntity compraCotada = compraRepository.findById(idCompra).orElseThrow(() -> new EntidadeNaoEncontradaException("Compra inexistente"));
        CotacaoEntity cotacao = new CotacaoEntity();

        cotacao.setNome(cotacaoDTO.getNome());
        cotacao.setStatus(StatusCotacao.EM_ABERTO);
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

    public CotacaoEntity findById(Integer idCotacao) throws EntidadeNaoEncontradaException {
        return cotacaoRepository.findById(idCotacao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Essa cotação não existe"));
    }

    public CotacaoDTO aprovarOuReprovarCotacao(Integer idCotacao, EnumAprovacao enumAprovacao) throws EntidadeNaoEncontradaException, RegraDeNegocioException {

        CotacaoEntity cotacao = findById(idCotacao);
        CompraEntity compra = compraServiceUtil.findByIDCompra(cotacao.getCompra().getIdCompra());

        cotacaoServiceUtil.verificarStatusDaCompraAndCotacao(compra, cotacao);

        cotacao.setStatus(enumAprovacao.equals(EnumAprovacao.APROVAR) ? StatusCotacao.APROVADO : StatusCotacao.REPROVADO);
        compra.setStatus(StatusCompra.APROVADO_GESTOR);
        compra.setValorTotal(cotacao.getValor());

        cotacaoRepository.save(cotacao);
        compraRepository.save(compra);
        return converterCotacaoToCotacaoDTO(cotacao);

    }

    private CotacaoDTO converterCotacaoToCotacaoDTO(CotacaoEntity cotacao) {
        CompraWithValorItensDTO compra = objectMapper.convertValue(cotacao.getCompra(), CompraWithValorItensDTO.class);
        List<CotacaoValorItensDTO> itemDTOList = cotacao.getItens().stream()
                .map(item -> objectMapper.convertValue(item, CotacaoValorItensDTO.class))
                .toList();
        CotacaoDTO cotacaoDTO = objectMapper.convertValue(cotacao, CotacaoDTO.class);
        cotacaoDTO.setCompraDTO(compra);
        cotacaoDTO.setListaDeValores(itemDTOList);
        return cotacaoDTO;
    }
}
