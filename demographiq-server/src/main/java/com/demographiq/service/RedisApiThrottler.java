package com.demographiq.service;

import redis.clients.jedis.UnifiedJedis;
import io.github.cdimascio.dotenv.Dotenv;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;

public class RedisApiThrottler {
    private static UnifiedJedis jedis;

    public static UnifiedJedis getConnection() {
        Dotenv dotenv = Dotenv.load();
        String password = dotenv.get("Redis_PASSWORD");
        if (jedis == null) {
            JedisClientConfig config = DefaultJedisClientConfig.builder()
                    .user("default")
                    .password(password)
                    .build();

            jedis = new UnifiedJedis(
                new HostAndPort("redis-10783.c90.us-east-1-3.ec2.redns.redis-cloud.com", 10783),
                config
            );
        }
        return jedis;
    }

    public static void closeConnection() {
        if (jedis != null) {
            jedis.close();
        }
    }
}