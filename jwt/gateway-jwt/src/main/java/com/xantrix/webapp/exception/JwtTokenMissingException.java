package com.xantrix.webapp.exception;

import lombok.extern.java.Log;

import javax.naming.AuthenticationException;

@Log
public class JwtTokenMissingException extends AuthenticationException {

    private static final long serialVersionUID = 7314476306512206940L;

    public JwtTokenMissingException(String msg) {
        super(msg);
        log.warning(msg);
    }

}
