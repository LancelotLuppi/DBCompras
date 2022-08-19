package br.com.dbc.vemser.dbcompras.dto.item;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ItemCreateDTO {
    @NotBlank
    private String nome;
    @NotNull
    @Min(1)
    private Integer quantidade;
}
