package org.jahia.modules.npm-modules-engine.helpers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identify the annotated member as a reference of an OSGI Service.
 *
 * <p>
 * When the annotation is applied to a method, the method is the bind method of
 * the reference. When the annotation is applied to a field, the field will
 * contain the bound service(s) of the reference.
 *
 * <p>
 * This annotation is processed at runtime by {@link OSGiServiceInjector}.
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OSGiService {

    /**
     * The type of the service for this reference.
     *
     * <p>
     * If not specified, the type of the service for this reference is based
     * upon how this annotation is used:
     * <ul>
     * <li>Annotated method - The type of the service is the type of the first
     * argument of the method.</li>
     * <li>Annotated field - The type of the service is the
     * type of the field.</li>
     * </ul>
     */
    Class<?> service() default Object.class;

    /**
     * The OSGI filter for this reference.
     *
     * <p>
     * If not specified, no filter is used for service lookup.
     */
    String filter() default "";
}