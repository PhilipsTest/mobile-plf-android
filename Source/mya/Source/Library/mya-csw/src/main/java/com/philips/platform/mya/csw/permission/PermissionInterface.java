/*
 * Copyright (c) 2017 Koninklijke Philips N.V.
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.mya.csw.permission;

import com.philips.platform.mya.csw.dialogs.ConfirmDialogView;

public interface PermissionInterface {

    void showProgressDialog();

    void hideProgressDialog();

    void showErrorDialog(boolean goBack, String title, String message);

    void showConfirmRevokeConsentDialog(ConfirmDialogView dialog, ConfirmDialogView.ConfirmDialogResultHandler handler);
}
