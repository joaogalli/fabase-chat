package com.gihub.joaogalli.fabase_chat.counter;

/**
 * Created by joao.galli on 20/04/2017.
 */

public class Counter {

    int expected, counted;

    private Listener listener;

    public Counter(int expected, Listener listener) {
        this.expected = expected;
        this.listener = listener;
        verifyIfReached();
    }

    public void increase() {
        counted++;
        verifyIfReached();
    }

    public void verifyIfReached() {
        if (expected <= counted && listener != null) {
            listener.onReached(this);
        }
    }

    public void reset() {
        counted = 0;
    }

    public static interface Listener<T extends Counter> {
        void onReached(T counter);
    }
}
