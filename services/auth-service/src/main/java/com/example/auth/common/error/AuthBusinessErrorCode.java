package com.example.auth.common.error;

/**
 * Enumeration of business-level error codes for the Auth Service.
 * 
 * <p>
 * These error codes represent domain and validation
 * failures that occur during authentication-related
 * use cases (e.g. register, login).
 * </p>
 * 
 * <p>
 * <b>This enum is intended to be</b>:
 * <ul>
 *   <li>
 *     Returned to clients (via REST response)
 *   </li>
 *   <li>
 *     Used by frontend application to decide UI behavior
 *   </li>
 *   <li>
 *     Stable and versioned as part of the public API contract
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * <i>Important</i>:
 * <ul>
 *   <li>
 *     This enum does NOT represent infrastructure errors
 *     (e.g. database down, network failure).
 *   </li>
 *   <li>
 *     This enum does NOT represent security-layer errors
 *     (e.g. UNAUTHORIZED, FORBIDDEN) handled by Gateway / Security.
 *   </li>
 *   <li>
 *     Only business / domain-related error should be defined here.
 *   </li>
 * </ul>
 * </p>
 * 
 * <p>
 * Example REST error response:
 * <pre>
 * {
 *   "code": "INVALID_CREDENTIALS",
 *   "message": "Authentication failed, password does not match",
 *   "traceId": "abc-123"
 * }
 * </pre>
 * </p>
 */
public enum AuthBusinessErrorCode implements ErrorCode {

    /**
     * Generic internal business error.
     * 
     * <p>
     * Should be used only when a specific error
     * cannot be determined.
     * </p>
     */
    INTERNAL(
        "INTERNAL",
        "Unexpected business error occurred - AuthBusinessErrorCode"
    ),

    // ----- REGISTER -----

    /**
     * Indicates that the register command payload
     * does not satisfy validation constrants.
     */
    INVALID_REGISTER_COMMAND(
        "INVALID_REGISTER_COMMAND",
        "The register command is not as required"
    ),
    
    /**
     * Indicates that a user with the same identifier
     * (e.g. email or username) already exists.
     */
    USER_ALREADY_EXISTS(
        "USER_ALREADY_EXISTS",
        "User already exists in the database"
    ),
    // ----- -----

    // ----- LOGIN -----
    
    /**
     * Indicates that the login command payload
     * does not satisfy validation constraints.
     */
    INVALID_LOGIN_COMMAND(
        "INVALID_LOGIN_COMMAND",
        "The login command is not as required"
    ),

    /**
     * Indicates that authentication failed due to
     * incorrect credentials (e.g. wrong password).
     */
    INVALID_CREDENTIALS(
        "INVALID_CREDENTIALS",
        "Authentication failed, password does not match"
    );
    // ----- -----

    private final String code;
    private final String message;

    /**
     * Constructs a business error code with the given code and message.
     *
     * @param code unique error identifier
     * @param message descriptive error message
     */
    private AuthBusinessErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Returns the unique error code.
     *
     * @return error code as a string
     */
    public String code() {
        return this.code;
    }

    /**
     * Returns the human-readable error message.
     *
     * @return error message
     */
    public String message() {
        return this.message;
    }

}
