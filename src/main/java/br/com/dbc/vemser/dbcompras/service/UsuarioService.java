package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.LoginCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginAccessDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusUsuario;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
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
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ObjectMapper objectMapper;
    private final CargoRepository cargoRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;


    public LoginDTO create(LoginCreateDTO login) throws RegraDeNegocioException {

        if(usuarioRepository.findByEmail(login.getEmail()).isPresent()) {
            throw new RegraDeNegocioException("Este email já está cadastrado");
        }

        UsuarioEntity usuarioEntity = retornarUsuarioEntity(login);
        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(encodePassword(login.getPassword()));
        usuarioEntity.setPhoto(login.getImagemPerfilB64()!=null ? Base64.getDecoder().decode(login.getImagemPerfilB64()) : null);
        usuarioEntity.setEnable(StatusUsuario.ATIVAR.getStatus());
        usuarioEntity = usuarioRepository.save(usuarioEntity);

        return objectMapper.convertValue(usuarioEntity, LoginDTO.class);

    }

    public String validarLogin(LoginAccessDTO login) {
        return recuperarToken(login.getEmail(), login.getPassword());
    }

    public LoginDTO getLoggedUser()
            throws UsuarioException {
        return objectMapper.convertValue(retornarUsuarioEntityLogado(), LoginDTO.class);
    }

    public LoginDTO updatePassword(String novaSenha) throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        usuarioEntity.setPassword(encodePassword(novaSenha));
        usuarioRepository.save(usuarioEntity);

        return objectMapper.convertValue(usuarioEntity, LoginDTO.class);
    }

    public LoginDTO updateLoggedUser(LoginUpdateDTO usuarioUpdate) throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        usuarioEntity.setNome(usuarioUpdate.getNome());
        usuarioEntity.setEmail(usuarioUpdate.getEmail());
        UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioEntity);

        return retornarUsuarioDTO(usuarioAtualizado);
    }

    public void desativarContaLogada(LoginAccessDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        boolean verificacao = usuarioEntity.getEmail().equals(confirmacao.getEmail())
                && new Argon2PasswordEncoder().matches(confirmacao.getPassword(), usuarioEntity.getPassword());

        if(!verificacao) {
            throw new RegraDeNegocioException("Usuario ou senha invalidos");
        }

        usuarioEntity.setEnable(false);
        usuarioRepository.save(usuarioEntity);
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

    private String recuperarToken(String email, String senha) {
        UsernamePasswordAuthenticationToken userPassAuthToken =
                new UsernamePasswordAuthenticationToken(
                        email,
                        senha);

        Authentication authentication = authenticationManager.authenticate(userPassAuthToken);
        Object usuarioLogado = authentication.getPrincipal();
        UsuarioEntity usuarioEntityLogado = (UsuarioEntity) usuarioLogado;

        return tokenService.generateToken(usuarioEntityLogado);
    }

    private UsuarioEntity retornarUsuarioEntity(LoginCreateDTO loginCreateDTO) {
        return objectMapper.convertValue(loginCreateDTO, UsuarioEntity.class);
    }

    private LoginDTO retornarUsuarioDTO(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, LoginDTO.class);
    }

    private String encodePassword(String password) {
        return new Argon2PasswordEncoder().encode(password);
    }

}
