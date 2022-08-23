package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.entity.CompraEntity;
import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.entity.ItemEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusCompra;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class CotacaoServiceUtilTest {
    @InjectMocks
    private CotacaoServiceUtil cotacaoServiceUtil;
    private ExceptionUtil exceptionUtil = new ExceptionUtil();

    @Before
    public void init() {
        ReflectionTestUtils.setField(cotacaoServiceUtil, "exceptionUtil", exceptionUtil);
    }

    @Test(expected = RegraDeNegocioException.class)
    public void deveTestarVerificarStatusDaCompraAndCotacao() throws RegraDeNegocioException {
        CompraEntity compra = getCompraEntity();
        CotacaoEntity cotacao = getCotacaoEntity();
        compra.setCotacoes(Set.of(cotacao));

        cotacaoServiceUtil.verificarStatusDaCompraAndCotacao(compra, cotacao);
    }

    private static CompraEntity getCompraEntity() {
        CompraEntity compra = new CompraEntity();

        compra.setIdCompra(10);
        compra.setDataCompra(LocalDateTime.of(1991, 9, 8, 10, 20));
        compra.setStatus(StatusCompra.ABERTO);
        compra.setName("compra");
        compra.setDescricao("descricao");
        compra.setValorTotal(10.0);

        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setIdItem(12);
        itemEntity.setNome("batata");
        itemEntity.setQuantidade(3);

        compra.setItens(Set.of(itemEntity));
        compra.setCotacoes(null);
        return compra;
    }

    public CotacaoEntity getCotacaoEntity() {
        CotacaoEntity cotacao = new CotacaoEntity();
        cotacao.setIdCotacao(10);
        cotacao.setNome("Cotacao");
        cotacao.setValor(30.0);
        return cotacao;
    }
}
