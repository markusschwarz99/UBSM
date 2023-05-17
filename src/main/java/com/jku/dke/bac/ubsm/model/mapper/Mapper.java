package com.jku.dke.bac.ubsm.model.mapper;

import com.jku.dke.bac.ubsm.model.au.AirspaceUser;
import com.jku.dke.bac.ubsm.model.au.Margin;
import com.jku.dke.bac.ubsm.model.dto.*;
import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.*;

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
            Margin[] margins = airspaceUser.getFlexibleFlightPercentages();
            MarginDTO[] marginDTOS = new MarginDTO[]{Mapper.mapMarginToMarginDTO(margins[0]), Mapper.mapMarginToMarginDTO(margins[1]), Mapper.mapMarginToMarginDTO(margins[2])};
            airspaceUserDTO.setFlexibleFlightPercentages(marginDTOS);
            margins = airspaceUser.getFlexibleFlightWithPriorityPercentages();
            marginDTOS = new MarginDTO[]{Mapper.mapMarginToMarginDTO(margins[0]), Mapper.mapMarginToMarginDTO(margins[1]), Mapper.mapMarginToMarginDTO(margins[2])};
            airspaceUserDTO.setFlexibleFlightWithPriorityPercentages(marginDTOS);
            airspaceUserDTO.setWeightMapFunction(getWeightMap(airspaceUser));
            airspaceUserDTOs.add(airspaceUserDTO);
        });

        return airspaceUserDTOs.toArray(airspaceUserDTOs.toArray(new AirspaceUserDTO[0]));
    }

    private static String getWeightMap(AirspaceUser airspaceUser) {
        switch (airspaceUser.getWeightMapFunction().getClass().getSimpleName()) {
            case ("DefaultWeightMapFunction"):
                return "default";
            default:
                return "default";
        }
    }

    public static FlightListDTO mapFlightListToFlightListDTO(int idx, Map<Slot, Flight> flightList) {
        FlightListDTO flightListDTO = new FlightListDTO(idx);
        Map<Slot, FlightDTO> fl = new LinkedHashMap<>();
        flightList.forEach((slot, flight) -> {
            FlightDTO flightDTO = new FlightDTO(flight.getAirspaceUser().getName(), flight.getInitialTime(), flight.getScheduledTime(), flight.getWishedTime(), flight.getNotBefore(), flight.getNotAfter(), flight.getPriority(), flight.getFlightType(), flight.getAirspaceUser().getWeightMapFunction(), flight.getWeightMap());
            fl.put(slot, flightDTO);
        });
        flightListDTO.setFlightList(fl);
        return flightListDTO;
    }

    public static Margin mapMarginDTOToMargin(MarginDTO marginDTO) {
        return new Margin(marginDTO.getLowerBound(), marginDTO.getUpperBound());
    }

    public static MarginDTO mapMarginToMarginDTO(Margin margin) {
        return new MarginDTO(margin.getLowerBound(), margin.getUpperBound());
    }
}
