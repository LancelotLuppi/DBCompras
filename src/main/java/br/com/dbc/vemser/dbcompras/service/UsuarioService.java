package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.dto.UsuarioCreateDTO;
import br.com.dbc.vemser.dbcompras.dto.UsuarioDTO;
import br.com.dbc.vemser.dbcompras.dto.UsuarioLoginDTO;
import br.com.dbc.vemser.dbcompras.entity.UsuarioEntity;
import br.com.dbc.vemser.dbcompras.exception.UsuarioException;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import br.com.dbc.vemser.dbcompras.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@RequiredArgsConstructor
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

}
