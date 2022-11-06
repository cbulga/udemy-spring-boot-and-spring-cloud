package com.xantrix.webapp.exception;

import lombok.extern.java.Log;

import javax.naming.AuthenticationException;

@Log
public class JwtTokenMalformedException extends AuthenticationException {

    private static final long serialVersionUID = 3052767780733863241L;

    public JwtTokenMalformedException(String msg) {
        super(msg);
        log.warning(msg);
    }

}
