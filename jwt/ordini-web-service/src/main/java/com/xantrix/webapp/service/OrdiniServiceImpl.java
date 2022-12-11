package com.xantrix.webapp.service;

import com.xantrix.webapp.entity.Ordini;
import com.xantrix.webapp.repository.DettOrdiniRepository;
import com.xantrix.webapp.repository.OrdiniRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional()
public class OrdiniServiceImpl implements OrdiniService {

    private final OrdiniRepository ordiniRepository;
    private final DettOrdiniRepository dettOrdiniRepository;

    public OrdiniServiceImpl(OrdiniRepository ordiniRepository, DettOrdiniRepository dettOrdiniRepository) {
        this.ordiniRepository = ordiniRepository;
        this.dettOrdiniRepository = dettOrdiniRepository;
    }

    @Override
    public Optional<Ordini> findById(String id) {
        return ordiniRepository.findById(id);
    }

    @Override
    public void insOrdine(Ordini ordini) {
        ordiniRepository.save(ordini);
    }

    @Override
    public void delOrdine(Ordini ordini) {
        ordiniRepository.delete(ordini);
    }

    @Override
    public double selValTot(String id) {
        return dettOrdiniRepository.getValOrdine(id);
    }


}
