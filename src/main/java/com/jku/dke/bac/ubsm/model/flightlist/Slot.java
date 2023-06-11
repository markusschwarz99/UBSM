package com.jku.dke.bac.ubsm.model.flightlist;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class Slot {

    private LocalTime departureTime;

    public Slot(LocalTime departureTime) {
        this.departureTime = departureTime;
    }


    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Slot slot = (Slot) o;

        return getDepartureTime() != null ? getDepartureTime().equals(slot.getDepartureTime()) : slot.getDepartureTime() == null;
    }

    @Override
    public int hashCode() {
        return getDepartureTime() != null ? getDepartureTime().hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "departureTime=" + departureTime +
                '}';
    }
}
