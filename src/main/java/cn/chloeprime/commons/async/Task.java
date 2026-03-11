package cn.chloeprime.commons.async;

public interface Task {
    /**
     * Stop this task,
     * avoids the callback being called furthermore.
     */
    void stop();
}
