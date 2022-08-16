package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.*;
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
import java.util.Optional;
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

    public UserLoginComSucessoDTO create(UserCreateDTO login) throws RegraDeNegocioException {
        validarEmail(login.getEmail());
        verificarSeEmailExiste(login.getEmail());
        validarSenha(login.getSenha());

        UsuarioEntity usuarioEntity = retornarUsuarioEntity(login);

        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(encodePassword(login.getSenha()));
        usuarioEntity.setPhoto(login.getFoto()!=null ? Base64.getDecoder().decode(login.getFoto()) : null);
        usuarioEntity.setEnable(StatusUsuario.ATIVAR.getStatus());

        usuarioEntity = usuarioRepository.save(usuarioEntity);

        return createToUserLoginComSucessoDTO(usuarioEntity, login.getEmail(), login.getSenha());
    }

    public UserDTO updatePassword(String novaSenha) throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        usuarioEntity.setPassword(encodePassword(novaSenha));
        usuarioRepository.save(usuarioEntity);

        return objectMapper.convertValue(usuarioEntity, UserDTO.class);
    }

    public UserDTO updateLoggedUser(UserUpdateDTO usuarioUpdate) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        if(usuarioUpdate.getEmail() != null) {
            validarEmail(usuarioUpdate.getEmail());
            verificarSeEmailExiste(usuarioUpdate.getEmail());
            usuarioEntity.setEmail(usuarioUpdate.getEmail());
        }
        if(usuarioUpdate.getNome() != null) {
            usuarioEntity.setNome(usuarioUpdate.getNome());
        }
        if(usuarioUpdate.getSenha() != null) {
            usuarioEntity.setPassword(usuarioUpdate.getSenha());
        }
        if(usuarioUpdate.getFoto() != null) {
            usuarioEntity.setPhoto(usuarioUpdate.getFoto()!=null ? Base64.getDecoder().decode(usuarioUpdate.getFoto()) : null);
        }

        UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioEntity);

        return retornarUsuarioDTO(usuarioAtualizado);
    }

    public void desativarContaLogada(UserLoginDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();

        boolean verificacao = usuarioEntity.getEmail().equals(confirmacao.getEmail())
                && new Argon2PasswordEncoder().matches(confirmacao.getPassword(), usuarioEntity.getPassword());

        if(!verificacao) {
            throw new RegraDeNegocioException("Usuario ou senha inválidos");
        }

        usuarioEntity.setEnable(false);
        usuarioRepository.save(usuarioEntity);
    }

    public void deletarUsuario(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = findById(idUsuario);

        usuarioRepository.delete(usuarioEntity);
    }

    public Optional<UsuarioEntity> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public UserWithProfileImageDTO getLoggedUser()
            throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityLogado();
        UserWithProfileImageDTO userWithProfileImageDTO = objectMapper.convertValue(usuarioEntity, UserWithProfileImageDTO.class);
        
        byte[] byteFoto = usuarioEntity.getPhoto();
        userWithProfileImageDTO.setImagemPerfilB64(usuarioEntity.getPhoto()!=null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : null);

        return userWithProfileImageDTO;
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

    public String validarLogin(UserLoginDTO login) {
        return recuperarToken(login.getEmail(), login.getPassword());
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

    private void verificarSeEmailExiste(String email) throws RegraDeNegocioException {
        if(findByEmail(email).isPresent()){
            throw new RegraDeNegocioException("Email já está possui cadastrado");
        }
    }

    private void validarEmail(String emailParaValidar) throws RegraDeNegocioException {
        if(emailParaValidar.matches("^(.+)@dbccompany.com.br")){
            log.info("Email validado");
        } else {
            throw new RegraDeNegocioException("Insira um email DBC válido");
        }
    }

    private void validarSenha(String senhaParaValidar) throws RegraDeNegocioException {
        if(senhaParaValidar.matches("^(?=.*[A-Z])(?=.*[!#@$%&])(?=.*[0-9])(?=.*[a-z]).{8,16}$")){
            log.info("Senha válida");
        } else {
            throw new RegraDeNegocioException("Formato de senha inválido.");
        }
    }

    private UserLoginComSucessoDTO createToUserLoginComSucessoDTO(UsuarioEntity usuarioEntity, String email, String senha){

        UserLoginComSucessoDTO userLoginComSucessoDTO = new UserLoginComSucessoDTO();
        userLoginComSucessoDTO.setIdUser(usuarioEntity.getIdUser());
        userLoginComSucessoDTO.setNome(usuarioEntity.getNome());
        userLoginComSucessoDTO.setEmail(usuarioEntity.getEmail());
        userLoginComSucessoDTO.setToken(recuperarToken(email, senha));

        byte[] byteFoto = usuarioEntity.getPhoto();

        userLoginComSucessoDTO.setImagemPerfilB64(usuarioEntity.getPhoto()!=null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : null);

        return userLoginComSucessoDTO;
    }

    public UsuarioEntity findById(Integer idUsuario) throws RegraDeNegocioException {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    }

    private UsuarioEntity retornarUsuarioEntity(UserCreateDTO userCreateDTO) {
        return objectMapper.convertValue(userCreateDTO, UsuarioEntity.class);
    }

    private UserDTO retornarUsuarioDTO(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, UserDTO.class);
    }

    private String encodePassword(String password) {
        return new Argon2PasswordEncoder().encode(password);
    }

}
