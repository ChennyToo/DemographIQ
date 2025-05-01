package com.demographiq.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisPooled;

@Service
public class RedisApiThrottler {
    private final JedisPooled jedis;
    private final String serverGlobalCallsKey = "globalCalls";
    private final int MAX_USER_CALLS_MINUTE = 10;
    private final int MAX_SERVER_CALLS_MINUTE = 10;

    @Autowired //Dependency inject jedisPooled from RedisConfig.java
    public RedisApiThrottler(JedisPooled jedisPooled) {
        this.jedis = jedisPooled;
    }

    public boolean registerApiCall(Integer userId) {
        boolean userResponse = registerUserApiCall(userId);
        boolean serverResponse = registerServerApiCall();
        //Return true if both user and server limits are not exceeded, indicating both are allowed
        return (userResponse == true && serverResponse == true);
    }

    private boolean registerUserApiCall(Integer userId) {
        String userUsageKey = "user(minute):" + userId;
        long userUsageCount = jedis.incr(userUsageKey);
        System.out.println("API calls made so far by user " + userId + " past minute " + userUsageCount);
        if (userUsageCount == 1) {
            jedis.expire(userUsageKey, 60);
        }
        if (userUsageCount > MAX_USER_CALLS_MINUTE) {
            System.out.println("API call limit exceeded for user " + userId);
            return false;
        } else {
            return true;
        }
    }

    private boolean registerServerApiCall() {
        long globalUsageCount = jedis.incr(serverGlobalCallsKey);
        System.out.println("API calls made so far by server past minute " + globalUsageCount);
        if (globalUsageCount == 1) {
            jedis.expire(serverGlobalCallsKey, 60);
        }
        if (globalUsageCount > MAX_SERVER_CALLS_MINUTE) {
            System.out.println("API call limit exceeded for server");
            return false;
        } else {
            return true;
        }
    }
}