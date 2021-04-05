package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

class CompositeChangeListenerTest {

    @Test
    void canAddRemoveDeleteChangeListeners() {
        CompositeChangeListener underTest = new CompositeChangeListener();
        ChangeListener listener = mock(ChangeListener.class);
        underTest.add(listener);

        assertThat(underTest.getListeners(), contains(listener));

        underTest.clear();
        assertThat(underTest.getListeners(), hasSize(0));
    }

    @Test
    void onChangeAllChangeListenersAreCalled() {
        CompositeChangeListener underTest = new CompositeChangeListener();
        ChangeListener listener1 = mock(ChangeListener.class);
        ChangeListener listener2 = mock(ChangeListener.class);
        underTest.add(listener1);
        underTest.add(listener2);
        underTest.changed("key", "value");
        verify(listener1, times(1)).changed("key", "value");
        verify(listener2, times(1)).changed("key", "value");
    }

    @Test
    void onlyChangesContainingStringKeyArePropagatedDownToListeners() {
        CompositeChangeListener underTest = new CompositeChangeListener();
        ChangeListener listener1 = mock(ChangeListener.class);
        underTest.add(listener1);

        underTest.changed("key", "value");
        verify(listener1, times(1)).changed("key", "value");

        underTest.changed(new BigDecimal(5), "value");
        verify(listener1, times(1)).changed(any(), any());
    }
}