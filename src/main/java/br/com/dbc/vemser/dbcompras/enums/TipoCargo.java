package br.com.dbc.vemser.dbcompras.enums;

import java.util.Arrays;

public enum TipoCargo {

    COLABORADOR(1),
    COMPRADOR(2),
    GESTOR(3),
    FINANCEIRO(4),
    ADMINISTRADOR(5);

    private Integer tipoCargo;

    TipoCargo(Integer tipo){
        this.tipoCargo = tipo;
    }

    public Integer getCargo(){
        return tipoCargo;
    }

    public static TipoCargo ofTipo(Integer tipo){
        return Arrays.stream(TipoCargo.values())
                .filter(tp -> tp.getCargo().equals(tipo))
                .findFirst()
                .get();
    }
}
