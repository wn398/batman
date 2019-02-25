package com.rayleigh.batman.repository;

import com.rayleigh.batman.model.Project;
import com.rayleigh.batman.uiModel.ProjectListModel;
import com.rayleigh.core.customQuery.CustomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /**
     * 获取项目生成列表模型表
     * @return
     */
    @Query(value = "SELECT project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.update_date AS updateDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = :userId GROUP BY project.ID", nativeQuery=true)
    Page<ProjectListModel> getCodeGeneraterProjectListModel (@Param("userId") String userId, Pageable pageable);

    @Query(value = "SELECT project. ID AS ID,project.NAME AS NAME,project.description AS description,project.package_name AS packageName,COUNT (DISTINCT(MODULE . ID)) AS moduleNum,COUNT (DISTINCT(entity. ID)) AS entityNum,project.create_date AS createDate,project.update_date AS updateDate,project. VERSION AS VERSION FROM batman_project project LEFT JOIN batman_module MODULE ON MODULE .project_id = project. ID LEFT JOIN batman_entity entity ON entity.project_id = project. ID WHERE project.sysuser_id = :userId GROUP BY project.ID", nativeQuery=true)
    List<ProjectListModel> getCodeGeneraterProjectListModel (@Param("userId") String userId);
}
