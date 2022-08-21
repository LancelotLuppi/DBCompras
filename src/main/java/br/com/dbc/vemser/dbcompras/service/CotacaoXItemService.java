package br.com.dbc.vemser.dbcompras.service;

import br.com.dbc.vemser.dbcompras.entity.CotacaoXItemEntity;
import br.com.dbc.vemser.dbcompras.entity.pk.CotacaoXItemPK;
import br.com.dbc.vemser.dbcompras.exception.EntidadeNaoEncontradaException;
import br.com.dbc.vemser.dbcompras.repository.CotacaoXItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CotacaoXItemService {

    private final CotacaoXItemRepository cotacaoXItemRepository;

    public CotacaoXItemEntity findById(CotacaoXItemPK cotacaoXItemPK) throws EntidadeNaoEncontradaException {
        return cotacaoXItemRepository.findById(cotacaoXItemPK)
                .orElseThrow(()-> new EntidadeNaoEncontradaException("Este item não existe"));
    }
}
