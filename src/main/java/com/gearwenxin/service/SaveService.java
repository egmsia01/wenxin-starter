package com.gearwenxin.service;

import com.gearwenxin.entity.Message;

import java.util.Deque;
import java.util.Map;

/**
 * @author Ge Mingjia
 * @date 2023/10/23
 */
public interface SaveService {
    boolean save(Map<String, Deque<Message>> messageMap);
}
