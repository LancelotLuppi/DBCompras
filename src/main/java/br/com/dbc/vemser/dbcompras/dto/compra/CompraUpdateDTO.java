package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.item.ItemUpdateDTO;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompraUpdateDTO {
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String descricao;
    @NotNull
    private List<ItemUpdateDTO> itens;
}
