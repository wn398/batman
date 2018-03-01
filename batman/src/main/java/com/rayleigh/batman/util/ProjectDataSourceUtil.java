package com.rayleigh.batman.util;

import com.rayleigh.batman.model.ProjectDataSource;

public class ProjectDataSourceUtil {
    public static ProjectDataSource getCopy(ProjectDataSource projectDataSource){
        ProjectDataSource projectDataSource2 = new ProjectDataSource();

        projectDataSource2.setDataSourceNickName(projectDataSource.getDataSourceNickName());
        projectDataSource2.setIsMainDataSource(projectDataSource.getIsMainDataSource());
        projectDataSource2.setPassword(projectDataSource.getPassword());
        projectDataSource2.setUsername(projectDataSource.getUsername());
        projectDataSource2.setDataBaseName(projectDataSource.getDataBaseName());
        projectDataSource2.setHostName(projectDataSource.getHostName());
        projectDataSource2.setDataBaseType(projectDataSource.getDataBaseType());
        projectDataSource2.setMarkup(projectDataSource.getMarkup());
        projectDataSource2.setPort(projectDataSource.getPort());
        projectDataSource2.setId(projectDataSource.getId());
        projectDataSource2.setCreateDate(projectDataSource.getCreateDate());
        projectDataSource2.setUpdateDate(projectDataSource.getUpdateDate());
        projectDataSource2.setVersion(projectDataSource.getVersion());
        return projectDataSource2;
    }
}
