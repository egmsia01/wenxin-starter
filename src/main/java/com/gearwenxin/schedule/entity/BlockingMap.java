package com.gearwenxin.schedule.entity;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class BlockingMap<K, V> {
    @Getter
    private final Map<K, V> map = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();
    private final Condition keyPresent = lock.newCondition();

    public V put(K key, V value) {
        return map.put(key, value);
    }

    public V get(K key) {
            while (!map.containsKey(key)) {
                log.debug("key {{}} not present, waiting...", key);
//                keyPresent.await();
            }
            return map.get(key);

    }

}
