package br.com.dbc.vemser.dbcompras.enums;

public enum StatusUsuario {
    ATIVAR(true),
    DESATIVAR(false);

    private boolean statusUsuario;

    StatusUsuario(boolean status) {
        this.statusUsuario = status;
    }

    public boolean getStatus() {
        return statusUsuario;
    }
}
