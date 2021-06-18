package com.jandadav.hydrobot2.statemachine;

import org.springframework.statemachine.StateMachine;

import java.util.Objects;

public abstract class NumericAttributeChangeListener implements ChangeListener {
    protected String myAttribute;
    protected StateMachine machine;

    public NumericAttributeChangeListener(String myAttribute, StateMachine machine) {
        this.myAttribute = myAttribute;
        this.machine = machine;
        getAttribute();
    }

    protected double getAttribute() {
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
                onChange(doubleValue);
            } else {
                throw new IllegalStateException("Attribute " + myAttribute + " is not of double type");
            }
        }
    }

    protected abstract void onChange(double doubleValue);
}
