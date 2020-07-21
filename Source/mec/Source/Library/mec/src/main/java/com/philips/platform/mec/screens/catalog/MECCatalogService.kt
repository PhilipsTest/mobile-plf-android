/*
 *  Copyright (c) Koninklijke Philips N.V., 2020
 *
 *  * All rights are reserved. Reproduction or dissemination
 *
 *  * in whole or in part is prohibited without the prior written
 *
 *  * consent of the copyright holder.
 *
 *
 */

package com.philips.platform.mec.screens.catalog

import android.graphics.drawable.ColorDrawable
import com.philips.platform.uid.view.widget.Label

class MECCatalogService {

    private fun getBackgroundColorOfFontIcon(label: Label): Int {
        val cd: ColorDrawable = label.background as ColorDrawable;
        val colorCode: Int = cd.color;
        return colorCode
    }
}