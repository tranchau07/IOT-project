package com.example.Iot_Project.repository.jpa;


import com.example.Iot_Project.entity.Permission;
import com.example.Iot_Project.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
