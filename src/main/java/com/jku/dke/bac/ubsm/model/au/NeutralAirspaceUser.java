package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;
import jakarta.persistence.Entity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Entity
@Component
public class NeutralAirspaceUser extends AirspaceUser {
    public NeutralAirspaceUser(String s) {
        super(s);
    }

    public NeutralAirspaceUser(String s, double d) {
        super(s, d);
    }


    public NeutralAirspaceUser() {

    }

    @Override
    public String toString() {
        return super.toString() + " - Neutral";
    }

    @Override
    public AirspaceUserType getType() {
        return AirspaceUserType.NEUTRAL;
    }

    @Override
    public Map<Slot, Double> generateWeightMap(Flight flight) {
        return null;
    }

    @Override
    public void generateFlightAttributes(Flight flight, List<Slot> possibleSlots) {
        // int timeRangeInSeconds = flight.getScheduledTime().toSecondOfDay() - flight.getInitialTime().toSecondOfDay();
        // maybe if range is below a certain threshold don't even participate

        Slot bestPossibleSlot = null;
        int slotIndex = 0;

        while (slotIndex < possibleSlots.size() && bestPossibleSlot == null) {
            if (possibleSlots.get(slotIndex).getDepartureTime().isAfter(flight.getInitialTime())
                    && possibleSlots.get(slotIndex).getDepartureTime().isBefore(flight.getScheduledTime())) {
                bestPossibleSlot = possibleSlots.get(slotIndex);
            }
            slotIndex++;
        }

        if (bestPossibleSlot != null) {
            flight.setWishedTime(bestPossibleSlot.getDepartureTime());
        } else {
            flight.setPriority(-1);
        }
    }
}
