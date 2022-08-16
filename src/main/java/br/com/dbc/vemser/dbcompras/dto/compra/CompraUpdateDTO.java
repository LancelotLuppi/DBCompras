package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import lombok.Data;

import java.util.List;

@Data
public class CompraUpdateDTO {

    private String name;

    private Double valor;

    private List<ItemCreateDTO> itens;

}
