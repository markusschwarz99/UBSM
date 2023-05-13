package com.jku.dke.bac.ubsm.model.weightMapFunction;

import java.util.function.Function;

public class DefaultWeightMapFunction extends WeightMap implements Function<Double, Double> {
    public DefaultWeightMapFunction() {
        super();
    }

    public DefaultWeightMapFunction(double maxWeight, double x1Pos, double y1Pos, double x2Pos, double y2Pos, double x1Neg, double y1Neg, double x2Neg, double y2Neg, double threshold) {
        super(maxWeight, x1Pos, y1Pos, x2Pos, y2Pos, x1Neg, y1Neg, x2Neg, y2Neg, threshold);
    }

    @Override
    public Double apply(Double slotDepartureTimeInSeconds) {
        if (slotDepartureTimeInSeconds < this.getLowerMargin() || slotDepartureTimeInSeconds > this.getUpperMargin()) {
            return Double.MIN_VALUE;
        } else {
            if (slotDepartureTimeInSeconds < this.getThreshold())
                return this.getMaxWeight() * (slotDepartureTimeInSeconds * this.getaPos() + this.getbPos());
            else return this.getMaxWeight() * (slotDepartureTimeInSeconds * this.getaNeg() + this.getbNeg());
        }
    }

    @Override
    public String toString() {
        return super.toString() + " - default";
    }
}
