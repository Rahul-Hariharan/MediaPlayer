package com.example.a539117.sampleapplication;

import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by rahul on 21-06-2016.
 */
public class ScheduledExecutor extends ScheduledThreadPoolExecutor {
    public ScheduledExecutor(int corePoolSize) {
        super(corePoolSize);
    }
}
