package com.yousails.chrenai.thread;

import android.os.Process;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:WangKunHui
 * Date: 2017/7/13 11:29
 * Desc:
 * E-mail:life_artist@163.com
 */
public class PriorityThreadFactory implements ThreadFactory {

    private int mThreadPriority = Process.THREAD_PRIORITY_BACKGROUND;
    private final AtomicInteger mCount = new AtomicInteger(1);

    public PriorityThreadFactory() {
    }

    public PriorityThreadFactory(int threadPriority) {
        this.mThreadPriority = threadPriority;
    }

    @SuppressWarnings("NullableProblems")
    public Thread newThread(final Runnable runnable) {
        return newThread(runnable, "PriorityThreadFactory#" + mCount.getAndIncrement());
    }

    public Thread newThread(final Runnable runnable, final String name) {
        Runnable wrapperRunnable = new Runnable() {
            public void run() {
                try {
                    Process.setThreadPriority(PriorityThreadFactory.this.mThreadPriority);
                } catch (Throwable e) {
                }

                runnable.run();
            }
        };
        return new Thread(wrapperRunnable, name);
    }
}
