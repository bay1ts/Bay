package com.bay1ts.bay.core;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

public class HaltException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int statusCode = HttpResponseStatus.OK.code();
    private String body = null;

    HaltException() {
        super();
    }

    HaltException(int statusCode) {
        this.statusCode = statusCode;
    }

    HaltException(String body) {
        this.body = body;
    }

    HaltException(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * @return the statusCode
     * @deprecated replaced by {@link #statusCode()}
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return the statusCode
     */
    public int statusCode() {
        return statusCode;
    }

    /**
     * @return the body
     * @deprecated replaced by {@link #body()}
     */
    public String getBody() {
        return body;
    }

    /**
     * @return the body
     */
    public String body() {
        return body;
    }

}