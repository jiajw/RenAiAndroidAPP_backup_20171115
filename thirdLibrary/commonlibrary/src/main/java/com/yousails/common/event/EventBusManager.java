package com.yousails.common.event;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author:WangKunHui
 * Date: 2017/7/12 13:10
 * Desc: EventBus事件工具类
 * E-mail:life_artist@163.com
 */
public class EventBusManager {

    private static EventBusManager mEventBusManager;


    public static synchronized EventBusManager getInstance() {
        if (mEventBusManager == null) {
            mEventBusManager = new EventBusManager();
        }
        return mEventBusManager;
    }

    private EventBus mDefaultEventBus;

    /**
     * {@value}
     */
    public static final int DEFAULT_THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors() * 2 >= 10 ? 10 : Runtime.getRuntime().availableProcessors() * 2;
    /**
     * {@value}
     */
    public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
    private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
    private int threadPriority = DEFAULT_THREAD_PRIORITY;

    /**
     * Creates default implementation of task executor
     */
    public static ThreadPoolExecutor createExecutor(int threadPoolSize, int threadPriority) {
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        return new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0L, TimeUnit.MILLISECONDS, taskQueue,
                createThreadFactory(threadPriority, "event-pool-"));
    }

    /**
     * Creates default implementation of {@linkplain ThreadFactory com.yousails.chrenai.thread factory} for task executor
     */
    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            group = Thread.currentThread().getThreadGroup();
            namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-com.yousails.chrenai.thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) t.setDaemon(false);
            t.setPriority(threadPriority);
            return t;
        }
    }

    public void register(Object subscriber) {
        if (!this.getEventBus().isRegistered(subscriber)) {
            this.getEventBus().register(subscriber);
        }
    }

    public void unregister(Object subscriber) {
        try {
            if (this.getEventBus().isRegistered(subscriber)) {
                this.getEventBus().unregister(subscriber);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void post(Object event) {
        getEventBus().post(event);
    }

    public EventBus getEventBus() {
        if (mDefaultEventBus == null) {
            mDefaultEventBus = EventBus.builder().executorService(createExecutor(threadPoolSize, threadPriority)).build();
        }
        return mDefaultEventBus;
    }

}
