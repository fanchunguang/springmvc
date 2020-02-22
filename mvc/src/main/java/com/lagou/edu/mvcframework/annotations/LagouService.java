package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LagouService {
    String value() default "";
}
