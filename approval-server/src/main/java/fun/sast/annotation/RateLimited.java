package fun.sast.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    /** 桶内容量（最多允许的请求数） */
    long capacity() default 5;

    /** 每次往桶里加的令牌数量 */
    long refillTokens() default 5;

    /** 时间窗口，单位：秒,往桶里加一次令牌的时间 */
    long refillPeriodSeconds() default 60;

    /** 限流 key（不同接口可自定义） */
    String key() default "";
}
