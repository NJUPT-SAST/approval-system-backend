package fun.sast.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import fun.sast.enums.UserRoleEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

/**
 * 声明接口或类权限
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    @AliasFor("role")
    UserRoleEnum value() default UserRoleEnum.STUDENT;
    @AliasFor("value")
    UserRoleEnum role() default UserRoleEnum.STUDENT;
}

