package com.jandadav.hydrobot2.statemachine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.springframework.statemachine.StateMachine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class HistoryChangeListener extends NumericAttributeChangeListener{

    private Queue<DataPoint> queue = new ConcurrentLinkedQueue<>();

    @Getter
    @RequiredArgsConstructor
    public static class DataPoint {
        private final LocalDateTime dateTime;
        private final double value;
    }

    public HistoryChangeListener(String myAttribute, StateMachine machine) {
        super(myAttribute, machine);
    }

    @Override
    protected void onChange(double doubleValue) {
        queue.add(new DataPoint(LocalDateTime.now(), doubleValue));
    }

    public double evaluate() {
        SimpleRegression regression = new SimpleRegression(false);
        queue.forEach(dataPoint -> regression.addData(createTimestamp(dataPoint.getDateTime()), dataPoint.getValue()));
        return regression.getSlope();
    }

    public double createTimestamp(LocalDateTime dateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        return dateTime.atZone(zoneId).toEpochSecond();
    }
}
