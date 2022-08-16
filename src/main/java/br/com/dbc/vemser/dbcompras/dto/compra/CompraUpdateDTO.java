package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemDTO;
import br.com.dbc.vemser.dbcompras.enums.StatusCotacoes;
import lombok.Data;

import java.util.List;

@Data
public class CompraUpdateDTO {

    private String name;

    private StatusCotacoes status;

    private Double valor;

    private List<ItemDTO> itens;

}
