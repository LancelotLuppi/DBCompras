package br.com.dbc.vemser.dbcompras.dto.compra;

import br.com.dbc.vemser.dbcompras.dto.item.ItemCreateDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class CompraCreateDTO {
    @NotBlank
    @NotNull
    private String name;
    @NotBlank
    @NotNull
    private String descricao;
    @Valid
    private List<ItemCreateDTO> itens;
}
