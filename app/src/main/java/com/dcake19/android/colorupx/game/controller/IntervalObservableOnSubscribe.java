package com.dcake19.android.colorupx.game.controller;

import java.util.concurrent.atomic.AtomicBoolean;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class IntervalObservableOnSubscribe implements ObservableOnSubscribe<Integer> {

    private Thread thread;
    private Integer id = 0;
    private AtomicBoolean subscribed = new AtomicBoolean(true);
    private AtomicBoolean playing = new AtomicBoolean(true);
    private int interval;
    private long remaining = 0;

    IntervalObservableOnSubscribe(int interval, boolean playAtStart,long remaining) {
        this.interval = interval;
        this.remaining = remaining;
        playing.set(playAtStart); ;
    }

    @Override
    public void subscribe(ObservableEmitter<Integer> e) throws Exception {
        thread = Thread.currentThread();

        long startTime = System.currentTimeMillis();
        while(subscribed.get()){

            while (playing.get()) {
                try {
                    startTime = System.currentTimeMillis() ;
                    Thread.sleep(remaining);
                    e.onNext(++id);
                    remaining = interval;
                } catch (InterruptedException e1) {
                    remaining = remaining
                            - (System.currentTimeMillis() - startTime);
                    break;
                }
            }
        }
    }


    public void resume() {
        playing.set(true);
    }

    public void pause() {
        playing.set(false);
        thread.interrupt();
    }

    public void stop() {
        subscribed.set(false);
        thread.interrupt();
    }

    public long getRemaining() {
        return remaining;
    }
}