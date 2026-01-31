package com.example.Iot_Project.repository;


import com.example.Iot_Project.enity.Permission;
import com.example.Iot_Project.enity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
