package com.philips.cdp.registration.ads.views;

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.RawRes
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.palette.graphics.Palette
import com.philips.cdp.registration.R
import com.philips.cdp.registration.ads.extension.hasDrawableSign
import com.philips.cdp.registration.ads.extension.hasHttpSign
import com.philips.cdp.registration.ads.helper.HouseAdsHelper.getDrawableUriAsString
import com.philips.cdp.registration.ads.helper.JsonPullerTask2
import com.philips.cdp.registration.ads.listener.AdListener
import com.philips.cdp.registration.ads.model.App
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target


class PromotionsAdsDialogFull(private val context: Context, private val jsonUrl: String) {

    private var jsonRawResponse:List<App> = ArrayList<App>()
    //private var jsonLocalRawResponse = ""
    private var showHeader = true
    private var hideIfAppInstalled = true
    private var usePalette = true
    private var cardCorner = 25
    private var callToActionButtonCorner = 25

    private var lastLoaded = 0
    private var isAdLoaded = false
    private var isUsingRawRes = false

    private var mAdListener: AdListener? = null
    private var dialog: AlertDialog? = null

    constructor(context: Context, @RawRes rawFile: Int) : this(context, "") {
        isUsingRawRes = true
     //   jsonLocalRawResponse = HouseAdsHelper.getJsonFromRaw(context, rawFile)
    }

    fun showHeaderIfAvailable(showHeader: Boolean): PromotionsAdsDialogFull {
        this.showHeader = showHeader
        return this
    }

    fun setCardCorners(corners: Int): PromotionsAdsDialogFull {
        this.cardCorner = corners
        return this
    }

    fun setCtaCorner(corner: Int): PromotionsAdsDialogFull {
        this.callToActionButtonCorner = corner
        return this
    }

    fun setAdListener(listener: AdListener): PromotionsAdsDialogFull {
        this.mAdListener = listener
        return this
    }

    @Suppress("unused")
    fun isAdLoaded(): Boolean {
        return isAdLoaded
    }

    fun hideIfAppInstalled(hide: Boolean): PromotionsAdsDialogFull {
        this.hideIfAppInstalled = hide
        return this
    }

    fun usePalette(usePalette: Boolean): PromotionsAdsDialogFull {
        this.usePalette = usePalette
        return this
    }

    fun loadAds(): PromotionsAdsDialogFull {
        isAdLoaded = false
       // if (!isUsingRawRes) {
            require(jsonUrl.trim().isNotEmpty()) { context.getString(R.string.error_url_blank) }
            if (jsonRawResponse.isEmpty()) {
                var jsonPullerTask2 = JsonPullerTask2();
                jsonPullerTask2.launchRequest(jsonUrl,context, object : JsonPullerTask2.JsonPullerListener {
                    override fun onPostExecute(result: List<App>?) {
                        if (result!!.size > 0) {
                            jsonRawResponse = result
                                configureAds(result)

                        }
                        else {
                            mAdListener?.onAdLoadFailed(Exception(context.getString(R.string.error_null_response)))
                        }
                    }
                })
            }
            else configureAds(jsonRawResponse)

       // else configureAds(jsonLocalRawResponse)
        return this
    }

    fun showAd(): PromotionsAdsDialogFull {

        if (dialog != null){
            dialog!!.show()}
        return this
    }

    private fun configureAds(dialogModalList: List<App>?) {

        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post(Runnable {
            val builder = AlertDialog.Builder(context)

            if (dialogModalList!!.size > 0) {
                val dialogModal = dialogModalList.get(lastLoaded)

                if (lastLoaded == dialogModalList.size - 1) lastLoaded = 0
                else lastLoaded++

                val view = View.inflate(context, R.layout.house_ads_dialog_full_layout, null)
                val iconClose = view.findViewById<TextView>(R.id.closeIcon);
                iconClose.setOnClickListener { dialog!!.hide() }
                val iconUrl = dialogModal.appIcon!!
                val largeImageUrl = dialogModal.appHeaderImage!!
                val appTitle = dialogModal.appTitle!!
                val appDescription = dialogModal.appDesc!!

                if (!isUsingRawRes) {
                    require(!(iconUrl.trim().isEmpty() || !iconUrl.trim().hasHttpSign)) {
                        context.getString(R.string.error_icon_url_null)
                    }

                    require(!(largeImageUrl.trim().isNotEmpty() && !largeImageUrl.trim().hasHttpSign)) {
                        context.getString(R.string.error_header_image_url_null)
                    }

                    require(!(appTitle.trim().isEmpty() || appDescription.trim().isEmpty())) {
                        context.getString(R.string.error_title_description_null)
                    }
                }
                else {
                    if (iconUrl.trim().isNotEmpty()) {
                        when {
                            iconUrl.trim().hasHttpSign -> Log.d(TAG, "App Logo param starts with `http://`")
                            iconUrl.trim().hasDrawableSign -> Log.d(TAG, "App Logo param is a local drawable")
                            else -> throw IllegalArgumentException(context.getString(R.string.error_raw_resource_icon_null))
                        }
                    }
                    if (largeImageUrl.trim().isNotEmpty()) {
                        when {
                            largeImageUrl.trim().hasHttpSign -> Log.d(TAG, "App Header param starts with `http://`")
                            largeImageUrl.trim().hasDrawableSign -> Log.d(TAG, "App Header param is a local drawable")
                            else -> throw IllegalArgumentException(context.getString(R.string.error_raw_resource_header_image_null))
                        }
                    }
                }

                val cardView = view.findViewById<CardView>(R.id.houseAds_card_view)
                cardView.radius = cardCorner.toFloat()

                val callToActionButton = view.findViewById<Button>(R.id.houseAds_cta)
                val gradientDrawable = callToActionButton.background as GradientDrawable
                gradientDrawable.cornerRadius = callToActionButtonCorner.toFloat()

                val icon = view.findViewById<ImageView>(R.id.houseAds_app_icon)
                val headerImage = view.findViewById<ImageView>(R.id.houseAds_header_image)
                val title = view.findViewById<TextView>(R.id.houseAds_title)
                val description = view.findViewById<TextView>(R.id.houseAds_description)
                val ratings = view.findViewById<RatingBar>(R.id.houseAds_rating)
                val price = view.findViewById<TextView>(R.id.houseAds_price)

                val iconUrlToLoad: String = if (iconUrl.hasDrawableSign) getDrawableUriAsString(context, iconUrl)!!
                else iconUrl



                Picasso.get().load("https://www.assets.signify.com/is/image/PhilipsLighting/a4b08b37828d4873be92aacb0096940a?wid=1280&hei=1280").into(icon, object : Callback {
                    override fun onSuccess() {
                        isAdLoaded = true
                        mAdListener?.onAdLoaded()

                        if (icon.visibility == View.GONE) icon.visibility = View.VISIBLE
                        var dominantColor = ContextCompat.getColor(context, R.color.colorAccent)
                        if (usePalette) {
                            val palette = Palette.from((icon.drawable as BitmapDrawable).bitmap).generate()
                            dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.colorAccent))
                        }

                        val drawable = callToActionButton.background as GradientDrawable
                        drawable.setColor(dominantColor)

                        if (dialogModal.appRating!!.toFloat() > 0) {
                            ratings.rating = (dialogModal.appRating)!!.toFloat()
                            val ratingsDrawable = ratings.progressDrawable
                            DrawableCompat.setTint(ratingsDrawable, dominantColor)
                        }
                        else ratings.visibility = View.GONE
                    }

                    override fun onError(exception: Exception) {
                        isAdLoaded = false
                        mAdListener?.onAdLoadFailed(exception)
                        icon.visibility = View.GONE
                    }
                })

                if (largeImageUrl.trim().isNotEmpty() && showHeader) {
                    val largeImageUrlToLoad: String = if (largeImageUrl.hasDrawableSign) getDrawableUriAsString(context, largeImageUrl)!!
                    else largeImageUrl

                    Picasso.get().load(largeImageUrlToLoad).into(object : Target {
                        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                            headerImage.setImageBitmap(bitmap)
                            headerImage.visibility = View.VISIBLE
                        }

                        override fun onBitmapFailed(exception: Exception, errorDrawable: Drawable?) {
                            headerImage.visibility = View.GONE
                        }

                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                    })
                }
                else headerImage.visibility = View.GONE

                title.text = "White and color ambiance 1-pack B22"
                description.text = "nclude a Philips Hue white and color ambiance bulb in your Philips Hue system and experience high quality white and colored light that offers you endless possibilities"
                callToActionButton.text = "philips play store"
                if (dialogModal.appPrice!!.trim().isEmpty()) price.visibility = View.GONE
                else price.text = String.format(context.getString(R.string.price_format), dialogModal.appPrice)

                builder.setView(view)
                dialog = builder.create()
               dialog!!.window!!.setBackgroundDrawableResource(android.R.color.white)
                dialog!!.setOnShowListener { mAdListener?.onAdShown() }
                dialog!!.setOnCancelListener { mAdListener?.onAdClosed() }
                dialog!!.setOnDismissListener { mAdListener?.onAdClosed() }
                mAdListener?.onAdLoaded()

                callToActionButton.setOnClickListener {
                    dialog!!.dismiss()
                    val packageOrUrl = dialogModal.appUri
                    if (packageOrUrl!!.trim().startsWith("http")) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(packageOrUrl)))
                        mAdListener?.onApplicationLeft()
                    } else {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageOrUrl")))
                            mAdListener?.onApplicationLeft()
                        } catch (e: ActivityNotFoundException) {
                            mAdListener?.onApplicationLeft()
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageOrUrl")))
                        }

                    }
                }
            }
        })



    }



    private companion object {
        private val TAG = PromotionsAdsDialogFull::class.java.simpleName.toString()
    }
}