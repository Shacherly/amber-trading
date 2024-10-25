package com.google.backend.trading.util;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 环形数组时间窗口统计
 *
 * @author trading
 */
public class ArraySideWindow {
    private final AtomicInteger[] array;
    private final long[] times;
    private final int span = 200;
    private final int size;
    private final int delta = 5;
    private final int MILL = 1000;
    private final int second;
    private volatile int index;
    private ReentrantLock lock;

    public ArraySideWindow(int second) {
        this.second = second;
        this.size = second * MILL / span + delta;
        this.array = new AtomicInteger[size];
        this.times = new long[size];
        this.index = 0;
        for (int i = 0; i < size; i++) {
            this.array[i] = new AtomicInteger(0);
        }
        this.times[index] = System.currentTimeMillis();
        this.lock = new ReentrantLock(false);
    }

    public void count() {
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            if (now - times[index] > span) {
                index++;
                index = index < size ? index : 0;
            }
            if (now - times[index] >= second * MILL) {
                times[index] = now;
                this.array[index].set(0);
            }
            this.array[index].incrementAndGet();
        } catch (Exception e) {

        } finally {
            lock.unlock();
        }
    }

    public int get() {
        int curIndex = index;
        long now = System.currentTimeMillis();
        int count = 0;
        int sum = 0;
        while (count < size) {
            if (now - times[curIndex] >= second * MILL) {
                break;
            }
            sum += array[curIndex--].get();
            if (curIndex < 0) {
                curIndex = size - 1;
            }
            count++;
        }
        return sum;
    }

    public int getSecond() {
        return second;
    }

    public static void main(String[] args) throws InterruptedException {
        ArraySideWindow window = new ArraySideWindow(5);
        for (int i = 0; i < 20; i++) {
            Thread.sleep(300);
            window.count();
        }
        System.out.println(window.get());
        Thread.sleep(5000);
        System.err.println(window.get());
        for (int i = 0; i < 10; i++) {
            Thread.sleep(300);
            window.count();
        }
        System.err.println(window.get());


    }
}
