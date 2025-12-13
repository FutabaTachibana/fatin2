package org.f14a.fatin2.event;

public interface Cancelable {
    boolean isCancelled();
    void cancel();
}
