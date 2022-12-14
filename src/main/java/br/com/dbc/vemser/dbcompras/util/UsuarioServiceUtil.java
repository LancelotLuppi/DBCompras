package br.com.dbc.vemser.dbcompras.util;

import br.com.dbc.vemser.dbcompras.dto.cargo.CargoDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserLoginComSucessoDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UserWithCargoDTO;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import br.com.dbc.vemser.dbcompras.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Set;


@Component
@RequiredArgsConstructor
public class UsuarioServiceUtil {
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final ObjectMapper objectMapper;

    private final Argon2PasswordEncoder passwordEncoder;


    public UsuarioEntity findById(Integer idUsuario) throws RegraDeNegocioException {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    }

    public UsuarioEntity retornarUsuarioEntityLogado()
            throws UsuarioException, RegraDeNegocioException {
        return findById(getIdLoggedUser());
    }

    public Integer getIdLoggedUser() throws UsuarioException {
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
        if (emailParaValidar.matches("^(.+)@dbccompany.com.br")) {
            // FIXME Ajeitar esta lógica
        } else {
            throw new RegraDeNegocioException("Insira um email DBC válido");
        }
    }

    public void verificarSeEmailTemCadastro(String email) throws RegraDeNegocioException {
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new RegraDeNegocioException("Email já está possui cadastrado");
        }
    }

    public void validarFormatacaoSenha(String senhaParaValidar) throws RegraDeNegocioException {
        if (senhaParaValidar.matches("^(?=.*[A-Z])(?=.*[.!@$%^&(){}:;<>,?/~_+-=|])(?=.*[0-9])(?=.*[a-z]).{8,16}$")) {
            // FIXME Ajeitar esta lógica
        } else {
            throw new RegraDeNegocioException("A senha deve ter entre 8 e 16 caracteres, com letras, números e caracteres especiais");
        }
    }

    public boolean verificarSenhaUsuario(String senha, UsuarioEntity usuario) {
        return passwordEncoder.matches(senha, usuario.getPassword());
    }

    public UserWithCargoDTO retornarUsuarioDTOComCargo(UsuarioEntity usuario) {
        Set<CargoEntity> cargos = usuario.getCargos();
        List<CargoDTO> cargoDTOS = cargos.stream()
                .map(cargo -> objectMapper.convertValue(cargo, CargoDTO.class))
                .toList();
        UserWithCargoDTO user = objectMapper.convertValue(usuario, UserWithCargoDTO.class);
        byte[] byteFoto = usuario.getPhoto();
        user.setImagemPerfilB64(usuario.getPhoto() != null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : null);
        user.setCargos(cargoDTOS);
        return user;
    }

    public UsuarioEntity retornarUsuarioEntity(UserCreateDTO userCreateDTO) {
        return objectMapper.convertValue(userCreateDTO, UsuarioEntity.class);
    }

    public UserDTO retornarUsuarioDTO(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, UserDTO.class);
    }

    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

    public UserLoginComSucessoDTO generateUserLoginComSucessoDTO(UsuarioEntity usuarioEntity, String email, String senha) {

        UserLoginComSucessoDTO userLoginComSucessoDTO = new UserLoginComSucessoDTO();
        userLoginComSucessoDTO.setIdUser(usuarioEntity.getIdUser());
        userLoginComSucessoDTO.setNome(usuarioEntity.getNome());
        userLoginComSucessoDTO.setEmail(usuarioEntity.getEmail());
        userLoginComSucessoDTO.setToken(recuperarToken(email, senha));

        byte[] byteFoto = usuarioEntity.getPhoto();

        userLoginComSucessoDTO.setImagemPerfilB64(usuarioEntity.getPhoto() != null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : Optional.empty());

        return userLoginComSucessoDTO;
    }
}
