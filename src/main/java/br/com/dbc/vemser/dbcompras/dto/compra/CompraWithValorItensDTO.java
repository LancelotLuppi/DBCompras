package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemValorizadoDTO;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CompraWithValorItensDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private LocalDate dataCompra;
    private Double valorTotal;
    private List<ItemValorizadoDTO> itens;
    private String status;
}
