package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.ExtendedState;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultExtendedState;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class NumberInRangeListenerTest {

    private StateMachine machine;
    private final static Object event = new Object();

    @BeforeEach
    void setUp() {
        machine = mock(StateMachine.class);
        when(machine.getExtendedState()).thenReturn(new DefaultExtendedState(Collections.singletonMap("testAttr", 5.0d)));
    }

    @Test
    void reactsOnlyOnInterestingAttributesChanges() {
        NumberInRangeListener underTest = new NumberInRangeListener("testAttr", machine, 0, 5, Events.POT_FULL);

        underTest.changed("AnotherAttribute", "value");
        verify(machine, never()).sendEvent(Events.POT_FULL);

        underTest.changed("testAttr", 3.0d);
        verify(machine, never()).sendEvent(Events.POT_FULL);
        underTest.changed("testAttr", 7.0d);
        verify(machine, atLeastOnce()).sendEvent(Events.POT_FULL);
    }

    @Test
    void boundsHaveToBeOrdered() {
        assertThrows(IllegalArgumentException.class, () -> new NumberInRangeListener("testAttr", machine, 5, 0, Events.POT_FULL));
        assertDoesNotThrow(() -> new NumberInRangeListener("testAttr", machine, 0, 5, Events.POT_FULL));
    }

    @Test
    void checksThatAtributeExistsAndTypeOfAttributeIsNumeric() {
        when(machine.getExtendedState()).thenReturn(new DefaultExtendedState(Collections.emptyMap()));
        assertThrows(IllegalArgumentException.class, () -> new NumberInRangeListener("testAttr", machine, 0, 0, null));

        when(machine.getExtendedState()).thenReturn(new DefaultExtendedState(Collections.singletonMap("testAttr", 5.0d)));
        assertDoesNotThrow( () -> new NumberInRangeListener("testAttr", machine, 0, 0, null));
    }

}