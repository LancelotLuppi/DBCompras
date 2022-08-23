package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import org.springframework.stereotype.Component;

@Component
public class ExceptionUtil {
    public void verificarCondicaoException(boolean condicao, String message) throws RegraDeNegocioException {
        if (condicao) {
            throw new RegraDeNegocioException(message);
        }
    }
}
