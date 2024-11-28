package ua.orlov.springcoregym.configuration;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class LoginAttemptCacheConfig {

    @Value("${logout.duration.minutes}")
    private int lockoutDurationMinutes;

    @Bean
    public LoadingCache<String, Integer> loginAttemptCache() {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(lockoutDurationMinutes, TimeUnit.MINUTES)
                .maximumSize(1000)
                .build(CacheLoader.from(key -> 0));
    }

}
