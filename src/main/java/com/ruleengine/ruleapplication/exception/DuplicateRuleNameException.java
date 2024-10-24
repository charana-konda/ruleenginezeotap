package com.ruleengine.ruleapplication.exception;


public class DuplicateRuleNameException extends RuntimeException {
    public DuplicateRuleNameException(String message) {
        super(message);
    }
}
