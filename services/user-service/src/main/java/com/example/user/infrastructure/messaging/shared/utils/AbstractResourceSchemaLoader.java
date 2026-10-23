package com.example.user.infrastructure.messaging.shared.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.example.user.common.error.UserInfrasErrorCode;
import com.example.user.common.exception.InfrastructureException;

/**
 * Abstract base class for loading and parsing schema
 * resources from the classpath.
 * <p>
 * This class provides a template method for loading
 * a resource as an {@link InputStream} and delegates
 * the parsing logic to subclasses.
 * </p>
 * 
 * @param <T>
 *         the type of schema produced after parsing the resource
 */
public abstract class AbstractResourceSchemaLoader<T> {
    
    /** private string base path of resource */
    private final String basePath;

    /**
     * Creates a new schema loader with the given base resource path.
     * 
     * @param basePath
     *         the base classpath directory where schema resource are located
     */
    protected AbstractResourceSchemaLoader(String basePath) {
        this.basePath = basePath;
    }

    /**
     * Loads and parses a schema resource with the given name.
     *
     * @param schemaName
     *         the name of the schema resource (relative to {@code basePath})
     *
     * @return the parsed schema
     *
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#SCHEMA_NOT_FOUND}
     *         if the schema resource cannot be found on the classpath
     * @throws InfrastructureException with code {@link UserInfrasErrorCode#SCHEMA_LOAD_FAILED}
     *         if an error occurs while loading or parsing the schema
     */
    public final T load(String schemaName) {
        String path = resolvePath(schemaName);

        try (InputStream is = 
                getClass().getClassLoader().getResourceAsStream(path)) {
            
            if (is == null) {
                throw new InfrastructureException(
                    UserInfrasErrorCode.SCHEMA_NOT_FOUND,
                    Map.of(
                        "path", path
                    )
                );
            }

            return this.parse(is);

        } catch (IOException e) {
            throw new InfrastructureException(
                UserInfrasErrorCode.SCHEMA_LOAD_FAILED,
                Map.of(
                    "path", path
                ),
                e
            );
        } finally {
            // Default do nothing
        }
    }
    
    /**
     * Resolves the full classpath location for a given schema name.
     *
     * @param schemaName
     *         the schema resource name
     *
     * @return the resolved classpath resource path
     */
    protected String resolvePath(String schemaName) {
        return basePath + "/" + schemaName;
    }

    /**
     * Parses the schema from the provided {@link InputStream}.
     * <p>
     * Implementations must define how the input stream is converted into
     * the target schema type.
     * </p>
     * 
     * @param inputStream
     *         the input stream of the schema resource
     * 
     * @return the parsed schema
     * 
     * @throws IOException
     *         if parsing fails
     */
    protected abstract T parse(InputStream inputStream) throws IOException;

}
