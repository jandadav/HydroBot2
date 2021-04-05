package com.jandadav.hydrobot2.statemachine;

import org.springframework.statemachine.ExtendedState;

import java.util.ArrayList;
import java.util.List;

public class CompositeChangeListener implements ExtendedState.ExtendedStateChangeListener {

    List<ChangeListener> listeners = new ArrayList<>();

    public void add(ChangeListener listener) {
        listeners.add(listener);
    }

    public List<ChangeListener> getListeners() {
        return new ArrayList<>(listeners);
    }

    public void clear() {
        listeners.clear();
    }

    @Override
    public void changed(Object key, Object value) {
        if (key instanceof String) {
            listeners.forEach(l -> l.changed(((String) key), value));
        }
    }
}
