package br.com.dbc.vemser.dbcompras.dto.item;

import lombok.Data;


import javax.validation.constraints.*;

@Data
public class ItemCreateDTO {
    @NotBlank
    private String nome;
    @NotNull
    @Min(1)
    private Integer quantidade;
    @NotNull
    private Double preco;
}
