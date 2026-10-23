package com.example.auth.infrastructure.security.jwt.claims;

public final class JwtClaimNames {
    
    private JwtClaimNames() {}

    // Json Web Token registered claims
    public static final String ISSUER = "iss";
    public static final String SUBJECT = "sub";
    public static final String ISSUED_AT = "iat";
    public static final String EXPIRES_AT = "exp";
    public static final String AUDIENCE = "aud";

    // Platform claims (Optional)

}
