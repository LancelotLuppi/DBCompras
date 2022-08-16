package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.entity.CotacaoEntity;
import br.com.dbc.vemser.dbcompras.enums.SituacaoCompra;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class CompraListDTO {
    private Integer idCompra;
    private String name;
    private LocalDate dataCompra;
    private Double valor;
    private Set<CotacaoEntity> cotacoes;
    private SituacaoCompra status;
}
