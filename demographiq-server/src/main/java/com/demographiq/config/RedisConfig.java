package com.demographiq.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import redis.clients.jedis.Connection;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPooled;

/**
 * Spring Configuration class for setting up the Redis client with connection pooling.
 */
@Configuration
public class RedisConfig {
    @Value("${redis.host}")
    private String redisHost;

    @Value("${redis.port}")
    private int redisPort;

    @Value("${redis.user}")
    private String redisUser;

    @Value("${Redis_PASSWORD}")
    private String redisPassword;

    @Value("${redis.pool.maxTotal}")
    private int maxTotal;

    @Value("${redis.pool.maxIdle}")
    private int maxIdle;

    @Value("${redis.pool.minIdle}")
    private int minIdle;


    /**
     * Defines a singleton JedisPooled bean with connection pooling.
     * Spring will manage the lifecycle of this client and its connection pool.
     *
     * @return The configured JedisPooled instance.
     */
    @Bean
    public JedisPooled jedisPooled() {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .user(redisUser)
                .password(redisPassword)
                .build();
        
        GenericObjectPoolConfig<Connection> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        
        HostAndPort hostAndPort = new HostAndPort(redisHost, redisPort);

        return new JedisPooled(poolConfig, hostAndPort, clientConfig);
    }

}
