package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class StateMachineFactoryTest {

    @Test
    void factoryGeneratesBuiltMachine() {
        StateMachine<States, Events> berta = StateMachineFactory.buildMachine("Berta");
        assertTrue(berta.getId().equals("Berta"));
    }

    @Test
    void stateMachineHasDefinedFlow() {
        StateMachine<States, Events> sm = StateMachineFactory.buildMachine("Berta");
        sm.start();
        assertThat(sm.getState().getId(), is(States.STANDBY));

        sm.sendEvent(Events.TIME_TO_WATER);
        assertThat(sm.getState().getId(), is(States.FILL));

        sm.sendEvent(Events.POT_FULL);
        assertThat(sm.getState().getId(), is(States.DRAIN));

        sm.sendEvent(Events.POT_EMPTY);
        assertThat(sm.getState().getId(), is(States.STANDBY));
    }
}