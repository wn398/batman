<#include "CopyRight.ftl">
package ${project.packageName}.extend.repository;

import ${project.packageName}.base.model.${entity.name};
import ${project.packageName}.base.repository.${entity.name}Repository;
import org.springframework.stereotype.Repository;

@Repository
public interface ${entity.name}ExtendRepository extends ${entity.name}Repository {
}
