package it.polyatskovun.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    @CacheEvict(value = {"product", "products"}, allEntries = true)
    public void clearCache() {
        log.debug("Cache cleared.");
    }
}
