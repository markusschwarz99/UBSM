package com.jku.dke.bac.ubsm.model.mapper;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.dto.auDTO.AggressiveAirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.AirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.NeutralAirspaceUserDTO;
import com.jku.dke.bac.ubsm.model.dto.auDTO.PassiveAirspaceUserDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mapper {

    public static AirspaceUserDTO[] mapAirspaceUserToAirspaceUserDTO(AirspaceUser[] airspaceUsers) {
        List<AirspaceUserDTO> airspaceUserDTOs = new ArrayList<>();
        Arrays.stream(airspaceUsers).forEach(airspaceUser -> {
            AirspaceUserDTO airspaceUserDTO = null;
            switch (airspaceUser.getClass().getSimpleName()) {
                case "AggressiveAirspaceUser" -> airspaceUserDTO = new AggressiveAirspaceUserDTO();
                case "NeutralAirspaceUser" -> airspaceUserDTO = new NeutralAirspaceUserDTO();
                case "PassiveAirspaceUser" -> airspaceUserDTO = new PassiveAirspaceUserDTO();
            }
            if (airspaceUserDTO == null) throw new IllegalArgumentException("AirspaceUserDTO is null");
            airspaceUserDTO.setName(airspaceUser.getName());
            airspaceUserDTO.setCredits(airspaceUser.getCredits());
            airspaceUserDTO.setProbabilityFlexibleFlight(airspaceUser.getPriorityDistribution()[0]);
            airspaceUserDTO.setProbabilityPriorityFlight(100 - airspaceUser.getPriorityDistribution()[1]);
            airspaceUserDTO.setProbabilityFlexibleWithPriorityFlight(100 - (airspaceUser.getPriorityDistribution()[0] + 100 - airspaceUser.getPriorityDistribution()[1]));
            airspaceUserDTO.setPriorityFlightMinutesToAdd(airspaceUser.getPriorityFlightMinutesToAdd());
            airspaceUserDTO.setFlexibleFlightPercentages(airspaceUser.getFlexibleFlightPercentages());
            airspaceUserDTO.setFlexibleFlightWithPriorityPercentages(airspaceUser.getFlexibleFlightWithPriorityPercentages());
            airspaceUserDTO.setWeightMapFunction(getWeightMap(airspaceUser));
            airspaceUserDTOs.add(airspaceUserDTO);
        });

        return airspaceUserDTOs.toArray(airspaceUserDTOs.toArray(new AirspaceUserDTO[0]));
    }

    private static String getWeightMap(AirspaceUser airspaceUser) {
        switch (airspaceUser.getWeightMapFunction().getClass().getSimpleName()) {
            case ("DefaultWeightMapFunction"):
                return "DefaultWeightMapFunction";
        }
        return "unknown";
    }
}
