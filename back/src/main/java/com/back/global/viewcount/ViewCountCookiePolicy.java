package com.back.global.viewcount;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 쿠키 기반 조회수 중복제거 정책.
 * 본 글 ID를 쿠키에 누적하고, 쿠키는 매일 자정에 만료된다.
 * Clock을 주입받아 순수 로직으로 테스트할 수 있다.
 */
@Component
public class ViewCountCookiePolicy {

    public static final String COOKIE_NAME = "viewedPosts";
    private static final String SEPARATOR = "_";

    private final Clock clock;

    public ViewCountCookiePolicy(Clock clock) {
        this.clock = clock;
    }

    public Decision decide(String currentCookieValue, long postId) {
        Set<Long> seen = parse(currentCookieValue);
        boolean shouldIncrement = seen.add(postId);
        String newValue = seen.stream().map(String::valueOf).collect(Collectors.joining(SEPARATOR));
        return new Decision(shouldIncrement, newValue, secondsUntilMidnight());
    }

    private Set<Long> parse(String cookieValue) {
        Set<Long> ids = new LinkedHashSet<>();
        if (cookieValue == null || cookieValue.isBlank()) {
            return ids;
        }

        for (String token : cookieValue.split(SEPARATOR)) {
            if (token.isBlank()) {
                continue;
            }
            try {
                ids.add(Long.parseLong(token.trim()));
            } catch (NumberFormatException ignored) {
                // 손상된 토큰은 무시한다.
            }
        }
        return ids;
    }

    private int secondsUntilMidnight() {
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime nextMidnight = LocalDate.now(clock).plusDays(1).atStartOfDay();
        return (int) Duration.between(now, nextMidnight).getSeconds();
    }

    /**
     * @param shouldIncrement 조회수를 증가시켜야 하는지 여부
     * @param cookieValue     응답에 내려줄 갱신된 쿠키 값
     * @param maxAgeSeconds   쿠키 만료까지 남은 초(자정까지)
     */
    public record Decision(boolean shouldIncrement, String cookieValue, int maxAgeSeconds) {
    }
}
