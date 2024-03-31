package com.gearwenxin.schedule.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Slf4j
public class BlockingMap<K, V> {

    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Map<K, Lock> lockMap = new ConcurrentHashMap<>();
    private final Map<K, Condition> conditionMap = new ConcurrentHashMap<>();

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V putAndNotify(K key, V value) {
        Lock lock = getLockForKey(key);
        lock.lock();
        try {
            V previous = map.put(key, value);
            Condition condition = conditionMap.remove(key);
            if (condition != null) {
                condition.signal();
            }
            return previous;
        } finally {
            lock.unlock();
        }
    }

    public V getAndAwait(K key) {
        Lock lock = getLockForKey(key);
        lock.lock();
        try {
            while (!map.containsKey(key)) {
                Condition condition = getConditionForKey(key);
                condition.await();
            }
            return map.get(key);
        } catch (InterruptedException e) {
            log.error("Interrupted while waiting for key: {}", key);
            Thread.currentThread().interrupt();
            return null;
        } finally {
            lock.unlock();
        }
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

    private Lock getLockForKey(K key) {
        lockMap.putIfAbsent(key, new ReentrantLock());
        return lockMap.get(key);
    }

    private Condition getConditionForKey(K key) {
        conditionMap.putIfAbsent(key, lockMap.get(key).newCondition());
        return conditionMap.get(key);
    }

}
