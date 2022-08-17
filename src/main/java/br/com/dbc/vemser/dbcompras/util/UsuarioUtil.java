package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.usuario.UserCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginComSucessoDTO;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import br.com.dbc.vemser.dbcompras.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class UsuarioUtil {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;


    public UsuarioEntity findById(Integer idUsuario) throws RegraDeNegocioException {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    }

    public UsuarioEntity retornarUsuarioEntityLogado()
            throws UsuarioException {
        return usuarioRepository
                .findById(getIdLoggedUser())
                .orElseThrow(() -> new UsuarioException("Usuário não cadastrado"));
    }

    private Integer getIdLoggedUser() throws UsuarioException {
        Integer idUser;
        try {
            idUser = (Integer) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } catch (Exception e) {
            throw new UsuarioException("Usuário não logado");
        }
        return idUser;
    }

    public String recuperarToken(String email, String senha) {
        UsernamePasswordAuthenticationToken userPassAuthToken =
                new UsernamePasswordAuthenticationToken(
                        email,
                        senha);

        Authentication authentication = authenticationManager.authenticate(userPassAuthToken);
        Object usuarioLogado = authentication.getPrincipal();
        UsuarioEntity usuarioEntityLogado = (UsuarioEntity) usuarioLogado;

        return tokenService.generateToken(usuarioEntityLogado);
    }

    public void validarEmail(String emailParaValidar) throws RegraDeNegocioException {
        if(emailParaValidar.matches("^(.+)@dbccompany.com.br")){
            log.info("Email validado");
        } else {
            throw new RegraDeNegocioException("Insira um email DBC válido");
        }
    }

    public void verificarSeEmailTemCadastro(String email) throws RegraDeNegocioException {
        if(usuarioRepository.findByEmail(email).isPresent()){
            throw new RegraDeNegocioException("Email já está possui cadastrado");
        }
    }

    public void validarSenha(String senhaParaValidar) throws RegraDeNegocioException {
        if(senhaParaValidar.matches("^(?=.*[A-Z])(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,16}$")){
            log.info("Senha válida");
        } else {
            throw new RegraDeNegocioException("A senha deve ter entre 8 e 16 caracteres, com letras, números e caracteres especiais");
        }
    }

    public UsuarioEntity retornarUsuarioEntity(UserCreateDTO userCreateDTO) {
        return objectMapper.convertValue(userCreateDTO, UsuarioEntity.class);
    }

    public UserDTO retornarUsuarioDTO(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, UserDTO.class);
    }

    public String encodePassword(String password) {
        return new Argon2PasswordEncoder().encode(password);
    }

    public UserLoginComSucessoDTO generateUserLoginComSucessoDTO(UsuarioEntity usuarioEntity, String email, String senha){

        UserLoginComSucessoDTO userLoginComSucessoDTO = new UserLoginComSucessoDTO();
        userLoginComSucessoDTO.setIdUser(usuarioEntity.getIdUser());
        userLoginComSucessoDTO.setNome(usuarioEntity.getNome());
        userLoginComSucessoDTO.setEmail(usuarioEntity.getEmail());
        userLoginComSucessoDTO.setToken(recuperarToken(email, senha));

        byte[] byteFoto = usuarioEntity.getPhoto();

        userLoginComSucessoDTO.setImagemPerfilB64(usuarioEntity.getPhoto()!=null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : Optional.empty());

        return userLoginComSucessoDTO;
    }
}
