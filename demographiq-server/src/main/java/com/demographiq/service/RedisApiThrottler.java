package com.demographiq.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.UnifiedJedis;

@Service
public class RedisApiThrottler {
    private UnifiedJedis jedis;
    private String serverGlobalCallsKey = "globalCalls";
    private final int MAX_USER_CALLS_MINUTE = 3;
    private final int MAX_SERVER_CALLS_MINUTE = 5;

    @Value("${Redis_PASSWORD}")
    private String redisPassword;

    public void getConnection() {
        if (jedis == null) {
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                    .user("default")
                    .password(redisPassword)
                    .build();

            jedis = new UnifiedJedis(
                new HostAndPort("redis-10783.c90.us-east-1-3.ec2.redns.redis-cloud.com", 10783),
                config
            );
        }
    }

    public boolean registerApiCall(String userKey) {
        this.getConnection();
        boolean userResponse = registerUserApiCall(userKey);
        boolean serverResponse = registerServerApiCall();
        if (userResponse == false || serverResponse == false) {
            return false; // API call limit exceeded
        } else {
            return true; // API call allowed
        }
    }

    private boolean registerUserApiCall(String userKey) {
        long userUsageCount = jedis.incr(userKey);
        System.out.println("API calls made so far by user " + userKey + " past minute " + userUsageCount);
        if (userUsageCount == 1) {
            jedis.expire(userKey, 60);
        }
        if (userUsageCount > MAX_USER_CALLS_MINUTE) {
            System.out.println("API call limit exceeded for user " + userKey);
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