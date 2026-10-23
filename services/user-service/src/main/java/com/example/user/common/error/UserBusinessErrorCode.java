package com.example.user.common.error;

/**
 * Enumeration of business-level error codes for the Auth Service.
 * 
 * <p>
 * These error codes represent domain and validation
 * failures that occur during user-related
 * use cases (e.g. profile, update profile).
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
 *   "code": "USER_NOT_FOUND",
 *   "message": "User is invalid",
 *   "traceId": "abc-123"
 * }
 * </pre>
 * </p>
 */
public enum UserBusinessErrorCode implements ErrorCode {
    
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
        "Unexpected business error occurred - UserBusinessErrorCode"
    ),

    // ----- USER -----
    USER_NOT_FOUND(
        "USER_NOT_FOUND",
        "User is invalid"
    ),
    // ----- -----
    ;

    private final String code;
    private final String message;

    /**
     * Constructs a business error code with the given code and message.
     *
     * @param code unique error identifier
     * @param message descriptive error message
     */
    private UserBusinessErrorCode(String code, String message) {
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
