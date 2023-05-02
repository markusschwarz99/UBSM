package com.jku.dke.bac.ubsm.repos;

import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightRepository  extends JpaRepository<Flight, Long> {
}
