package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacao;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CotacaoServiceUtil {

    public void verificarStatusDaCompraAndCotacao(CompraEntity compra, CotacaoEntity cotacao) throws RegraDeNegocioException {
        if(!compra.getStatus().equals(StatusCompra.COTADO)) {
            throw new RegraDeNegocioException("Essa compra não está permitida a ser aprovada!");
        }
        if (!cotacao.getStatus().equals(StatusCotacao.EM_ABERTO)) {
            throw new RegraDeNegocioException("Esta cotação já foi aprovada");
        }
        if (compra.getCotacoes().size() < 2) {
            throw new RegraDeNegocioException("Este usuario tem menos de duas cotações cadastradas");
        }
    }
}
