package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.compra.CompraWithValorItensDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoDTO;
import br.com.dbc.vemser.dbcompras.dto.cotacao.CotacaoValorItensDTO;
import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CotacaoServiceUtil {
    private final CotacaoRepository cotacaoRepository;
    private final ExceptionUtil exceptionUtil;
    private final ObjectMapper objectMapper;

    public void verificarStatusDaCompraAndCotacao(CompraEntity compra, CotacaoEntity cotacao) throws RegraDeNegocioException {
        exceptionUtil
                .verificarCondicaoException(!compra.getStatus().equals(StatusCompra.COTADO),
                        "Essa compra não está permitida a ser aprovada!");
        exceptionUtil
                .verificarCondicaoException(!cotacao.getStatus().equals(StatusCotacao.EM_ABERTO),
                        "Esta cotação já foi concluída");
        exceptionUtil
                .verificarCondicaoException(compra.getCotacoes().size() < 2,
                        "Este usuario tem menos de duas cotações cadastradas");
    }

    public CotacaoEntity findById(Integer idCotacao) throws EntidadeNaoEncontradaException {
        return cotacaoRepository.findById(idCotacao)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Essa cotação não existe"));
    }

    public CotacaoDTO converterCotacaoToCotacaoDTO(CotacaoEntity cotacao) {
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
