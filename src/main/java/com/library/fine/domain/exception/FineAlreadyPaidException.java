package com.library.fine.domain.exception;

import com.library.fine.domain.model.FineId;

public class FineAlreadyPaidException extends RuntimeException {

    public FineAlreadyPaidException(FineId fineId) {
        super("Fine is already paid: " + fineId.getValue());
    }
}
