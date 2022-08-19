package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.repository.CotacaoRepository;
import br.com.dbc.vemser.dbcompras.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CotacaoItemService {


    private final ItemRepository itemRepository;

    private final CotacaoRepository cotacaoRepository;



}
