package org.f14a.fatin2.event;

/**
 * 所有事件的基类。
 * <p>
 * 事件类必须继承此类并实现 {@link #isAsync()} 方法，以指示事件是否同步触发。
 * <p>
 * {@link #fire()} 方法能被重写，但是必须调用 {@code super.fire()} 以确保事件能够正确分发。
 */
public abstract class Event {
    /**
     * @see Event
     */
    public abstract boolean isAsync();

    /**
     * @see Event
     */
    public void fire() {
        EventBus.getInstance().post(this);
    }
}
