package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import lombok.Data;

import java.util.List;

@Data
public class CompraCreateDTO {
    private String name;
    private String descricao;
    private List<ItemCreateDTO> itens;
}
