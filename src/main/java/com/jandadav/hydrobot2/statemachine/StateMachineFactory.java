package com.jandadav.hydrobot2.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineBuilder;

import java.util.EnumSet;

@Slf4j
public class StateMachineFactory {

    public static StateMachine<States, Events> buildMachine(String machineId) {

        try {
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

            builder.configureConfiguration().withConfiguration().machineId(machineId);
            StateMachine<States, Events> stateMachine = builder.build();
            return stateMachine;

        } catch (Exception e) {
            log.error("Could not build state machine, shutting down.");
            System.exit(-1);
            return null;
        }
    }

}
