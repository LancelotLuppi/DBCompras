package br.com.dbc.vemser.dbcompras.documentation;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginComSucessoDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserUpdateDTO;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

public interface UsuarioDocumentation {

    @Operation(summary = "Criar novo usuário", description = "$ Os campos de nome, email e senha são obrigatórios. " +
            "$ Nome deve receber o nome completo da pessoa. " +
            "$ Email deve estar com a formatação válida com o padrão de email da DBC (@dbccompany.com.br). " +
            "$ Senha deve ter entre 8 e 16 caracteres, contendo ao menos: um número, uma letra minúscula, uma letra maiúscula e um caractere especial. " +
            "$ A foto de perfil é opcional, deve-se converter o arquivo em Base64 e enviar como String na requisição. " +
            "$ O token de acesso desse novo usuário criado é retornado no DTO. " +
            "$ O tipo de usuário padrão é o COLABORADOR. $")
        @ApiResponse(responseCode = "201", description = "O usuário foi criado com sucesso")
        @ApiResponse(responseCode = "400", description = "Erro de formatação do JSON || Email inválido || Nome do usuário nulo ou vazio || Senha com requisitos insuficientes")
    ResponseEntity<UserLoginComSucessoDTO> create(@RequestBody @Valid UserCreateDTO userCreateDTO) throws RegraDeNegocioException;


    @Operation(summary = "Recuperar dados do usuário logado", description = "Retorna os dados de id, nome, email e foto de perfil " +
            "do usuário que está logado na aplicação.")
        @ApiResponse(responseCode = "200", description = "Retorna os dados com sucesso")
        @ApiResponse(responseCode = "400", description = "Erro de formatação do JSON || Usuário não cadastrado")
    ResponseEntity<UserDTO> getUser() throws UsuarioException;

    @Operation(summary = "Atualizar dados gerais do usuário logado", description = "Atualiza os dados de nome, email e foto " +
            "do usuário que está logado no sistema, pode-se alterar apenas um campo caso necessite")
        @ApiResponse(responseCode = "200", description = "Atualiza os dados com sucesso")
        @ApiResponse(responseCode = "400", description = "Erro de formatação do JSON || Email inválido || Email já cadastrado")
    ResponseEntity<UserDTO> updateLogged(@Valid @RequestBody UserUpdateDTO usuario) throws UsuarioException, RegraDeNegocioException;
}
