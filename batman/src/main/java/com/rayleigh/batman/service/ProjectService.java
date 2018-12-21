package com.rayleigh.batman.service;

import com.rayleigh.batman.model.Project;
import com.rayleigh.core.service.BaseService;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

/**
 * Created by wangn20 on 2017/6/13.
 */
public interface ProjectService {
    Project save(Project project);

    List<Project> getAll();
    //更新整个实体
    Project update(Project project);
    //更新部分实体，设置了的更新
    Project partUpdate(Project project);

    Page getPageAll(int page,int pageSize);

    Project findOne(String id);

    void deleteOne(Project project);

    void testListSql();

    List<Project> findByUserId(String userId);

    Date getMaxHierachyDate(Project project);
}
