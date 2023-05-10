package com.jku.dke.bac.ubsm.model.au;


import com.jku.dke.bac.ubsm.model.flightlist.Flight;
import com.jku.dke.bac.ubsm.model.flightlist.FlightType;
import com.jku.dke.bac.ubsm.model.flightlist.Slot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class NeutralAirspaceUser extends AirspaceUser {

    public static Function<Double, Double> mirror(Function<Double, Double> originalFunction, double xPoint, double yPoint) {
        return x -> {
            // Calculate the distance between the original point and the point of reflection
            double distance = Math.sqrt(Math.pow(x - xPoint, 2) + Math.pow(originalFunction.apply(x) - yPoint, 2));

            // Calculate the reflected point
            double reflectedX = 2 * xPoint - x;
            double reflectedY = 2 * yPoint - originalFunction.apply(x);

            // Calculate the distance between the reflected point and the point of reflection
            double reflectedDistance = Math.sqrt(Math.pow(reflectedX - xPoint, 2) + Math.pow(reflectedY - yPoint, 2));

            // Scale the reflected distance by the distance ratio to preserve the shape of the original function
            double distanceRatio = distance / reflectedDistance;

            return yPoint + (reflectedY - yPoint) * distanceRatio;
        };
    }

    @Override
    public String toString() {
        return super.toString() + " - Neutral";
    }

    @Override
    public void generateWeightMap(Flight flight, List<Slot> possibleSlots) {
        double x1Pos = flight.getNotBefore().toSecondOfDay();
        double y1Pos = 0;
        double x2Pos = flight.getWishedTime().toSecondOfDay();
        double y2Pos = 1;
        double aPos = (y2Pos - y1Pos) / (x2Pos - x1Pos);
        double bPos = y2Pos / (flight.getWishedTime().toSecondOfDay() * aPos);
        System.out.println("x1Pos:" + x1Pos);
        System.out.println("y1Pos:" + y1Pos);
        System.out.println("x2Pos:" + x2Pos);
        System.out.println("y2Pos:" + y2Pos);
        System.out.println("aPos:" + aPos);
        System.out.println("bPos:" + bPos);

        double x1Neg = flight.getNotAfter().toSecondOfDay() - flight.getWishedTime().toSecondOfDay();
        // double x1Neg = flight.getWishedTime().toSecondOfDay();
        double y1Neg = 0;
        double x2Neg = flight.getWishedTime().toSecondOfDay();
        double y2Neg = 1;
        double aNeg = (y2Neg - y1Neg) / (x2Neg - x1Neg);
        double bNeg = y1Neg / (flight.getNotAfter().toSecondOfDay() * aNeg);
        System.out.println("x1Neg:" + x1Neg);
        System.out.println("y1Neg:" + y1Neg);
        System.out.println("x2Neg:" + x2Neg);
        System.out.println("y2Neg:" + y2Neg);
        System.out.println("aNeg:" + aNeg);
        System.out.println("bNeg:" + bNeg);

        Map<Slot, Double> weightMap = new LinkedHashMap<>();
        System.out.println("---- New Flight ----");
        possibleSlots.forEach(slot -> {
            double weight;
            if (slot.getDepartureTime().isBefore(flight.getNotBefore()) || slot.getDepartureTime().isAfter(flight.getNotAfter())) {
                weight = Double.MIN_VALUE;
            } else {
                if (slot.getDepartureTime().isBefore(flight.getWishedTime())) {
                    weight = slot.getDepartureTime().toSecondOfDay() * aPos + bPos;
                    System.out.println("pos");
                } else {
                    weight = slot.getDepartureTime().toSecondOfDay() * aNeg + bNeg;
                    System.out.println("neg");
                }
            }
            weightMap.put(slot, weight);
        });
        flight.setWeightMap(weightMap);
    }

    @Override
    protected void generateTimes(Flight flight, List<Slot> possibleSlots) {
        if (flight.getFlightType() == FlightType.PRIORITY) {
            generatePriorityTimes(flight, possibleSlots, 60);
        } else if (flight.getFlightType() == FlightType.FLEXIBLE) {
            generateFlexibleTimes(flight, possibleSlots, 0.20, 0.35, 0.75);
        } else if (flight.getFlightType() == FlightType.FLEXIBLEWITHPRIORITY) {
            generateFlexibleWithPriorityTimes(flight, possibleSlots, 0.25, 0.45);
        }
    }

}
