/* Copyright (c) Koninklijke Philips N.V., 2020

 * All rights are reserved. Reproduction or dissemination

 * in whole or in part is prohibited without the prior written

 * consent of the copyright holder.

 */
package com.philips.platform.mec.screens.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.RecyclerView
import com.philips.platform.mec.databinding.MecReviewRowBinding
import com.philips.platform.mec.screens.reviews.MECReview


class MECReviewsAdapter(private val mecReviews: List<MECReview>?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(MecReviewRowBinding.inflate(LayoutInflater.from(parent.context)))
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = mecReviews?.get(position)
        val viewHolder = holder as ViewHolder
        viewHolder.bind(review!!)

        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        viewHolder.binding.mecRetailerItemProductLayout.setLayoutParams(params)

        if(review.pros==null || review.pros == "null"){
            viewHolder.binding.mecProsLayout.visibility = View.GONE
        } else {
            viewHolder.binding.mecProsLayout.visibility = View.VISIBLE
        }
        if(review.cons==null || review.cons == "null"){
            viewHolder.binding.mecConsLayout.visibility = View.GONE
        } else {
            viewHolder.binding.mecConsLayout.visibility = View.VISIBLE
        }
        if (position % 2 == 0) {
            //viewHolder.binding.root.setBackgroundColor(R.attr.uidContentPrimaryBackgroundColor)
            viewHolder.binding.mecRetailerItemProductLayout.setBackgroundColor(getColor(viewHolder.binding.root.context, com.philips.platform.mec.R.color.uidColorWhite))
        } else {
            //viewHolder.binding.root.setBackgroundColor(R.attr.uidContentSecondaryNeutralBackgroundColor)
            viewHolder.binding.mecRetailerItemProductLayout.setBackgroundColor(Color.parseColor("#F5F5F5"))
        }
        //android:text='@{ (mecReview.submitter+" - "+mecReview.formattedDate): (mecReview.submitter+" - "+mecReview.formattedDate + "@string/mec_has_used_this_product_for"+mecReview.useDuration) }'
      var durationUse :String =review.submitter+" - "+review.getFormattedDate();
       if(review.useDuration.toString()!=null && !review.useDuration.toString().isEmpty()){
           durationUse = durationUse + " - "+viewHolder.binding.mecRetailerItemReviewSubmitter.context.getString(com.philips.platform.mec.R.string.mec_has_used_this_product_for)+" "+review.useDuration.toString();
       }
        viewHolder.binding.mecRetailerItemReviewSubmitter.text=durationUse;
    }

    override fun getItemCount(): Int {
        return mecReviews?.size!!
    }

    private class ViewHolder(val binding: MecReviewRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(review: MECReview) {
            binding.mecReview = review
        }
    }
}
