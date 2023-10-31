package com.gearwenxin.service;

import com.gearwenxin.entity.Message;

import java.util.Deque;
import java.util.Map;

/**
 * @author Ge Mingjia
 */
public interface SaveService {
    boolean save(Map<String, Deque<Message>> messageMap);
}
