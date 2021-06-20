package com.jandadav.hydrobot2.statemachine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.statemachine.StateMachine;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
@Slf4j
public class HistoryChangeListener extends NumericAttributeChangeListener{

    private Queue<DataPoint> queue = new ConcurrentLinkedQueue<>();
    private LocalDateTime firstMeasurement = null;
    private final double sensitivity;

    @Getter
    @RequiredArgsConstructor
    public static class DataPoint {
        private final LocalDateTime dateTime;
        private final double value;
    }

    public HistoryChangeListener(String myAttribute, StateMachine machine) {
        this(myAttribute, machine, 1.0d);
    }

    public HistoryChangeListener(String myAttribute, StateMachine machine, double sensitivity) {
        super(myAttribute, machine);
        this.sensitivity = sensitivity;
    }

    @Override
    protected void onChange(double doubleValue) {
        queue.add(new DataPoint(LocalDateTime.now(), doubleValue));
    }

    protected void onChange(LocalDateTime when, double doubleValue) {
        queue.add(new DataPoint(when, doubleValue));
    }

    public double evaluate() {
        SimpleRegression regression = new SimpleRegression(true);
        LocalDateTime firstMeasurement = queue.peek().getDateTime();
        queue.forEach(dataPoint -> {

            long chrono = ChronoUnit.MILLIS.between(firstMeasurement, dataPoint.getDateTime());
            log.info("chrono: {}", chrono);
            log.info("datapoint: {}", dataPoint.getValue());

            regression.addData(
                    chrono
                , dataPoint.getValue());
        });
        double result = regression.getSlope() * sensitivity; //100000;
        log.info("slope: {}", String.format("%.12f", result));
        return result;
    }
}
