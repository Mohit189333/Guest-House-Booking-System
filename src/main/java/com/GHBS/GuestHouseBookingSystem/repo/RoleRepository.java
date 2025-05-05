package com.GHBS.GuestHouseBookingSystem.repo;

import com.GHBS.GuestHouseBookingSystem.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Used to fetch roles from the database (e.g., USER, ADMIN).
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}