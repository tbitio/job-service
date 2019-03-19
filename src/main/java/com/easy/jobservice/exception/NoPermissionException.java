package com.easy.jobservice.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NoPermissionException extends Exception {

    public NoPermissionException(String message) {
        super(message);
    }
}
