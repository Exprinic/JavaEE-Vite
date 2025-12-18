// BusinessException.java - 业务异常类
package com.exdemix.backend.exception;

public class BusinessException extends RuntimeException {
    
    private final String code;
    private final String message;
    
    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
        this.message = message;
    }
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String getMessage() {
        return message;
    }
}