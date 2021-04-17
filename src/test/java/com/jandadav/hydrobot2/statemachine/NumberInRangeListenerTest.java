package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NumberInRangeListenerTest {

    private StateMachine machine;

    @BeforeEach
    void setUp() {
        machine = mock(StateMachine.class);
    }

    @Test
    void reactsOnlyOnInterestingAttributesChanges() {
        NumberInRangeListener underTest = new NumberInRangeListener("MyAttribute", machine, 0, 5, Events.POT_FULL);

        underTest.changed("AnotherAttribute", "value");
        verify(machine, times(0)).sendEvent(Events.POT_FULL);
        underTest.changed("MyAttribute", "value");
        verify(machine, times(1)).sendEvent(Events.POT_FULL);
    }

    @Test
    void boundsHaveToBeOrdered() {
        assertThrows(IllegalArgumentException.class, ()-> new NumberInRangeListener("MyAttribute", machine, 5, 0, Events.POT_FULL));
        assertDoesNotThrow(()-> new NumberInRangeListener("MyAttribute", machine, 0, 5, Events.POT_FULL));

    }
}