package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.LoginDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.LoginReturnDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UsuarioDTO;
import br.com.dbc.vemser.dbcompras.dto.usuario.UsuarioUpdateDTO;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
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



    public Optional<UsuarioEntity> findByEmail(String email){
        return usuarioRepository.findByEmail(email);
    }

    public UsuarioDTO findById()
            throws UsuarioException {
        return retornarUsuarioDTO(retornarUsuarioEntityById());
    }

    public LoginReturnDTO create(LoginDTO login) {
        UsuarioEntity usuarioEntity = retornarUsuarioEntity(login);
        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(encodePassword(login.getPassword()));
        usuarioEntity.setEnable(true);
        usuarioEntity = usuarioRepository.save(usuarioEntity);



        return objectMapper.convertValue(usuarioEntity, LoginReturnDTO.class);
    }



    public String validarLogin(LoginDTO login) {
        return recuperarToken(login.getEmail(), login.getPassword());
    }

    public LoginDTO updatePassword (LoginDTO usuario)
            throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityById();

        usuarioEntity.setPassword(encodePassword(usuario.getPassword()));

        usuarioRepository.save(usuarioEntity);
        return objectMapper.convertValue(usuarioEntity, LoginDTO.class);

    }

    public UsuarioDTO update(UsuarioUpdateDTO usuarioUpdate) throws UsuarioException {

        UsuarioEntity usuarioEntity = retornarUsuarioEntityById();

        usuarioEntity.setNome(usuarioUpdate.getNome());
        usuarioEntity.setEmail(usuarioUpdate.getEmail());
        usuarioEntity = usuarioRepository.save(usuarioEntity);
        return retornarUsuarioDTO(usuarioEntity);
    }

    public void delete()
            throws UsuarioException {
        UsuarioEntity usuario = retornarUsuarioEntityById();
        usuarioRepository.delete(usuario);
    }

    public LoginReturnDTO getLoggedUser()
            throws UsuarioException {
        return objectMapper.convertValue(retornarUsuarioEntityById(), LoginReturnDTO.class);
    }

    public UsuarioEntity retornarUsuarioEntityById()
            throws UsuarioException {
        return usuarioRepository
                .findById(getIdLoggedUser())
                .orElseThrow(() -> new UsuarioException("Usuário não cadastrado"));
    }

    private Integer getIdLoggedUser() throws UsuarioException {
        Integer idUser;
        try {
            idUser =  (Integer) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } catch (Exception e){
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
        Object usuarioLogado =  authentication.getPrincipal();
        UsuarioEntity usuarioEntityLogado = (UsuarioEntity) usuarioLogado;

        return tokenService.generateToken(usuarioEntityLogado);
    }

    private UsuarioEntity retornarUsuarioEntity (LoginDTO loginDTO) {
        return objectMapper.convertValue(loginDTO, UsuarioEntity.class);
    }

    private UsuarioDTO retornarUsuarioDTO (UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }

    private String encodePassword(String password){
        return new Argon2PasswordEncoder().encode(password);
    }

}
