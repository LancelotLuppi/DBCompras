package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.usuario.*;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.ControlarAcesso;
import br.com.dbc.vemser.dbcompras.enums.StatusUsuario;
import br.com.dbc.vemser.dbcompras.enums.TipoCargo;
import br.com.dbc.vemser.dbcompras.exception.RegraDeNegocioException;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import br.com.dbc.vemser.dbcompras.util.UsuarioServiceUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final CargoRepository cargoRepository;
    private final UsuarioServiceUtil usuarioServiceUtil;
    private final ObjectMapper objectMapper;
    private final Argon2PasswordEncoder passwordEncoder;


    public UserLoginComSucessoDTO create(UserCreateDTO login) throws RegraDeNegocioException {
        usuarioServiceUtil.validarEmail(login.getEmail());
        usuarioServiceUtil.verificarSeEmailTemCadastro(login.getEmail());
        usuarioServiceUtil.validarFormatacaoSenha(login.getSenha());

        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntity(login);

        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(usuarioServiceUtil.encodePassword(login.getSenha()));
        usuarioEntity.setPhoto(login.getFoto() != null ? Base64.getDecoder().decode(login.getFoto()) : null);
        usuarioEntity.setEnable(StatusUsuario.ATIVAR.getStatus());

        usuarioEntity = usuarioRepository.save(usuarioEntity);

        return usuarioServiceUtil.generateUserLoginComSucessoDTO(usuarioEntity, login.getEmail(), login.getSenha());
    }

    public UserWithCargoDTO updateUserByAdmin(Integer idUsuario, Set<TipoCargo> tipoCargos) throws RegraDeNegocioException {

        if(tipoCargos.contains(TipoCargo.ADMINISTRADOR)) {
            tipoCargos.clear();
            tipoCargos.add(TipoCargo.COLABORADOR);
            tipoCargos.add(TipoCargo.COMPRADOR);
            tipoCargos.add(TipoCargo.FINANCEIRO);
            tipoCargos.add(TipoCargo.GESTOR);
            tipoCargos.add(TipoCargo.ADMINISTRADOR);
        }

        Set<CargoEntity> cargosUsuario = new HashSet<>();
        UsuarioEntity usuarioEntity = usuarioServiceUtil.findById(idUsuario);
        cargosUsuario.addAll(tipoCargos.stream()
                .map(cargos -> cargoRepository.findById(cargos.getCargo()).get())
                .toList());
        usuarioEntity.setCargos(cargosUsuario);
        usuarioEntity.setIdUser(idUsuario);
        usuarioRepository.save(usuarioEntity);

        return usuarioServiceUtil.retornarUsuarioDTOComCargo(usuarioEntity);
    }

    public UserDTO updateLoggedUser(UserUpdateDTO usuarioUpdate) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntityLogado();
        if (usuarioUpdate.getEmail() != null) {
            usuarioServiceUtil.validarEmail(usuarioUpdate.getEmail());
            if (!usuarioUpdate.getEmail().equals(usuarioEntity.getEmail())) {
                usuarioServiceUtil.verificarSeEmailTemCadastro(usuarioUpdate.getEmail());
                usuarioEntity.setEmail(usuarioUpdate.getEmail());
            }
        }
        if (usuarioUpdate.getNome() != null) {
            usuarioEntity.setNome(usuarioUpdate.getNome());
        }
        if (usuarioUpdate.getFoto() != null) {
            usuarioEntity.setPhoto(usuarioUpdate.getFoto() != null ? Base64.getDecoder().decode(usuarioUpdate.getFoto()) : null);
        }
        UsuarioEntity usuarioAtualizado = usuarioRepository.save(usuarioEntity);
        return usuarioServiceUtil.retornarUsuarioDTO(usuarioAtualizado);
    }

    public void desativarContaLogada(UserLoginDTO confirmacao) throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntityLogado();

        boolean verificacao = usuarioEntity.getEmail().equals(confirmacao.getEmail())
                && passwordEncoder.matches(confirmacao.getPassword(), usuarioEntity.getPassword());

        if (!verificacao) {
            throw new RegraDeNegocioException("Usuario ou senha inválidos");
        }
        usuarioEntity.setEnable(false);
        usuarioRepository.save(usuarioEntity);
    }

    public void deletarUsuario(Integer idUsuario) throws RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioServiceUtil.findById(idUsuario);
        usuarioRepository.delete(usuarioEntity);
    }

    public UserWithCargoDTO createUserByAdmin(UserCreateDTO userCreateDTO, Set<TipoCargo> tipoCargo) throws
            RegraDeNegocioException {
        usuarioServiceUtil.validarEmail(userCreateDTO.getEmail());
        usuarioServiceUtil.verificarSeEmailTemCadastro(userCreateDTO.getEmail());
        usuarioServiceUtil.validarFormatacaoSenha(userCreateDTO.getSenha());
        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntity(userCreateDTO);


        usuarioEntity.setCargos(Set.of(cargoRepository.findById(TipoCargo.COLABORADOR.getCargo()).get()));
        usuarioEntity.setPassword(usuarioServiceUtil.encodePassword(userCreateDTO.getSenha()));
        usuarioEntity.setPhoto(userCreateDTO.getFoto() != null ? Base64.getDecoder().decode(userCreateDTO.getFoto()) : null);
        usuarioEntity.setEnable(StatusUsuario.ATIVAR.getStatus());

        usuarioEntity = usuarioRepository.save(usuarioEntity);
        updateUserByAdmin(usuarioEntity.getIdUser(), tipoCargo);
        return usuarioServiceUtil.retornarUsuarioDTOComCargo(usuarioEntity);

    }

    public UserWithCargoDTO getLoggedUser() throws UsuarioException, RegraDeNegocioException {
        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntityLogado();
        UserWithCargoDTO userWithProfileImageDTO = usuarioServiceUtil.retornarUsuarioDTOComCargo(usuarioEntity);

        byte[] byteFoto = usuarioEntity.getPhoto();
        userWithProfileImageDTO.setImagemPerfilB64(usuarioEntity.getPhoto() != null ? Optional.of(Base64.getEncoder().encodeToString(byteFoto)) : null);

        return userWithProfileImageDTO;
    }

    public String validarLogin(UserLoginDTO login) throws RegraDeNegocioException {
        try {
            return usuarioServiceUtil.recuperarToken(login.getEmail(), login.getPassword());
        } catch (BadCredentialsException ex) {
            ex.printStackTrace();
            throw new RegraDeNegocioException("Usuário ou senha inválidos");
        }
    }

    public List<UserWithCargoDTO> list() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioServiceUtil::retornarUsuarioDTOComCargo)
                .collect(Collectors.toList());
    }

    public void controlarAcessoUsuario(Integer idUsuario, ControlarAcesso controlarAcesso)
            throws RegraDeNegocioException {
        boolean habilitado = controlarAcesso.getEnable() == 1;
        UsuarioEntity usuarioEntity = usuarioServiceUtil.findById(idUsuario);
        usuarioEntity.setEnable(habilitado);
        usuarioRepository.save(usuarioEntity);
    }

    public void updateLoggedPassword(UserUpdatePasswordDTO userUpdatePasswordDTO) throws RegraDeNegocioException, UsuarioException {
        UsuarioEntity usuarioEntity = usuarioServiceUtil.retornarUsuarioEntityLogado();

        if (usuarioServiceUtil.verificarSenhaUsuario(userUpdatePasswordDTO.getSenhaAtual(), usuarioEntity)) {

            usuarioServiceUtil.validarFormatacaoSenha(userUpdatePasswordDTO.getNovaSenha());
            usuarioEntity.setPassword(usuarioServiceUtil.encodePassword(userUpdatePasswordDTO.getNovaSenha()));
            usuarioRepository.save(usuarioEntity);
            usuarioServiceUtil.retornarUsuarioDTO(usuarioEntity);

        } else {
            throw new RegraDeNegocioException("Senha inválida");
        }

    }
}

