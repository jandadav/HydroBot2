package com.jandadav.hydrobot2.statemachine;

import org.junit.jupiter.api.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;

class CompositeChangeListenerTest {

    @Test
    void canAddRemoveDeleteChangeListener() {
        CompositeChangeListener underTest = new CompositeChangeListener();
        ChangeListener listener = new ChangeListener();
        underTest.add(listener);

        assertThat(underTest.getListeners(), contains(listener));

        underTest.clear();
        assertThat(underTest.getListeners(), hasSize(0));
    }

    @Test
    void onChangeAllChangeListenersAreCalled() {
        CompositeChangeListener underTest = new CompositeChangeListener();
        ChangeListener listener = mock(ChangeListener.class);
        underTest.add(listener);
        underTest.changed("key", "value");
        verify(listener, times(1)).changed("key", "value");
    }
}