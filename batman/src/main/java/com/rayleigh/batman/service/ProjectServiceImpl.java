package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.repository.ModuleRepository;
import com.rayleigh.batman.repository.ProjectRepository;
import com.rayleigh.batman.util.BatmanBaseModelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collections;
import java.util.Date;
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
       // Project dataBaseProject = projectRepository.findOne(project.getId());
        //部分更新
        Project resultProject = (Project) BatmanBaseModelUtil.saveOrUpdateBaseModelObjWithRelationPreProcess(project);
       // MapperUtil.getModelMapper().updatePropertyForProject(project,dataBaseProject);
       // dataBaseProject.getModules().parallelStream().forEach(module -> module.setProject(dataBaseProject));
        projectRepository.save(resultProject);

        //防止循环解析json
        //dataBaseProject.getModules().parallelStream().forEach(module -> module.setProject(null));

        return resultProject;

    }

    @Override
    public Page getPageAll(int page,int pageSize) {
        return projectRepository.findAll(PageRequest.of(page,pageSize));
    }


    @Override
    public Project findOne(String id) {
        return projectRepository.findById(id).get();
    }

    @Override
    public void deleteOne(Project project) {
        projectRepository.deleteById(project.getId());
    }

    @Override
    public void testListSql() {
        projectRepository.listBySQL("select * from batman_project");
    }

    @Override
    public List<Project> findByUserId(String userId) {
        return projectRepository.findByUserId(userId);
    }

    @Override
    public void setUpdateDate(String id, Date date) {
        projectRepository.updateAll(new Specification<Project>() {
            @Override
            public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get("id"),id);
            }
        }, Collections.singletonMap("updateDate",date));
    }

}
