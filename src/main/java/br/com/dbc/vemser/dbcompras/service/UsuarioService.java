package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.*;
import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.enums.CargoUsuario;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

    public UsuarioEntity converterUsuarioEntity(UsuarioCreateDTO usuarioCreateDTO) {
        return objectMapper.convertValue(usuarioCreateDTO, UsuarioEntity.class);
    }

    public UsuarioDTO converterUsuarioDTO(UsuarioEntity usuario) {
        return objectMapper.convertValue(usuario, UsuarioDTO.class);
    }

    private String encodePassword(String password){
        return new BCryptPasswordEncoder().encode(password);
    }


    public Optional<UsuarioEntity> findByUsername(String username) {
        return null;
    }

    public Optional<UsuarioEntity> findByLogin(String login){
        return usuarioRepository.findByLogin(login);
    }

    public Integer getIdLoggedUser() throws UsuarioException {
        Integer idUser;
        try {
            idUser =  (Integer) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal();
        } catch (Exception e){
            throw new UsuarioException("Usuário não logado");
        }
        return idUser;
    }

    public UsuarioLoginDTO getLoggedUser()
            throws UsuarioException {
        return objectMapper.convertValue(retornarUsuarioEntityById(), UsuarioLoginDTO.class);
    }

    public UsuarioEntity retornarUsuarioEntityById()
            throws UsuarioException {
        return usuarioRepository
                .findById(getIdLoggedUser())
                .orElseThrow(() -> new UsuarioException("Usuário não cadastrado"));
    }

    public UsuarioDTO findById()
            throws UsuarioException {
        return converterUsuarioDTO(retornarUsuarioEntityById());
    }

    public UsuarioDTO create(UsuarioCreateDTO usuario, CargoUsuario cargo) throws JsonProcessingException {
        UsuarioEntity usuarioEntity = converterUsuarioEntity(usuario);
        Optional<CargoEntity> cargoEntity = cargoRepository.findById(cargo.ordinal());
        usuarioEntity.setCargos(Set.of(cargoEntity.get()));
        usuarioEntity.setPassword(encodePassword(usuario.getSenha()));
        usuarioEntity.setEnable(true);
        usuarioEntity = usuarioRepository.save(usuarioEntity);
        UsuarioDTO usuarioDTO = converterUsuarioDTO(usuarioEntity);
        return usuarioDTO;
    }

    public UsuarioUpdateLoginDTO updateLogin (UsuarioUpdateLoginDTO usuario)
            throws UsuarioException {
        UsuarioEntity usuarioEntity = retornarUsuarioEntityById();

        if(usuario.getLogin() != null){
            usuarioEntity.setLogin(usuario.getLogin());
        }

        if(usuario.getSenha() != null){
            usuarioEntity.setPassword(encodePassword(usuario.getSenha()));
        }

        usuarioRepository.save(usuarioEntity);
        return objectMapper.convertValue(usuarioEntity, UsuarioUpdateLoginDTO.class);

    }

    public UsuarioDTO update(UsuarioUpdateDTO usuarioUpdate, CargoUsuario cargo)
            throws JsonProcessingException, UsuarioException {

        UsuarioEntity usuarioEntity = retornarUsuarioEntityById();

        if(usuarioUpdate.getEmail() != null){
            usuarioEntity.setEmail(usuarioUpdate.getEmail());
        }

        if(usuarioUpdate.getPhoto() != 0){
            usuarioEntity.setPhoto(usuarioUpdate.getPhoto());
        }

        if(usuarioUpdate.getNome() != null) {
            usuarioEntity.setNome(usuarioUpdate.getNome());
        }

        usuarioEntity = usuarioRepository.save(usuarioEntity);
        return converterUsuarioDTO(usuarioEntity);
    }

    public void delete()
            throws UsuarioException {
        UsuarioEntity usuario = retornarUsuarioEntityById();
        usuarioRepository.delete(usuario);
    }

}
