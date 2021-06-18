package com.jandadav.hydrobot2.statemachine;

import org.springframework.statemachine.StateMachine;

public class NumberInRangeListener extends NumericAttributeChangeListener {

    private double lowerBound;
    private double upperBound;
    private Events outOfBoundsEvent;

    public NumberInRangeListener(String myAttribute, StateMachine machine, double lowerBound, double upperBound, Events outOfBoundsEvent) {
        super(myAttribute, machine);
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("Upper bound has to be higher than lower bound");
        }

        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.outOfBoundsEvent = outOfBoundsEvent;

    }

    @Override
    protected void onChange(double doubleValue) {
        if ( doubleValue < lowerBound || doubleValue > upperBound ) {
            machine.sendEvent(outOfBoundsEvent);
        }
    }
}
