package com.jandadav.hydrobot2.statemachine;

import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;

import java.util.Objects;

public class NumberInRangeListener implements ChangeListener {

    private String myAttribute;
    private StateMachine machine;
    private double lowerBound;
    private double upperBound;
    private StateMachineFactory.Events outOfBoundsEvent;

    //maybe relax to E extends Object instead of StateMachineFactory.Events
    public NumberInRangeListener(String myAttribute, StateMachine machine, double lowerBound, double upperBound, StateMachineFactory.Events outOfBoundsEvent) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("Upper bound has to be higher than lower bound");
        }

        this.myAttribute = myAttribute;
        this.machine = machine;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.outOfBoundsEvent = outOfBoundsEvent;

        getAttribute();
    }

    private double getAttribute() {
        Objects.requireNonNull(machine.getExtendedState(), "State machine's extended state cannot be null");
        try {
            Double aDouble = machine.getExtendedState().get(myAttribute, Double.class);
            Objects.requireNonNull(aDouble);
            return aDouble;
        } catch (Exception e) {
            throw new IllegalArgumentException("Attribute " + myAttribute + " does not exist within extended state or is not of double type");
        }
    }



    @Override
    public void changed(String key, Object value) {
        if (myAttribute.equals(key)) {
            if (value instanceof Double) {
                Double doubleValue = (Double) value;
                if ( doubleValue < lowerBound || doubleValue > upperBound ) {
                    machine.sendEvent(outOfBoundsEvent);
                }
            } else {
                throw new IllegalStateException("Attribute " + myAttribute + " is not of double type");
            }


        }
    }
}
