package com.eqan.web.exceptions;

public class NotAuthorizedException extends SecurityException{

    private static final long serialVersionUID = 957942354670424275L;
    
    public NotAuthorizedException() {
        super("User could not be authenticated");
    }
    
    public NotAuthorizedException(String message) {
        super(message);
    }
    
}
