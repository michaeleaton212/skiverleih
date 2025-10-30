package skirental;

// Simple runtime exception for application-level errors.
// CleanCode: small, explicit exception type; single responsibility.
public class AppException extends RuntimeException {
    public AppException(String m){ super(m); }
}
