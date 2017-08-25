package com.eqan.web.security;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcryptPasswordHashing {
    private static final Logger LOG = LoggerFactory.getLogger(BcryptPasswordHashing.class);
    private static final int WORKLOAD = 12;

    public static boolean checkPassword(String plaintextPassword, String hashedPassword) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Checking hashed password");
        }
        return BCrypt.checkpw(plaintextPassword, hashedPassword);
    }
    
    public static String hashPassword(String plaintextPassword) {
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Hashing password with workload {}", WORKLOAD);
        }
        String salt = BCrypt.gensalt(WORKLOAD);
        return BCrypt.hashpw(plaintextPassword, salt);
    }
}
