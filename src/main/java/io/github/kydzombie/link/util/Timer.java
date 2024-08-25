package io.github.kydzombie.link.util;

public class Timer {
    boolean active = false;
    long startTime;
    final long maxMillis;

    public Timer(float maxValue) {
        this.maxMillis = (long) (maxValue * 1000);
    }

    public void start() {
        startTime = System.currentTimeMillis();
        active = true;
    }

    public float getPercent() {
        if (!active) return 1f;
        long now = System.currentTimeMillis();
        long timeElapsed = Math.min(maxMillis, now - startTime);
        return (float) timeElapsed / maxMillis;
    }
}