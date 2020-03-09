package com.philips.cdp.di.mec.screens.detail

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bazaarvoice.bvandroidsdk.*
import com.philips.cdp.di.mec.R
import com.philips.cdp.di.mec.analytics.MECAnalytics
import com.philips.cdp.di.mec.analytics.MECAnalyticsConstant

import com.philips.cdp.di.mec.databinding.MecProductReviewFragmentBinding
import com.philips.cdp.di.mec.screens.MecBaseFragment
import com.philips.cdp.di.mec.screens.reviews.MECReview
import com.philips.cdp.di.mec.utils.MECConstant
import kotlinx.android.synthetic.main.mec_product_review_fragment.*
import android.content.Intent
import android.net.Uri


/**
 * A simple [Fragment] subclass.
 */
class MECProductReviewsFragment : MecBaseFragment() {

    override fun getFragmentTag(): String {
        return "MECProductReviewsFragment"
    }


    private var productctn: String? = null
    var offset: Int = 0
    var limit: Int = 20
    var totalReview: Int = 0

    private lateinit var ecsProductDetailViewModel: EcsProductDetailViewModel
    private val reviewObserver : Observer<ReviewResponse> = object : Observer<ReviewResponse> {

        override fun onChanged(reviewResponse:  ReviewResponse?) {

            val reviews = reviewResponse?.results

            totalReview = reviewResponse?.totalResults ?: 0
                for (review in reviews!!) {
                    val nick = if (review.userNickname != null) review.userNickname else getString(R.string.mec_anonymous)

                    mecReviews.add(MECReview(review.title, review.reviewText, review.rating.toString(), nick, review.lastModificationDate, ecsProductDetailViewModel.getValueFor("Pros", review), ecsProductDetailViewModel.getValueFor("Cons", review), ecsProductDetailViewModel.getValueForUseDuration(review)))

            }

            if(mecReviews.size>0){
                binding.mecProductReviewEmptyLabel.visibility = View.GONE
                binding.mecBvTrustmark.visibility = View.VISIBLE
            } else{
                binding.mecProductReviewEmptyLabel.visibility = View.VISIBLE
                binding.mecBvTrustmark.visibility = View.GONE
            }
            reviewsAdapter!!.notifyDataSetChanged()
            binding.mecProgressLayout.visibility = View.GONE
        }

    }

    private var reviewsAdapter: MECReviewsAdapter? = null
    private lateinit var mecReviews: MutableList<MECReview>

    private lateinit var binding: MecProductReviewFragmentBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = MecProductReviewFragmentBinding.inflate(inflater, container, false)

        ecsProductDetailViewModel = this.let { ViewModelProviders.of(it).get(EcsProductDetailViewModel::class.java) }

        ecsProductDetailViewModel.review.observe(this, reviewObserver)
        ecsProductDetailViewModel.mecError.observe(this,this)

        mecReviews = mutableListOf<MECReview>()

        val bundle = arguments
        productctn = bundle!!.getString(MECConstant.MEC_PRODUCT_CTN,"INVALID")

        //TODO in binding
        reviewsAdapter = MECReviewsAdapter(mecReviews)
        binding.recyclerView.adapter = reviewsAdapter
        binding.recyclerView.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

            this!!.productctn?.let { ecsProductDetailViewModel.getBazaarVoiceReview(it, offset, limit) }

        bazaarvoiceLink(binding.mecBazaarvoiceLink)

        binding.mecNestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                val lay = binding.recyclerView
                        .layoutManager as LinearLayoutManager

                if(isScrollDown(lay))
                    if (isAllFetched()) {
                        executeRequest()
                    }
            }
        })

        return binding.root
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if(isVisibleToUser) {
            productctn?.let { tagActions(it) }
        }
    }

    private fun bazaarvoiceLink(view: TextView) {
        val spanTxt = SpannableStringBuilder(
                getString(R.string.mec_bazaarVoice_Terms_And_Condition))
        spanTxt.append("\n"+getString(R.string.mec_bazaarVoice_Detail_at))
        spanTxt.append(" ")
        spanTxt.append(getString(R.string.mec_bazaarVoice_link))
        spanTxt.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val uri = Uri.parse(getString(R.string.mec_bazaarVoice_link))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }

            override fun updateDrawState(ds: TextPaint) {
                ds.isUnderlineText = true
                ds.color = R.attr.uidHyperlinkDefaultPressedTextColor
            }
        }, spanTxt.length - getString(R.string.mec_bazaarVoice_link).length, spanTxt.length, 0)
        binding.mecBazaarvoiceLink.setHighlightColor(Color.TRANSPARENT)
        view.movementMethod = LinkMovementMethod.getInstance()
        view.setText(spanTxt, TextView.BufferType.SPANNABLE)
    }

    private fun tagActions(ctn : String) {
        var map = HashMap<String, String>()
        map.put(MECAnalyticsConstant.specialEvents, MECAnalyticsConstant.userReviewsViewed)
        map.put(MECAnalyticsConstant.mecProducts,ctn)
        MECAnalytics.trackMultipleActions(MECAnalyticsConstant.sendData,map)
    }

    private fun isAllFetched() = totalReview != 0 && reviewsAdapter!!.itemCount < totalReview


    private fun executeRequest() {
        binding.mecProgressLayout.visibility = View.VISIBLE
        offset += limit
        this!!.productctn?.let { ecsProductDetailViewModel.getBazaarVoiceReview(it,offset,limit) }
    }

    private fun isScrollDown(lay: LinearLayoutManager): Boolean {
        val visibleItemCount = lay.childCount
        val firstVisibleItemPosition = lay.findFirstVisibleItemPosition()
        return visibleItemCount + firstVisibleItemPosition >= lay.itemCount && firstVisibleItemPosition >= 0
    }

}
