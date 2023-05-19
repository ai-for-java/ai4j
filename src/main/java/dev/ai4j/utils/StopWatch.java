package dev.ai4j.utils;

public class StopWatch {

    private final long startTime;

    public StopWatch(long currentTimeMillis) {
        this.startTime = currentTimeMillis;
    }

    public static StopWatch start() {
        return new StopWatch(System.currentTimeMillis());
    }

    public int stop() {
        return (int) (System.currentTimeMillis() - startTime);
    }
}
