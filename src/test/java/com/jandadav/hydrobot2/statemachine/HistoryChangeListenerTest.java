package com.jandadav.hydrobot2.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.support.DefaultExtendedState;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Slf4j
class HistoryChangeListenerTest {

    HistoryChangeListener underTest;
    private StateMachine machine;
    
    @BeforeEach
    void setUp() {
        machine = mock(StateMachine.class);
        when(machine.getExtendedState()).thenReturn(new DefaultExtendedState(Collections.singletonMap("testAttr", 5.0d)));
        underTest = new HistoryChangeListener("testAttr", machine, 100000);
    }

    @Test
    void flatline() throws InterruptedException {

        LocalTime t = LocalTime.of(0,0,0);

        underTest.onChange(LocalDateTime.of(LocalDate.now(), t),  5.0d);
        t =  ChronoUnit.MILLIS.addTo(t, 400);
        underTest.onChange(LocalDateTime.of(LocalDate.now(), t),  5.0d);
        t =  ChronoUnit.MILLIS.addTo(t, 320);
        underTest.onChange(LocalDateTime.of(LocalDate.now(), t),  5.0d);
        t =  ChronoUnit.MILLIS.addTo(t, 440);

        assertThat(underTest.evaluate(), closeTo(0.0d, 0.000001d));
    }


    /**
     * At 1 minute @ 4hz with 10% variance, a.k.a. a really trash sensor
     *
     * This can and should be tested cranked up as this test is inherently flaky
     * to gain enough confidence in the value. Cranked down for speed.
     *
     * Test should be calibrated with real sensor's performance data
     */
    @RepeatedTest(value = 10, name = RepeatedTest.LONG_DISPLAY_NAME)
    void noiseStability() throws InterruptedException {

        LocalDateTime baseTime = null;

        for (int i = 0; i < 240; i++) {
            if (baseTime == null) {
                baseTime = LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0));
            } else {
                log.info("Generate chrono: {}", ChronoUnit.MILLIS.addTo(baseTime, Math.round(gaussianNoise(0.2d, 420.0d))));
                baseTime = ChronoUnit.MILLIS.addTo(baseTime, Math.round(gaussianNoise(0.2d, 500.0d)));
            }
            underTest.onChange(baseTime ,gaussianNoise(0.1d, 5.0d));
        }

        assertThat(underTest.evaluate(), closeTo(0.0d, 0.25d));
    }

    @Test
    void drainSpeedTunedTo() throws InterruptedException {
        underTest.onChange(LocalDateTime.of(1984, 1, 1, 0, 0), 0.0d);

        underTest.onChange(LocalDateTime.of(1984, 1, 1, 0, 30), 20.0d);
        assertThat(underTest.evaluate(), closeTo(1.0d, 0.2d));

    }

    @Test
    void fillSpeedTunedTo() throws InterruptedException {
        underTest.onChange(LocalDateTime.of(1984, 1, 1, 0, 0), 0.0d);

        underTest.onChange(LocalDateTime.of(1984, 1, 1, 0, 1), 20.0d);
        assertThat(underTest.evaluate(), closeTo(33.0d, 5.0d));

    }

    @Test
    @Disabled
    void RegressionTests() {
        SimpleRegression regression = new SimpleRegression(false);
        regression.addData(1.0d,1.0d);
        regression.addData(2.0d,2.0d);
        assertThat(regression.getSlope(), is(1.0d));
    }

    private double gaussianNoise(double variance, double mean) {
        Random r = new Random();
        return r.nextGaussian() * Math.sqrt(variance) + mean;
    }
}