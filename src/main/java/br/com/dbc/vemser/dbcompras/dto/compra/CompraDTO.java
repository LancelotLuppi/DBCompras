package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import lombok.Data;

import java.util.List;

@Data
public class CompraDTO {
    private Integer idCompra;
    private String name;
    private String descricao;
    private List<ItemDTO> itens;
}
