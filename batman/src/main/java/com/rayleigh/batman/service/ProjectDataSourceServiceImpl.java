package com.rayleigh.batman.service;

import com.rayleigh.batman.model.ProjectDataSource;
import com.rayleigh.batman.repository.ProjectDataSourceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectDataSourceServiceImpl implements ProjectDataSourceService {
    @Autowired
    private ProjectDataSourceRepository projectDataSourceRepository;
    @Override
    public List<ProjectDataSource> getAll() {
        return projectDataSourceRepository.findAll();
    }

    @Override
    public void deleteById(String id) {
        projectDataSourceRepository.deleteById(id);
    }

    @Override
    public ProjectDataSource findOne(String id) {
        return projectDataSourceRepository.findById(id).get();
    }
}
