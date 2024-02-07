package com.eatwhere.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

public class ApiException extends Exception {

    public enum ApiExceptionType {
        INITIALIZATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Initalization error"),
        CONFIGURATION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Configuration error"),
        INVALID_INPUT(HttpStatus.BAD_REQUEST, "Invalid input"),
        SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Server error"),
        ACCESS_DENIED(HttpStatus.FORBIDDEN, "Access denied"),
        NOT_FOUND(HttpStatus.NOT_FOUND, "Not found");

        private final HttpStatus code;
        private final String prefix;

        ApiExceptionType(HttpStatus code, String prefix) {
            this.code = code;
            this.prefix = prefix;
        }

        public HttpStatus getCode() {
            return code;
        }

        public String getPrefix() {
            return prefix;
        }
    }

    private static final long serialVersionUID = 1L;

    private ApiExceptionType type;

    public ApiException(ApiExceptionType type) {
        super(type.getPrefix());
        this.type = type;
    }

    public ApiException(ApiExceptionType type, String message) {
        super(StringUtils.isBlank(message) ? type.getPrefix() : type.getPrefix() + ": " + message);
        this.type = type;
    }

    public ApiExceptionType getType() {
        return type;
    }

}
