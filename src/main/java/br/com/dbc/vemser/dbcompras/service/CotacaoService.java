package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.ComprasComCotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoComItemDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
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
import br.com.dbc.vemser.dbcompras.util.ItemServiceUtil;
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
    private final ItemServiceUtil itemServiceUtil;
    private final EmailService emailService;


    public void create(Integer idCompra, CotacaoCreateDTO cotacaoDTO) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        CompraEntity compraCotada = compraRepository.findById(idCompra).orElseThrow(() -> new EntidadeNaoEncontradaException("Compra inexistente"));
        List<Integer> idsCotados = cotacaoDTO.getListaDeValores().stream()
                .map(CotacaoValorItensDTO::getIdItem)
                .toList();

        itemServiceUtil.verificarItensDaCompra(compraCotada, idsCotados);
        if (cotacaoDTO.getListaDeValores().size() != compraCotada.getItens().size()) {
            throw new RegraDeNegocioException("Todos os itens desse compra devem ser cotados");
        }

        compraCotada.setStatus(StatusCompra.EM_COTACAO);

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

    public List<ComprasComCotacaoDTO> listarCompraComCotacao() {
        // FIXME Melhorar consulta / performance (1 consulta só?)
        return compraRepository.findAll()
                .stream()
                .map(compraEntity -> {
                    ComprasComCotacaoDTO comprasComCotacaoDTO = objectMapper.convertValue(compraEntity, ComprasComCotacaoDTO.class);
                    List<CotacaoComItemDTO> cotacaoComItemDTOS = compraEntity.getCotacoes()
                            .stream()
                            .map(cotacaoEntity -> {
                                CotacaoComItemDTO cotacaoComItemDTO = objectMapper.convertValue(cotacaoEntity, CotacaoComItemDTO.class);
                                List<ItemValorizadoDTO> itemValorizadoDTOS = compraEntity.getItens()
                                        .stream()
                                        .map(item -> {
                                            ItemValorizadoDTO itemComValor = new ItemValorizadoDTO();

                                            CotacaoXItemPK cotacaoXItemPK = new CotacaoXItemPK();
                                            cotacaoXItemPK.setIdCotacao(cotacaoEntity.getIdCotacao());
                                            cotacaoXItemPK.setIdItem(item.getIdItem());

                                            CotacaoXItemEntity cotacaoXItem = cotacaoXItemRepository.findById(cotacaoXItemPK).get();
                                            itemComValor.setIdItem(item.getIdItem());
                                            itemComValor.setNome(item.getNome());
                                            itemComValor.setValorUnitario(cotacaoXItem.getValorDoItem());
                                            itemComValor.setQuantidade(item.getQuantidade());
                                            itemComValor.setValorTotal(cotacaoXItem.getValorTotal());
                                            return itemComValor;
                                        }).toList();
                                double valorCotacao = 0.0;

                                for (ItemValorizadoDTO itemValorizadoDTO : itemValorizadoDTOS) {
                                    valorCotacao += itemValorizadoDTO.getValorUnitario() * itemValorizadoDTO.getQuantidade();
                                }
                                cotacaoComItemDTO.setValor(valorCotacao);
                                cotacaoComItemDTO.setItemValorizadoDTOS(itemValorizadoDTOS);
                                return cotacaoComItemDTO;
                            })
                            .toList();

                    double valorTotal = 0;
                    for (CotacaoComItemDTO cotacaoComItemDTO : cotacaoComItemDTOS) {
                        if (cotacaoComItemDTO.getValor() != null) {
                            valorTotal += cotacaoComItemDTO.getValor();
                        }
                    }
                    comprasComCotacaoDTO.setValorTotal(valorTotal);
                    comprasComCotacaoDTO.setCotacoes(cotacaoComItemDTOS);
                    return comprasComCotacaoDTO;
                }).toList();

    }


    public CotacaoDTO aprovarOuReprovarCotacao(Integer idCotacao, EnumAprovacao enumAprovacao) throws EntidadeNaoEncontradaException, RegraDeNegocioException {

        CotacaoEntity cotacao = cotacaoServiceUtil.findById(idCotacao);
        CompraEntity compra = compraServiceUtil.findByIDCompra(cotacao.getCompra().getIdCompra());

        cotacaoServiceUtil.verificarStatusDaCompraAndCotacao(compra, cotacao);

        cotacao.setStatus(enumAprovacao.equals(EnumAprovacao.APROVAR) ? StatusCotacao.APROVADO : StatusCotacao.REPROVADO);

        compra.setStatus(StatusCompra.APROVADO_GESTOR);
        compra.setValorTotal(cotacao.getValor());

        compraRepository.save(compra);
        cotacao = cotacaoRepository.save(cotacao);

        emailService.sendEmail(compra.getUsuario().getNome(), compra.getName(), compra.getUsuario().getNome(), compra.getStatus().getStatusCompra());

        return cotacaoServiceUtil.converterCotacaoToCotacaoDTO(cotacao);
    }

}
