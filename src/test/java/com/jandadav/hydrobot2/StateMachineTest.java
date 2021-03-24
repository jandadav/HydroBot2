package com.jandadav.hydrobot2;

import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;
import java.util.EnumSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class StateMachineTest {

    static enum States {
        FILL, DRAIN, STANDBY
    }

    static enum Events {
        POT_FULL, POT_EMPTY, TIME_TO_WATER
    }

    public StateMachine<States, Events> buildMachine() throws Exception {
        StateMachineBuilder.Builder<States, Events> builder = StateMachineBuilder.builder();

        builder.configureStates()
                .withStates()
                .initial(States.STANDBY)
                .states(EnumSet.allOf(States.class));

        builder.configureTransitions()
                .withExternal().source(States.STANDBY).target(States.FILL)
                .event(Events.TIME_TO_WATER)
                .and()
                .withExternal().source(States.FILL).target(States.DRAIN)
                .event(Events.POT_FULL)
                .and()
                .withExternal().source(States.DRAIN).target(States.STANDBY)
                .event(Events.POT_EMPTY);


        StateMachine<States, Events> stateMachine = builder.build();

        return stateMachine;

    }

    @Test
    void eventsChangeSMState() throws Exception {
        StateMachine<States, Events> sm = buildMachine();
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
