package com.gearwenxin.schedule;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockingMap<K, V> {
    @Getter
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition keyPresent = lock.newCondition();

    public V put(K key, V value) {
        lock.lock();
        try {
            V putValue = map.put(key, value);
            keyPresent.signalAll();
            return putValue;
        } finally {
            lock.unlock();
        }
    }

    public V get(K key) {
        lock.lock();
        try {
            while (!map.containsKey(key)) {
                keyPresent.await();
            }
            return map.get(key);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

}
