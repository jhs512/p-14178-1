package com.back.global.viewcount;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class ViewCountCookiePolicyTest {

    private static final ZoneId ZONE = ZoneId.systemDefault();

    private ViewCountCookiePolicy policyAt(LocalDateTime now) {
        Clock fixed = Clock.fixed(now.atZone(ZONE).toInstant(), ZONE);
        return new ViewCountCookiePolicy(fixed);
    }

    @Test
    void 신규_글_첫_조회는_증가한다() {
        ViewCountCookiePolicy policy = policyAt(LocalDateTime.of(2026, 5, 28, 10, 0));

        ViewCountCookiePolicy.Decision d = policy.decide(null, 5L);

        assertThat(d.shouldIncrement()).isTrue();
        assertThat(d.cookieValue()).isEqualTo("5");
    }

    @Test
    void 같은_날_같은_글_재조회는_증가하지_않는다() {
        ViewCountCookiePolicy policy = policyAt(LocalDateTime.of(2026, 5, 28, 10, 0));

        ViewCountCookiePolicy.Decision d = policy.decide("5", 5L);

        assertThat(d.shouldIncrement()).isFalse();
        assertThat(d.cookieValue()).isEqualTo("5");
    }

    @Test
    void 다른_글_조회는_증가하고_쿠키에_누적된다() {
        ViewCountCookiePolicy policy = policyAt(LocalDateTime.of(2026, 5, 28, 10, 0));

        ViewCountCookiePolicy.Decision d = policy.decide("5", 7L);

        assertThat(d.shouldIncrement()).isTrue();
        assertThat(d.cookieValue()).contains("5").contains("7");
    }

    @Test
    void 자정_경과로_쿠키가_만료되면_같은_글도_다시_증가한다() {
        // 자정이 지나 브라우저가 쿠키를 보내지 않는 상황 = null
        ViewCountCookiePolicy policy = policyAt(LocalDateTime.of(2026, 5, 29, 0, 1));

        ViewCountCookiePolicy.Decision d = policy.decide(null, 5L);

        assertThat(d.shouldIncrement()).isTrue();
    }

    @Test
    void 쿠키_만료시간은_자정까지_남은_초이다() {
        ViewCountCookiePolicy policy = policyAt(LocalDateTime.of(2026, 5, 28, 23, 0));

        ViewCountCookiePolicy.Decision d = policy.decide(null, 1L);

        assertThat(d.maxAgeSeconds()).isEqualTo(3600);
    }
}
