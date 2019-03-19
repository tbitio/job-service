package com.easy.jobservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JobException extends Exception {

    public JobException(String message) {
        super(message);
    }
}
