package com.gearwenxin.schedule.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Slf4j
public class BlockingMap<K, V> {

    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Map<K, CountDownLatch> latchMap = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    public synchronized V putAndNotify(K key, V value) {
        V previous = map.put(key, value);
        CountDownLatch latch = latchMap.remove(key);
        if (latch != null) {
            latch.countDown();
        }
        return previous;
    }

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V getAndAwait(K key) {
        while (!map.containsKey(key)) {
            log.debug("[BlockingMap] key {{}} not present, waiting...", key);
            CountDownLatch latch = new CountDownLatch(1);
            latchMap.put(key, latch);
            try {
                latch.await();
            } catch (InterruptedException e) {
                log.error("latch await error", e);
            }
        }
        return map.get(key);
    }

    public V get(K key) {
        return map.get(key);
    }

    public V get(K key, boolean delete) {
        if (delete) {
            return map.remove(key);
        }
        return map.get(key);
    }

}
