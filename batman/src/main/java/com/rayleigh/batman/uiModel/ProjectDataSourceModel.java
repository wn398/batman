package com.rayleigh.batman.uiModel;

import com.rayleigh.batman.model.ProjectDataSource;

import java.util.List;

public class ProjectDataSourceModel extends ProjectDataSource {
    private List<String> tableNames;

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }
}
