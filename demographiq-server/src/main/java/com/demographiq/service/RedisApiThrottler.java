package com.demographiq.service;

import redis.clients.jedis.UnifiedJedis;
import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

public class RedisApiThrottler {
    private static UnifiedJedis jedis;
    private static String serverGlobalCallsKey = "globalCalls";
    private static final int MAX_USER_CALLS_MINUTE = 3;
    private static final int MAX_SERVER_CALLS_MINUTE = 5;

    public static void getConnection() {
        if (jedis == null) {
            Dotenv dotenv = Dotenv.load();
            String password = dotenv.get("Redis_PASSWORD");
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                    .user("default")
                    .password(password)
                    .build();

            jedis = new UnifiedJedis(
                new HostAndPort("redis-10783.c90.us-east-1-3.ec2.redns.redis-cloud.com", 10783),
                config
            );
        }
    }

    public static boolean registerApiCall(String userKey) {
        getConnection();
        boolean userResponse = registerUserApiCall(userKey);
        boolean serverResponse = registerServerApiCall();
        if (userResponse == false || serverResponse == false) {
            return false; // API call limit exceeded
        } else {
            return true; // API call allowed
        }
    }

    private static boolean registerUserApiCall(String userKey) {
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

    private static boolean registerServerApiCall() {
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