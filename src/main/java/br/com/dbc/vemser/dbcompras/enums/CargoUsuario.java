package br.com.dbc.vemser.dbcompras.enums;

import java.util.Arrays;

public enum CargoUsuario {

    ROLES_COLABORADOR(Roles.COLABORADOR),
    ROLES_COMPRADOR(Roles.COMPRADOR),
    ROLES_GESTOR(Roles.GESTOR),
    ROLES_FINANCEIRO(Roles.FINANCEIRO),
    ROLES_ADMINISTRADOR(Roles.ADMINISTRADOR);

    private String tipoCargo;

    CargoUsuario(String tipo){
        tipoCargo = tipo;
    }

    public String getTipoCargo(){
        return tipoCargo;
    }

    public String getRole(){
        return "ROLE_" + tipoCargo;
    }

    public static CargoUsuario ofTipo(String tipoCargo){
        return Arrays.stream(CargoUsuario.values())
                .filter(tp -> tp.getTipoCargo().equals(tipoCargo))
                .findFirst()
                .get();
    }
}
