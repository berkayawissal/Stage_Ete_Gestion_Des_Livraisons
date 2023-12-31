package com.example.demo.service.implementation;

import com.example.demo.entity.EnLigne;
import com.example.demo.repository.EnLigneRepository;
import com.example.demo.service.EnLigneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@Transactional
public class EnLigneServiceImpl implements EnLigneService {

    private final EnLigneRepository repository;
    @Autowired
    public EnLigneServiceImpl(EnLigneRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<EnLigne> findAllEnLignes() {

            return repository.findAll();
    }

    @Override
    public EnLigne saveEnLigne (EnLigne enLigne) {
            return repository.save(enLigne);
    }
    @Override
    public void delete(Integer id) {
        if (id==null) {
            log.error("l'ID est null");
            return;
        }
        repository.deleteById(id);
    }

    @Override
    public EnLigne findById(Integer id) {
        Optional<EnLigne> optional =repository.findById(id);
        return optional.orElse(null);
    }
}
