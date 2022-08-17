package br.com.dbc.vemser.dbcompras.documentation;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginComSucessoDTO;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface UsuarioDocumentation {

    @Operation(summary = "Criar novo usuário", description = "Os campos de nome, email e senha são obrigatórios. " +
            "Nome deve receber o nome completo da pessoa. " +
            "Email deve estar com a formatação válida com o padrão de email da DBC (@dbccompany.com.br). " +
            "Senha deve ter entre 8 e 16 caracteres, contendo ao menos: um número, uma letra minúscula, uma letra maiúscula e um caractere especial. " +
            "A foto de perfil é opcional, deve-se converter o arquivo em Base64 e enviar como String na requisição. " +
            "O token de acesso desse novo usuário criado é retornado no DTO. " +
            "O tipo de usuário padrão é o COLABORADOR. ")
        @ApiResponse(responseCode = "201", description = "O usuário foi criado com sucesso")
        @ApiResponse(responseCode = "400", description = "Erro de formatação do JSON")
        @ApiResponse(responseCode = "400", description = "Email inválido")
        @ApiResponse(responseCode = "400", description = "Nome do usuário nulo ou vazio")
        @ApiResponse(responseCode = "400", description = "Senha com requisitos insuficientes")
    ResponseEntity<UserLoginComSucessoDTO> create(@RequestBody @Valid UserCreateDTO userCreateDTO) throws RegraDeNegocioException;
}
