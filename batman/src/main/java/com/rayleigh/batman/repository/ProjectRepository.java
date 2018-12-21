package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Project;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface ProjectRepository extends CustomRepository<Project, String> {
    @Query("select project from Project project where project.sysUser.id=:userId")
    List<Project> findByUserId(@Param("userId") String userId);

    @Query("select max(module.updateDate) from Module module where module.project.id=:projectId")
    Date getMaxModuleUpdateDateByProjectId(@Param("projectId") String projectId);

    @Query("select max(projectDataSource.updateDate) from ProjectDataSource projectDataSource where projectDataSource.project.id=:projectId")
    Date getMaxDataSourceUpdateDateByProjectId(@Param("projectId") String projectId);

    @Query("select max(project.hierachy_date) from Project project where project.id=:projectId")
    Date getMaxHierachyDateByProjectId(@Param("projectId") String projectId);
}
