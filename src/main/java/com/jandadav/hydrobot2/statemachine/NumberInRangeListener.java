package com.jandadav.hydrobot2.statemachine;

import org.springframework.statemachine.StateMachine;

public class NumberInRangeListener implements ChangeListener {

    private String myAttribute;
    private StateMachine machine;
    private double lowerBound;
    private double upperBound;
    private StateMachineFactory.Events outOfBoundsEvent;

    public NumberInRangeListener(String myAttribute, StateMachine machine, double lowerBound, double upperBound, StateMachineFactory.Events outOfBoundsEvent) {
        if (upperBound < lowerBound) {
            throw new IllegalArgumentException("Upper bound has to be higher than lower bound");
        }

        this.myAttribute = myAttribute;
        this.machine = machine;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.outOfBoundsEvent = outOfBoundsEvent;
    }



    @Override
    public void changed(String key, Object value) {
        if (myAttribute.equals(key)) {
            machine.sendEvent(outOfBoundsEvent);
        }
    }
}
