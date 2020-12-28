package org.noear.solon.extend.springboot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author noear 2020/12/28 created
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(ConfigurationSolon.class)
@Documented
public @interface EnableSolon {
}
