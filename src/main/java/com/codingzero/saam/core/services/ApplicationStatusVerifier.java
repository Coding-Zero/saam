package com.codingzero.saam.core.services;

import com.codingzero.saam.common.ApplicationStatus;
import com.codingzero.saam.common.Errors;
import com.codingzero.saam.core.Application;
import com.codingzero.utilities.error.BusinessError;

public class ApplicationStatusVerifier {

    public void checkForDeactiveStatus(Application application) {
        if (application.getStatus() == ApplicationStatus.DEACTIVE) {
            throw BusinessError.raise(Errors.INVALID_STATUS)
                    .message("No operations allowed for inactive application.")
                    .details("entity", Application.class.getSimpleName())
                    .details("id", application.getId())
                    .details("status", application.getStatus())
                    .build();
        }
    }

}
