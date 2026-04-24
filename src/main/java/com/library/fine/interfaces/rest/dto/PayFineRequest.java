package com.library.fine.interfaces.rest.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record PayFineRequest(@NotNull UUID fineId) {}
