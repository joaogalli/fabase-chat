package com.gihub.joaogalli.fabase_chat.counter;

/**
 * Created by joao.galli on 20/04/2017.
 */

public class SingleCounter<T> extends Counter {

    public SingleCounter(int expected, Listener listener) {
        super(expected, listener);
    }

    public T value;

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
