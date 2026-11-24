package com.spring.cloudvendor.exception;

import org.springframework.http.HttpStatus;

public class CloudVendorException {
    private final String massage;
    private final Throwable throwable;
    private final HttpStatus httpStatus;

    public CloudVendorException(String massage, Throwable throwable, HttpStatus httpStatus) {
        this.massage = massage;
        this.throwable = throwable;
        this.httpStatus = httpStatus;
    }

    public String getMassage() {
        return massage;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }


}
