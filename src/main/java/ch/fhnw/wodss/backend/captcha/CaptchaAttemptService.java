package ch.fhnw.wodss.backend.captcha;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Service
public class CaptchaAttemptService {
    private int MAX_ATTEMPTS = 3;
    private LoadingCache<String, Integer> attemptsCache;
 
    public CaptchaAttemptService() {
        attemptsCache = CacheBuilder.newBuilder()
          .expireAfterWrite(4, TimeUnit.HOURS).build(new CacheLoader<String, Integer>() {
            @Override
            public Integer load(String key) {
                return 0;
            }
        });
    }
 
    public void reCaptchaSucceeded(String key) {
        attemptsCache.invalidate(key);
    }
 
    public void reCaptchaFailed(String key) {
        int attempts = attemptsCache.getUnchecked(key);
        attempts++;
        attemptsCache.put(key, attempts);
    }
 
    public boolean isBlocked(String key) {
        return attemptsCache.getUnchecked(key) >= MAX_ATTEMPTS;
    }
}
