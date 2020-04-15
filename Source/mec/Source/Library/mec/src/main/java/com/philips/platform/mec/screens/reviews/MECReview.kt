/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.reviews

import java.text.SimpleDateFormat
import java.util.*

class MECReview(val title:String , val reviewText:String, val rating:String,val submitter:String,val date:Date, val pros:String,val cons:String, val useDuration:String) {


     fun getFormattedDate(): String? {
         val formateDate = SimpleDateFormat("dd MMM yyyy").format(date)
         return formateDate.toString()
         }
    }