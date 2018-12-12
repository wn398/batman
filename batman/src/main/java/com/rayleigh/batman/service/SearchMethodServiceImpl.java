package com.rayleigh.batman.service;

import com.rayleigh.batman.model.SearchMethod;
import com.rayleigh.batman.repository.SearchMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SearchMethodServiceImpl implements SearchMethodService {
    @Autowired
    private SearchMethodRepository searchMethodRepository;


    @Override
    public SearchMethod save(SearchMethod searchMethod) {
        return searchMethodRepository.save(searchMethod);
    }

    @Override
    public void delete(String id) {
        searchMethodRepository.deleteById(id);
    }

    @Override
    public SearchMethod findOne(String id) {
        return searchMethodRepository.findById(id).get();
    }
}
