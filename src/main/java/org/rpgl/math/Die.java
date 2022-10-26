package org.rpgl.math;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedTransferQueue;

public final class Die {

    private static final Random R;
    private static final Queue<Long> QUEUE;

    static {
        R = new Random(System.currentTimeMillis());
        QUEUE = new LinkedTransferQueue<>();
    }

    public static long roll(long upperBound) {
        if (QUEUE.peek() == null) {
            return R.nextLong(upperBound) + 1;
        }
        return dequeue();
    }

    public static void queue(long value) {
        QUEUE.add(value);
    }

    public static long dequeue() {
        return QUEUE.remove();
    }

    public static void flush() {
        QUEUE.clear();
    }

}
