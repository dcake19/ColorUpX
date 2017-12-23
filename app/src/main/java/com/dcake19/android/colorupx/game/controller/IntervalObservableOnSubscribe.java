package com.dcake19.android.colorupx.game.controller;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class IntervalObservableOnSubscribe implements ObservableOnSubscribe<Integer> {

    private Thread thread;
    private Integer id = 0;
    private AtomicBoolean subscribed = new AtomicBoolean(true);
    private AtomicBoolean playing = new AtomicBoolean(true);
    private AtomicInteger interval = new AtomicInteger();
    private long remaining = 0;

    IntervalObservableOnSubscribe(int interval, boolean playAtStart,long remaining) {
        this.interval.set(interval);
        this.remaining = remaining;
        playing.set(playAtStart);
    }

    @Override
    public synchronized void subscribe(ObservableEmitter<Integer> e) throws Exception {
        thread = Thread.currentThread();

        long startTime = System.currentTimeMillis();
        while(subscribed.get()){
            while (playing.get()) {
                try {
                    startTime = System.currentTimeMillis() ;
                    Thread.sleep(remaining);
                    e.onNext(++id);
                    remaining = interval.get();
                } catch (InterruptedException e1) {
                    remaining = remaining
                            - (System.currentTimeMillis() - startTime);
                    break;
                }
            }
            try {
                wait();
            } catch (InterruptedException ie) {}
        }
    }


    public void resume() {
        if(subscribed.get()) {
            playing.set(true);
            thread.interrupt();
        }
    }

    public void pause() {
        playing.set(false);
        thread.interrupt();
    }

    public void stop() {
        subscribed.set(false);
        if(thread!=null) thread.interrupt();
    }

    public long getRemaining() {
        return remaining;
    }

    public void setInterval(int interval){
        this.interval.set(interval);
    }
}