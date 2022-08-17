package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.*;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.StatusUsuario;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import br.com.dbc.vemser.dbcompras.util.UsuarioUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UsuarioUtil usuarioUtil;


    public UserLoginComSucessoDTO create(UserCreateDTO login) throws RegraDeNegocioException {
        usuarioUtil.validarEmail(login.getEmail());
        usuarioUtil.verificarSeEmailTemCadastro(login.getEmail());
        usuarioUtil.validarSenha(login.getSenha());

        UsuarioEntity usuarioEntity = usuarioUtil.retornarUsuarioEntity(login);

        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(usuarioUtil.encodePassword(login.getSenha()));
        usuarioEntity.setPhoto(login.getFoto()!=null ? Base64.getDecoder().decode(login.getFoto()) : null);
        usuarioEntity.setEnable(StatusUsuario.ATIVAR.getStatus());

        usuarioEntity = usuarioRepository.save(usuarioEntity);

        return usuarioUtil.generateUserLoginComSucessoDTO(usuarioEntity, login.getEmail(), login.getSenha());
    }

    public UserDTO updateLoggedUser(UserUpdateDTO usuarioUpdate) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioUtil.retornarUsuarioEntityLogado();
        if(usuarioUpdate.getEmail() != null) {
            usuarioUtil.validarEmail(usuarioUpdate.getEmail());
            usuarioUtil.verificarSeEmailTemCadastro(usuarioUpdate.getEmail());
            usuarioEntity.setEmail(usuarioUpdate.getEmail());
        }
        if(usuarioUpdate.getNome() != null) {
            usuarioEntity.setNome(usuarioUpdate.getNome());
        }
        if(usuarioUpdate.getSenha() != null) {
            usuarioUtil.validarSenha(usuarioUpdate.getSenha());
            usuarioEntity.setPassword(usuarioUtil.encodePassword(usuarioEntity.getPassword()));
        }
        if(usuarioUpdate.getFoto() != null) {
            usuarioEntity.setPhoto(usuarioUpdate.getFoto()!=null ? Base64.getDecoder().decode(usuarioUpdate.getFoto()) : null);
        }
        UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioEntity);
        return usuarioUtil.retornarUsuarioDTO(usuarioAtualizado);
    }

    public void desativarContaLogada(UserLoginDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioUtil.retornarUsuarioEntityLogado();

        boolean verificacao = usuarioEntity.getEmail().equals(confirmacao.getEmail())
                && new Argon2PasswordEncoder().matches(confirmacao.getPassword(), usuarioEntity.getPassword());

        if(!verificacao) {
            throw new RegraDeNegocioException("Usuario ou senha inválidos");
        }
        usuarioEntity.setEnable(false);
        usuarioRepository.save(usuarioEntity);
    }

    public void deletarUsuario(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioUtil.findById(idUsuario);
        usuarioRepository.delete(usuarioEntity);
    }

    public UserWithProfileImageDTO getLoggedUser() throws UsuarioException {
        UsuarioEntity usuarioEntity = usuarioUtil.retornarUsuarioEntityLogado();
        UserWithProfileImageDTO userWithProfileImageDTO = objectMapper.convertValue(usuarioEntity, UserWithProfileImageDTO.class);
        
        byte[] byteFoto = usuarioEntity.getPhoto();
        userWithProfileImageDTO.setImagemPerfilB64(usuarioEntity.getPhoto()!=null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : null);

        return userWithProfileImageDTO;
    }

    public String validarLogin(UserLoginDTO login) {
        return usuarioUtil.recuperarToken(login.getEmail(), login.getPassword());
    }
}
