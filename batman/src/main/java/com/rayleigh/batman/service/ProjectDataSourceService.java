package com.rayleigh.batman.service;


import com.rayleigh.batman.model.ProjectDataSource;
import com.rayleigh.core.service.BaseService;

import java.util.List;

public interface ProjectDataSourceService extends BaseService {
    List<ProjectDataSource> getAll();

    void deleteById(String id);

    ProjectDataSource findOne(String id);
}
