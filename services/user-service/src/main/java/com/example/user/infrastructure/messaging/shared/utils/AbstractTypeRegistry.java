package com.example.user.infrastructure.messaging.shared.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.example.user.common.error.UserInfrasErrorCode;
import com.example.user.common.exception.InfrastructureException;

public abstract class AbstractTypeRegistry<K, V> {
    
    private final Map<K, V> registry;

    protected AbstractTypeRegistry(
        List<V> components,
        Function<V, K> keyExtractor
    ) {

        if (components == null || components.isEmpty()) {
            this.registry = Map.of();
            return;
        }

        Map<K, V> map = new HashMap<>();

        for (V component : components) {
            K key = keyExtractor.apply(component);

            if (key == null) {
                throw new InfrastructureException(
                    UserInfrasErrorCode.INVALID_COMPONENT_KEY,
                    Map.of(
                        "component", component.getClass().getSimpleName()
                    )
                );
            }

            if (map.containsKey(key)) {
                throw new InfrastructureException(
                    UserInfrasErrorCode.DUPLICATE_COMPONENT,
                    Map.of(
                        "key", key
                    )
                );
            }

            map.put(key, component);
        }

        this.registry = Map.copyOf(map);
    }

    protected V getRequired(K key, String componentRole) {
        
        if (this.registry.isEmpty()) {
            throw new InfrastructureException(
                UserInfrasErrorCode.COMPONENT_REGISTRY_EMPTY
            );
        }

        V component = this.registry.get(key);
        if (component == null) {
            throw new InfrastructureException(
                UserInfrasErrorCode.COMPONENT_NOT_FOUND,
                Map.of(
                    "role", componentRole,
                    "key", key
                )
            );
        }
        
        return component;
    }

}
