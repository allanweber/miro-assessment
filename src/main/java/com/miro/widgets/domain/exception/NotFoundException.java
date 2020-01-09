package com.miro.widgets.domain.exception;

import java.util.UUID;

public class NotFoundException extends RuntimeException {

    private static final long serialVersionUID = 4627241210413401911L;

    public NotFoundException(UUID id) {
        super(String.format("Widget with id %s not found", id));
    }
}
