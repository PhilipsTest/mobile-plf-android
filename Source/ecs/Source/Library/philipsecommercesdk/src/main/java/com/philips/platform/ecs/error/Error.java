/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.ecs.error;

public class Error {
    private String message;
    private String reason;
    private String subject;
    private String subjectType;
    private String type;

    public String getMessage() {
        return message;
    }

    public String getReason() {
        return reason;
    }

    public String getSubject() {
        return subject;
    }

    public String getSubjectType() {
        return subjectType;
    }

    public String getType() {
        return type;
    }
}
