package com.akjostudios.acsp.common.dto;

import com.akjostudios.acsp.common.model.ResponseStatus;

public interface ExternalServiceResponse {
    ResponseStatus status();
    String error();
}