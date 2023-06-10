package com.jku.dke.bac.ubsm.model.au.weightMapFunction;

public class DefaultWeightMapFunction extends WeightMap {
    private double initialCredits;
    private double maxWeight;
    private double aPos;
    private double bPos;
    private double aNeg;
    private double bNeg;
    private double threshold;
    private double lowerMargin;
    private double upperMargin;
    private double lowerLimit;
    private int priority;
    private double balance;

    public DefaultWeightMapFunction() {
        super();
    }

    // x1 = notBefore, y1 = 0 | x2 = wishedTime, y2 = 1 | x3 = notAfter, y3 = 0
    public DefaultWeightMapFunction(double maxWeight, double x1, double y1, double x2, double y2, double x3, double y3, int priority, double lowerLimit, double balance, double initialCredits) {
        super();
        this.maxWeight = maxWeight;
        this.aPos = (y2 - y1) / (x2 - x1);
        this.bPos = y2 - (x2 * aPos);
        this.aNeg = (y3 - y2) / (x3 - x2);
        this.bNeg = y3 - (x3 * aNeg);
        this.threshold = x2;
        this.lowerMargin = x1;
        this.upperMargin = x3;
        this.priority = priority;
        this.lowerLimit = lowerLimit;
        this.balance = balance;
        this.initialCredits = initialCredits;
    }

    @Override
    public Double apply(Double slotDepartureTimeInSeconds) {
        if (slotDepartureTimeInSeconds < lowerLimit) {
            return -Double.MAX_VALUE;
        } else if (slotDepartureTimeInSeconds < lowerMargin || slotDepartureTimeInSeconds > upperMargin) {
            return 1.0;
        } else {
            double priorityImpact = priority / 10.0;
            double balanceImpact = balance / initialCredits + 0.5;
            if (slotDepartureTimeInSeconds < threshold) {
                return balanceImpact * priorityImpact * (maxWeight * (slotDepartureTimeInSeconds * aPos + bPos));
            } else {
                return balanceImpact * priorityImpact * (maxWeight * (slotDepartureTimeInSeconds * aNeg + bNeg));
            }
        }
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

    public double getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(double lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getInitialCredits() {
        return initialCredits;
    }

    public void setInitialCredits(double initialCredits) {
        this.initialCredits = initialCredits;
    }

    @Override
    public String toString() {
        return super.toString() + " - default";
    }
}
