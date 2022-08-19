package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoFinanceiroDTO;
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

    private CotacaoFinanceiroDTO converterCotacaoEntityToCotacaoFinanceiroDTO(CotacaoEntity cotacao) {
        CotacaoFinanceiroDTO cotacaoDTO = objectMapper.convertValue(cotacao, CotacaoFinanceiroDTO.class);
        cotacaoDTO.setItens(cotacao.getItens()
                .stream()
                .map(itemEntity -> objectMapper.convertValue(itemEntity, ItemDTO.class)).collect(Collectors.toSet()));
        return cotacaoDTO;
    }

    public CotacaoDTO create (CotacaoCreateDTO cotacaoDTO, Integer idCompra) throws UsuarioException, RegraDeNegocioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();
        compraServiceUtil.verificarCompraDoUserLogado(idCompra);
        CotacaoEntity cotacao = converterCotacaoCreateDTOToCotacaoEntity(cotacaoDTO);
        cotacao.setStatus(StatusCotacoes.EM_ABERTO.getSituacaoCompra());
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
        Double valorTotal = calcularValorTotalCotacao(cotacao);
        cotacao.setValorTotal(valorTotal);
        System.out.println(valorTotal);
        cotacaoRepository.save(cotacao);
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

    public List<CotacaoFinanceiroDTO> list() throws UsuarioException {

        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();

        return cotacaoRepository.findByUsuario(usuario.getIdUser())
                .stream()
                .map(this::converterCotacaoEntityToCotacaoFinanceiroDTO)
                .toList();

    }


    public Double calcularValorTotalCotacao (CotacaoEntity cotacao){

       List<ItemEntity> itens = cotacao.getItens().stream().toList();

       itens.forEach(item -> item.getQuantidade());

       List<CotacaoItemEntity> itemEntities = cotacaoItemRepository.findAll()
               .stream()
               .filter(cotacaoItemEntity -> cotacaoItemEntity.getCotacao().getIdCotacao().equals(cotacao.getIdCotacao()))
               .toList();

       Double valorTotal = 0.0;

       for(int i = 0; i < itens.size(); i++){

          if(itens.get(i).getIdItem().equals(itemEntities.get(i).getItem().getIdItem())){
              valorTotal += itens.get(i).getQuantidade() * itemEntities.get(i).getValorDoItem();
          }

       }

       return valorTotal;

    }

//    public void GestorAprovaOuReprovaCotacao(Integer topicId, Integer quotationId, StatusCotacoes statusCotacoes)
//            throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {
//
//        CotacaoEntity cotacao = findById(topicId);
//
//        UsuarioEntity usuario = usuarioServiceUtil.retornarUsuarioEntityLogado();
//
//        if (cotacao.getStatus().equals(StatusCotacoes.REPROVADO)){
//
//            throw new EntidadeNaoEncontradaException("o Topico não foi aberto");
//        }
//
//        if (usuario.getCotacoes().size() < 2)
//            throw new RegraDeNegocioException("o topico não tem duas cotações");
//        Set<CotacaoEntity> cotacaoEntities = usuario.getCotacoes();
//
//        // seta todas as quotations para reproved
//        cotacaoEntities.forEach(cotacaoEntity -> {
//            cotacaoEntity.setStatus(StatusCotacoes.APROVADO.getSituacaoCompra());
//        });
//
//        //se for para aprovar a cotação a flag sera marcada como true
//        if (statusCotacoes.getSituacaoCompra()) {
//            //então sera atualizada somente a cotação do id passado para aproved
//            quotationEntities.stream()
//                    .filter(quotationEntity -> quotationEntity.getQuotationId().equals(quotationId))
//                    .findFirst().orElseThrow((() -> new BusinessRuleException("Cotação não encontrada")))
//                    .setQuotationStatus(MANAGER_APPROVED);
//            //e por fim o topico será marcado como manager approved
//            topic.setStatus(MANAGER_APPROVED);
//        } else {
//            //se a lfag for false o topico é reprovado
//            topic.setStatus(StatusEnum.MANAGER_REPROVED);
//        }
//        topic.setQuotations(quotationEntities);
//        //topico é salvo
//        this.topicService.save(topic);
//    }

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

    public CotacaoDTO cotacaoAprovada(Integer idCotacao, StatusCotacoes status) throws EntidadeNaoEncontradaException, UsuarioException, RegraDeNegocioException {

        CotacaoEntity cotacao = findById(idCotacao);
        cotacao.setStatus(status.getSituacaoCompra());
        cotacaoRepository.save(cotacao);
        return converterCotacaoEntityToCotacaoDTO(cotacao);

    }


}
