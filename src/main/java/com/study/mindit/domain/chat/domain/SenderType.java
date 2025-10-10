package com.study.mindit.domain.chat.domain;

public enum SenderType {
    USER,
    AI;
    
    public String toRole() {
        return this == AI ? "assistant" : "user";
    }
}
