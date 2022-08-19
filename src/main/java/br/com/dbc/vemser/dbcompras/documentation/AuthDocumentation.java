package br.com.dbc.vemser.dbcompras.documentation;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginDTO;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface AuthDocumentation {

    @Operation(summary = "Realizar o login do usuário", description = "Recebe o email e a senha do usuário, retorna " +
            "uma string com o token de acesso para a aplicação caso o login esteja correto.")
        @ApiResponse(responseCode = "200", description = "Retorna o token de acesso")
        @ApiResponse(responseCode = "400", description = "Email ou senha inválido")
    ResponseEntity<String> auth(@Valid @RequestBody UserLoginDTO login) throws RegraDeNegocioException;
}
