package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.repository.ModuleRepository;
import com.rayleigh.batman.repository.ProjectRepository;
import com.rayleigh.batman.util.MapperUtil;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    @Override
    public Project save(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public List<Project> getAll() {
        return projectRepository.findAll();
    }

    @Override
    public Project update(Project project) {
        return projectRepository.save(project);
    }

    //部分更新
    @Override
    public Project partUpdate(Project project) {
        //更新必须先查出来
        Project dataBaseProject = projectRepository.findOne(project.getId());
        //部分更新
        MapperUtil.getModelMapper().updatePropertyForProject(project,dataBaseProject);
       // dataBaseProject.getModules().parallelStream().forEach(module -> module.setProject(dataBaseProject));
        projectRepository.save(dataBaseProject);

        //防止循环解析json
        //dataBaseProject.getModules().parallelStream().forEach(module -> module.setProject(null));

        return dataBaseProject;

    }

    @Override
    public Page getPageAll(int page,int pageSize) {
        return projectRepository.findAll(new PageRequest(page,pageSize));
    }


    @Override
    public Project findOne(String id) {
        return projectRepository.findOne(id);
    }

    @Override
    public void deleteOne(Project project) {
        projectRepository.delete(project.getId());
    }

    @Override
    public void testListSql() {
        projectRepository.listBySQL("select * from batman_project");
    }


}
