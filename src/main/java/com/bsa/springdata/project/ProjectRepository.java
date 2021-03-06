package com.bsa.springdata.project;

import com.bsa.springdata.project.dto.ProjectSummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query(
            value = "select count( distinct p) from projects p " +
                    "join teams t on p.id = t.project_id " +
                    "join users u on t.id = u.team_id " +
                    "join user2role u2r on u.id = u2r.user_id ",
            nativeQuery = true)
    int getCountWithRole(String role);


    @Query(
            value = "select p.name, count(distinct t) as teamsNumber, count(u) as developersNumber, " +
                    "string_agg(distinct tech.name, ',' order by tech.name desc) as technologies from projects p " +
                    "join teams t on p.id = t.project_id " +
                    "join users u on t.id = u.team_id " +
                    "join technologies tech on t.technology_id = tech.id " +
                    "group by p.name " +
                    "order by p.name",
            nativeQuery = true)
    List<ProjectSummaryDto> getSummary();

    @Query(
            value = "select p.id, p.name, p.description from projects p " +
                    "join teams t on p.id = t.project_id " +
                    "join users u on t.id = u.team_id " +
                    "group by p.id, p.name " +
                    "order by (count(distinct t.id), count(u.id), p.name) desc " +
                    "limit 1",
            nativeQuery = true)
    Optional<Project> findTheBiggest();

    @Query(
            value = "select p.id, p.name, p.description from projects p " +
                    "join teams t on p.id = t.project_id " +
                    "join users u on t.id = u.team_id " +
                    "join technologies tech on tech.id = t.technology_id " +
                    "where tech.name = :technology " +
                    "group by p.id " +
                    "order by count(u.id) desc " +
                    "limit 5",
            nativeQuery = true)
    List<Project> findTop5ByTechnology(String technology);
}