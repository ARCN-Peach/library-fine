package com.library.fine.domain.exception;

import com.library.fine.domain.model.FineId;

public class FineNotFoundException extends RuntimeException {

    public FineNotFoundException(FineId fineId) {
        super("Fine not found with id: " + fineId.getValue());
    }
}
