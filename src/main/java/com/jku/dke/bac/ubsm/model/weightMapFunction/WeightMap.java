package com.jku.dke.bac.ubsm.model.weightMapFunction;

public abstract class WeightMap {
    private double maxWeight;
    private double aPos;
    private double bPos;
    private double aNeg;
    private double bNeg;
    private double threshold;
    private double lowerMargin;
    private double upperMargin;
    private int priority;

    public WeightMap() {
    }

    public WeightMap(double maxWeight, double x1Pos, double y1Pos, double x2Pos, double y2Pos, double x1Neg, double y1Neg, double x2Neg, double y2Neg, double threshold, int priority) {
        this.maxWeight = maxWeight;
        this.aPos = (y2Pos - y1Pos) / (x2Pos - x1Pos);
        this.bPos = y2Pos - (x2Pos * aPos);
        this.aNeg = (y2Neg - y1Neg) / (x2Neg - x1Neg);
        this.bNeg = y2Neg - (x2Neg * aNeg);
        this.threshold = threshold;
        this.lowerMargin = x1Pos;
        this.upperMargin = x2Neg;
        this.priority = priority;
    }

    public double getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(double maxWeight) {
        this.maxWeight = maxWeight;
    }

    public double getaPos() {
        return aPos;
    }

    public void setaPos(double aPos) {
        this.aPos = aPos;
    }

    public double getbPos() {
        return bPos;
    }

    public void setbPos(double bPos) {
        this.bPos = bPos;
    }

    public double getaNeg() {
        return aNeg;
    }

    public void setaNeg(double aNeg) {
        this.aNeg = aNeg;
    }

    public double getbNeg() {
        return bNeg;
    }

    public void setbNeg(double bNeg) {
        this.bNeg = bNeg;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public double getLowerMargin() {
        return lowerMargin;
    }

    public void setLowerMargin(double lowerMargin) {
        this.lowerMargin = lowerMargin;
    }

    public double getUpperMargin() {
        return upperMargin;
    }

    public void setUpperMargin(double upperMargin) {
        this.upperMargin = upperMargin;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return "WeightMap{" +
                "maxWeight=" + maxWeight +
                ", aPos=" + aPos +
                ", bPos=" + bPos +
                ", aNeg=" + aNeg +
                ", bNeg=" + bNeg +
                ", threshold=" + threshold +
                ", lowerMargin=" + lowerMargin +
                ", upperMargin=" + upperMargin +
                ", priority=" + priority +
                '}';
    }
}
