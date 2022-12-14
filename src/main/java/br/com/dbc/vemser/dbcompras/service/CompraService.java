package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.compra.*;
import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.EnumAprovacao;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CompraRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import br.com.dbc.vemser.dbcompras.util.CompraServiceUtil;
import br.com.dbc.vemser.dbcompras.util.ExceptionUtil;
import br.com.dbc.vemser.dbcompras.util.ItemServiceUtil;
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompraService {

    private final ObjectMapper objectMapper;
    private final CompraRepository compraRepository;
    private final CompraServiceUtil compraServiceUtil;
    private final ItemRepository itemRepository;
    private final UsuarioServiceUtil usuarioServiceUtil;
    private final ItemServiceUtil itemServiceUtil;
    private final ExceptionUtil exceptionUtil;
    private final EmailService emailService;


    public CompraDTO create(CompraCreateDTO compraCreateDTO) throws UsuarioException, RegraDeNegocioException {

        exceptionUtil
                .verificarCondicaoException(compraCreateDTO.getItens().isEmpty(),
                        "N??o ?? poss??vel cadastrar uma compra sem itens");

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();

        CompraEntity compra = objectMapper.convertValue(compraCreateDTO, CompraEntity.class);
        compra.setDataCompra(LocalDateTime.now());
        compra.setStatus(StatusCompra.ABERTO);
        compra.setUsuario(usuario);
        CompraEntity compraSalva = compraRepository.save(compra);

        Set<ItemEntity> itens = compraServiceUtil.salvarItensDaCompra(compraCreateDTO, compraSalva);
        compraSalva.setItens(itens);
        emailService.sendEmail(usuario.getNome(), compraSalva.getName(), usuario.getEmail(), StatusCompra.ABERTO.getStatusCompra());

        return compraServiceUtil.converterCompraEntityToCompraDTO(compraSalva);
    }

    public List<CompraListDTO> listarComprasColaborador(Integer idCompra) throws UsuarioException, RegraDeNegocioException {

        if (idCompra != null) {
            compraServiceUtil.verificarCompraDoUserLogado(idCompra);
            return compraRepository.findById(idCompra)
                    .stream()
                    .map(compraServiceUtil::converterEntityParaListDTO)
                    .toList();
        } else {
            return compraRepository.findAllByUsuarioId(usuarioServiceUtil.getIdLoggedUser()).stream()
                    .map(compraServiceUtil::converterEntityParaListDTO)
                    .toList();
        }
    }

    public CompraDTO updateComNovosItens(Integer idCompra, CompraCreateDTO compraUpdate) throws UsuarioException, RegraDeNegocioException, EntidadeNaoEncontradaException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByIDCompra(idCompra);

        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.ABERTO),
                        "Apenas itens em ABERTO podem ser atualizados!");

        List<Integer> idsAntigosItens = compra.getItens().stream()
                .map(ItemEntity::getIdItem).toList();

        Set<ItemEntity> novosItens = compraUpdate.getItens().stream()
                .map(itemUpdated -> {
                    ItemEntity novoItem = objectMapper.convertValue(itemUpdated, ItemEntity.class);
                    novoItem.setCompra(compra);
                    return itemRepository.save(novoItem);
                })
                .collect(Collectors.toSet());
        compra.setItens(novosItens);
        compra.setName(compraUpdate.getName());
        compra.setDescricao(compraUpdate.getDescricao());
        CompraEntity compraAtualizada = compraRepository.save(compra);

        itemRepository.deleteAllById(idsAntigosItens);

        return compraServiceUtil.converterCompraEntityToCompraDTO(compraAtualizada);
    }

    public CompraDTO update(Integer idCompra, CompraUpdateDTO compraDTO) throws UsuarioException, EntidadeNaoEncontradaException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByIDCompra(idCompra);

        List<Integer> idItemRecebidoList = compraDTO.getItens().stream()
                .map(ItemUpdateDTO::getIdItem)
                .toList();

        itemServiceUtil.verificarItensDaCompra(compra, idItemRecebidoList);

        compra.setName(compra.getName());
        compra.setDescricao(compraDTO.getDescricao());
        compraDTO.getItens()
                .forEach(item -> {
                    ItemEntity itemEntity = itemRepository.findById(item.getIdItem()).get();
                    itemEntity.setNome(item.getNome());
                    itemEntity.setQuantidade(item.getQuantidade());
                    itemRepository.save(itemEntity);
                });

        CompraEntity compraAtualizada = compraRepository.save(compra);
        return compraServiceUtil.converterCompraEntityToCompraDTO(compraAtualizada);
    }

    public void delete(Integer idCompra) throws UsuarioException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        compraRepository.deleteCompra(idCompra);
    }


    public void removerItensDaCompra(Integer idCompra, Integer idItem) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CompraEntity compra = compraServiceUtil.findByIDCompra(idCompra);
        itemServiceUtil.verificarItensDaCompra(compra, List.of(idCompra));

        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.ABERTO),
                        "Apenas compras em status ABERTO podem ser alteradas");


        Set<ItemEntity> itemEntities = compra.getItens();

        itemEntities.removeIf(itemEntity -> idItem.equals(itemEntity.getIdItem()));
        itemRepository.delete(itemRepository.findById(idItem).get());
        compra.setItens(itemEntities);
        compraRepository.save(compra);
        compraServiceUtil.converterCompraEntityToCompraDTO(compra);
    }

    public List<CompraRelatorioRetornoDTO> relatorioCompras(Integer idCompra) {
        List<CompraRelatorioDTO> compras = compraRepository.findByCompraId(idCompra);
        return compras.stream()
                .map(compraDTO -> {
                    CompraEntity compra = compraRepository.findById(compraDTO.getIdCompra()).get();
                    CompraRelatorioRetornoDTO retorno = objectMapper.convertValue(compraDTO, CompraRelatorioRetornoDTO.class);
                    retorno.setItens(compra.getItens().stream()
                            .map(item -> objectMapper.convertValue(item, ItemDTO.class))
                            .toList());
                    return retorno;
                })
                .toList();
    }

    public CompraWithValorItensDTO aprovarReprovarCompra(Integer idCompra, EnumAprovacao aprovacao) throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = compraRepository.findById(idCompra)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Est?? compra n??o existe"));

        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.APROVADO_GESTOR),
                        "Esta compra n??o pode ser aprovada!");

        compra.setStatus(aprovacao == EnumAprovacao.APROVAR ? StatusCompra.APROVADO_FINANCEIRO : StatusCompra.REPROVADO_FINANCEIRO);

        CompraEntity compraSalva = compraRepository.save(compra);
        emailService.sendEmail(compraSalva.getUsuario().getNome(), compraSalva.getName(), compraSalva.getUsuario().getEmail(), compraSalva.getStatus().getStatusCompra());
        return compraServiceUtil.converterCompraEntityToCompraWithValor(compra);
    }

    public CompraDTO finalizarCotacao(Integer idCompra) throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = compraRepository.findById(idCompra).orElseThrow(() -> new EntidadeNaoEncontradaException("Compra inexistente"));

        exceptionUtil
                .verificarCondicaoException(compra.getCotacoes().size() < 2,
                        "A compra deve ter ao menos duas cota????es registradas para ser finalizada");
        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.EM_COTACAO),
                        "Apenas compras com status EM_COTACAO podem ter a cotacao concluida");

        compra.setStatus(StatusCompra.COTADO);
        return compraServiceUtil.converterCompraEntityToCompraDTO(compraRepository.save(compra));
    }

    public CompraDTO reprovarCompraGestor(Integer idCompra) throws EntidadeNaoEncontradaException, RegraDeNegocioException {
        CompraEntity compra = compraServiceUtil.findByIDCompra(idCompra);

        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.COTADO),
                        "Compras que n??o est??o com status COTADO n??o podem ser reprovadas");

        compra.setStatus(StatusCompra.REPROVADO_GESTOR);
        compra = compraRepository.save(compra);
        emailService.sendEmail(compra.getUsuario().getNome(), compra.getName(), compra.getUsuario().getEmail(), compra.getStatus().getStatusCompra());
        return compraServiceUtil.converterCompraEntityToCompraDTO(compra);
    }
}
