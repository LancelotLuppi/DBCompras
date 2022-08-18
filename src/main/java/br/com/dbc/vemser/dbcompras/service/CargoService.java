package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.entity.CargoEntity;
import br.com.dbc.vemser.dbcompras.repository.CargoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CargoService {

    private final CargoRepository cargoRepository;

    private final ObjectMapper objectMapper;

    public CargoEntity findByRole (String role){

        return cargoRepository.findCargoEntitieByName(role).get();

    }

}
