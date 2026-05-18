package com.example.Iot_Project.repository.jpa;


import com.example.Iot_Project.entity.Permission;
import com.example.Iot_Project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
    List<Permission> findAllByNameIn(Set<String> names);
}
