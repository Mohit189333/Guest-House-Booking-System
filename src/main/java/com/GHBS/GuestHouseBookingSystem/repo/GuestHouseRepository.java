package com.GHBS.GuestHouseBookingSystem.repo;

import com.GHBS.GuestHouseBookingSystem.entity.GuestHouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuestHouseRepository extends JpaRepository<GuestHouse, Long> {
}
