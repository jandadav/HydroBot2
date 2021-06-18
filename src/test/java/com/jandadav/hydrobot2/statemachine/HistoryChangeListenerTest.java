package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultExtendedState;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class HistoryChangeListenerTest {

    HistoryChangeListener underTest;
    private StateMachine machine;
    
    @BeforeEach
    void setUp() {
        machine = mock(StateMachine.class);
        when(machine.getExtendedState()).thenReturn(new DefaultExtendedState(Collections.singletonMap("testAttr", 5.0d)));
        underTest = new HistoryChangeListener("testAttr" ,machine);
    }

    @Test
    void name() {
        underTest.onChange(5.0d);
        underTest.onChange(5.0d);
        underTest.onChange(5.0d);
        assertThat(underTest.evaluate(), closeTo(0.0d, 0.0001d));
    }

    @Test
    void name2() throws InterruptedException {
        underTest.onChange(5.0d);
        TimeUnit.SECONDS.sleep(10);
        underTest.onChange(10.0d);
        TimeUnit.SECONDS.sleep(10);
        underTest.onChange(50.0d);
        assertThat(underTest.evaluate(), greaterThan(5.0d));

    }
}