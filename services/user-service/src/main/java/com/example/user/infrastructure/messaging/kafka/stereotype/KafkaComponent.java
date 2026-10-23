package com.example.user.infrastructure.messaging.kafka.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Stereotype annotation that marks a Spring bean as 
 * part of the Kafka messaging infrastructure.
 * 
 * <p>
 * This annotation serves two purposes:
 * <ul>
 *   <li>
 *     <b>Semantic marker</b>: indicates that the annotated types belongs to 
 *     the Kafka-based messaging layer (e.g. Kafka consumers, producer, etc.).
 *   </li>
 *   <li>
 *     <b>Spring DI integration</b>: acts as a meta-annotation for 
 *     {@link Component} and {@link Qualifier}, allowing Spring to automatically
 *     register the bean and select it during dependency injection.
 *   </li>
 * </ul>
 * </p>
 * 
 * <h2>Annotation composition</h2>
 * 
 * <h3>{@link Target}</h3>
 * <p>
 * {@code @Target(ElementType.TYPE)} restricts the usage of this annotation
 * to type-level elements only (classes, interfaces, enums).
 * This reflects the architectural intent that Kafka infrastructure elements
 * are modeled as Spring Beans, not as methods, fields, or parameters.
 * </p>
 * 
 * <h3>{@link Retention}</h3>
 * <p>
 * {@code @Retention(RetentionPolicy.RUNTIME)} ensures that this annotation
 * is retained at runtime, allowing the Spring container to inspect it
 * during component scanning and dependency injection.
 * </p>
 * 
 * <h3>{@link Documented}</h3>
 * <p>
 * {@code @Documented} indicates that the presence of this annotation should
 * be included in the generated Javadoc of annotated types, making its
 * architectural role explicit to future maintainers.
 * </p>
 * 
 * <h3>{@link Component}</h3>
 * <p>
 * {@code @Component} marks the annotated types as a Spring-managed bean.
 * Any class annotated with {@code @KafkaComponent} will be automatically
 * detected during component scanning and registered in the application context.
 * </p>
 * 
 * <h3>{@link Qualifier}</h3>
 * <p>
 * {@code @Qualifier(KafkaComponent.NAME)} assigns a logical qualifier to
 * the bean, enabling Spring to distinguish Kafka-related components from
 * other beans of the same type during dependency injection.
 * </p>
 * 
 * <p>
 * The qualifier is intentionally encapsulated within this stereotype
 * annotation to avoid scattering string-based qualifiers across the
 * codebase and to keep Kafka-related wiring consistent and explicit.
 * </p>
 * 
 * <h2>Qualifier constant</h2>
 * <p>
 * The {@link #NAME} constant defines the canonical qualifier value associated
 * with Kafka infrastructure components. Centralizing this value:
 * <ul>
 *   <li>avoids magic strings,</li>
 *   <li>prevents accidental mismatches during wiring,</li>
 *   <li>provides a single source of truth for Kafka-related bean selection.</li>
 * </ul>
 * </p>
 * 
 * <h2>Usage example</h2>
* <pre>{@code
 * @KafkaComponent
 * public class UserEventKafkaMapper
 *         implements EventMapper<UserEvent, UserMessage> {
 *     // Implement mapping methods here
 * }
 *
 * @Bean
 * public EventMapperRegistry kafkaMapperRegistry(
 *     @Qualifier(KafkaComponent.NAME)
 *     List<EventMapper<?, ?>> kafkaMappers
 * ) {
 *     return new EventMapperRegistry(kafkaMappers);
 * }
 * }</pre>
 * <p>
 * This design keeps the architectural meaning (Kafka infrastructure)
 * and the dependency injection mechanism (qualifier-based selection)
 * aligned while remaining fully Spring-native.
 * </p>
 * 
 * @see Component
 * @see Qualifier
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Qualifier(KafkaComponent.NAME)
public @interface KafkaComponent {
    String NAME = "kafka";
}
