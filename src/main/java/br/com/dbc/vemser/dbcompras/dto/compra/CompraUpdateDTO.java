package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CompraUpdateDTO {
    @NotBlank
    private String name;
    private List<ItemCreateDTO> itens;
}
