/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.intake.selectimage;

import com.americanwell.sdk.entity.FileAttachment;
import com.americanwell.sdk.entity.SDKError;

public interface THSFileDownloadCallback {

    void onDocumentDownloadSuccess(FileAttachment fileAttachment, SDKError sdkError);
    void onError(Throwable throwable);

}
