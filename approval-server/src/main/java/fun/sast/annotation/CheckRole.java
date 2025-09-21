package fun.sast.annotation;

import fun.sast.enums.UserRoleEnum;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明接口或类权限
 * @author NuoTian
 * @date 2022/8/18
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    @AliasFor("role")
    UserRoleEnum value() default UserRoleEnum.STUDENT;
    @AliasFor("value")
    UserRoleEnum role() default UserRoleEnum.STUDENT;
}
