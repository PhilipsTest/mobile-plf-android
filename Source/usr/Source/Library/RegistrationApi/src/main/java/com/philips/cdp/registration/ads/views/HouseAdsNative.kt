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
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.philips.cdp.registration.R
import com.philips.cdp.registration.ads.extension.hasDrawableSign
import com.philips.cdp.registration.ads.extension.hasHttpSign
import com.philips.cdp.registration.ads.helper.HouseAdsHelper
import com.philips.cdp.registration.ads.helper.JsonPullerTask2
import com.philips.cdp.registration.ads.listener.NativeAdListener
import com.philips.cdp.registration.ads.modal.HouseAdsNativeView
import com.philips.cdp.registration.ads.model.App
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


@Suppress("unused")
class HouseAdsNative(private val context: Context, private val jsonUrl: String) {

    private var isUsingRawRes = false
    private var jsonRawResponse:List<App> = ArrayList<App>()
    private var jsonLocalRawResponse = ""

    var isAdLoaded = false
    private var lastLoaded = 0

    private var usePalette = true
    private var hideIfAppInstalled = false

    private var customNativeView: View? = null
    private var nativeAdView: HouseAdsNativeView? = null
    private var mNativeAdListener: NativeAdListener? = null
    private var callToActionListener: NativeAdListener.CallToActionListener? = null

    constructor(context: Context, @RawRes rawFile: Int) : this(context, "") {
        isUsingRawRes = true
        jsonLocalRawResponse = HouseAdsHelper.getJsonFromRaw(context, rawFile)
    }

    fun setNativeAdView(nativeAdView: HouseAdsNativeView): HouseAdsNative {
        this.nativeAdView = nativeAdView
        return this
    }

    fun setNativeAdView(view: View): HouseAdsNative {
        this.customNativeView = view
        return this
    }

    fun hideIfAppInstalled(hide: Boolean): HouseAdsNative {
        this.hideIfAppInstalled = hide
        return this
    }

    fun usePalette(usePalette: Boolean): HouseAdsNative {
        this.usePalette = usePalette
        return this
    }

    fun setNativeAdListener(listener: NativeAdListener): HouseAdsNative {
        this.mNativeAdListener = listener
        return this
    }

    fun setCallToActionListener(listener: NativeAdListener.CallToActionListener): HouseAdsNative {
        this.callToActionListener = listener
        return this
    }

    fun loadAds(): HouseAdsNative {   isAdLoaded = false
        // if (!isUsingRawRes) {
        require(jsonUrl.trim().isNotEmpty()) { context.getString(R.string.error_url_blank) }
        if (jsonRawResponse.isEmpty()) {
            JsonPullerTask2().launchRequest(jsonUrl,context, object : JsonPullerTask2.JsonPullerListener {
                override fun onPostExecute(result: List<App>?) {
                    if (result!!.size > 0) {
                        jsonRawResponse = result
                        configureAds(result)

                    }
                    else {
                        mNativeAdListener?.onAdLoadFailed(Exception(context.getString(R.string.error_null_response)))
                    }
                }
            })
        }
        else configureAds(jsonRawResponse)

        // else configureAds(jsonLocalRawResponse)
        return this
    }

    private fun configureAds(modalList: List<App>?) {

        val uiHandler = Handler(Looper.getMainLooper())
        uiHandler.post(Runnable {


        if (modalList!!.size > 0) {
            val dialogModal = modalList[lastLoaded]
            if (lastLoaded == modalList.size - 1)
                lastLoaded = 0
            else lastLoaded++

            val title: TextView?
            val description: TextView?
            val price: TextView?
            val callToActionView: View?
            val icon: ImageView?
            val headerImage: ImageView?
            val ratings: RatingBar?


            if (nativeAdView != null) {
                val view = nativeAdView
                title = view!!.titleView
                description = view.descriptionView
                price = view.priceView
                callToActionView = view.callToActionView
                icon = view.iconView
                headerImage = view.headerImageView
                ratings = view.ratingsView
            } else {
                if (customNativeView != null) {
                    title = customNativeView!!.findViewById(R.id.houseAds_title)
                    description = customNativeView!!.findViewById(R.id.houseAds_description)
                    price = customNativeView!!.findViewById(R.id.houseAds_price)
                    callToActionView = customNativeView!!.findViewById(R.id.houseAds_cta)
                    icon = customNativeView!!.findViewById(R.id.houseAds_app_icon)
                    headerImage = customNativeView!!.findViewById(R.id.houseAds_header_image)
                    ratings = customNativeView!!.findViewById(R.id.houseAds_rating)
                } else throw NullPointerException(context.getString(R.string.error_native_ad_null))
            }

            val iconUrl = dialogModal.appIcon!!
            val largeImageUrl = dialogModal.appHeaderImage!!

            if (!isUsingRawRes) {
                require(!(iconUrl.trim().isEmpty() || !iconUrl.trim().hasHttpSign)) { context.getString(R.string.error_icon_url_null) }
                require(!(largeImageUrl.trim().isNotEmpty() && !largeImageUrl.trim().hasHttpSign)) { context.getString(R.string.error_header_image_url_null) }
                require(!(dialogModal.appTitle!!.trim().isEmpty() || dialogModal.appDesc!!.trim().isEmpty())) { context.getString(R.string.error_title_description_null) }
            }
            else {
                if (iconUrl.trim().isNotEmpty()) {
                    when {
                        iconUrl.trim().startsWith("http") -> Log.d(TAG, "App Logo param starts with `http://`")
                        iconUrl.trim().startsWith("@drawable/") -> Log.d(TAG, "App Logo param is a local drawable")
                        else -> throw IllegalArgumentException(context.getString(R.string.error_raw_resource_icon_null))
                    }
                }
                if (largeImageUrl.trim().isNotEmpty()) {
                    when {
                        largeImageUrl.trim().startsWith("http") -> Log.d(TAG, "App Header param starts with `http://`")
                        largeImageUrl.trim().startsWith("@drawable/") -> Log.d(TAG, "App Header param is a local drawable")
                        else -> throw IllegalArgumentException(context.getString(R.string.error_raw_resource_header_image_null))
                    }
                }
            }

            val iconUrlToLoad: String = if (iconUrl.hasDrawableSign) HouseAdsHelper.getDrawableUriAsString(context, iconUrl)!!
            else iconUrl

            Picasso.get().load("https://www.assets.signify.com/is/image/PhilipsLighting/a4b08b37828d4873be92aacb0096940a?wid=1280&hei=1280").into(icon, object : Callback {
                override fun onSuccess() {
                    if (usePalette) {
                        val palette = Palette.from((icon!!.drawable as BitmapDrawable).bitmap).generate()
                        val dominantColor = palette.getDominantColor(ContextCompat.getColor(context, R.color.colorAccent))

                        val drawable = GradientDrawable()
                        drawable.setColor(dominantColor)
//                        callToActionView!!.background = drawable

//                        if (dialogModal.appRating!!.toFloat() > 0) {
//                            ratings!!.rating = dialogModal.appRating!!.toFloat()
//                            val ratingsDrawable = ratings.progressDrawable
//                            DrawableCompat.setTint(ratingsDrawable, dominantColor)
//                        } else
//                            ratings!!.visibility = View.GONE
                    }


                    if (largeImageUrl.trim().isEmpty()) {
                        isAdLoaded = true
                        mNativeAdListener?.onAdLoaded()
                    }
                }

                override fun onError(exception: Exception) {
                    isAdLoaded = false
                    if (headerImage == null || dialogModal.appHeaderImage!!.isEmpty()) {
                        mNativeAdListener?.onAdLoadFailed(exception)
                    }
                }
            })


            if (largeImageUrl.trim().isNotEmpty()) {
                val largeImageUrlToLoad: String = if (largeImageUrl.hasDrawableSign) HouseAdsHelper.getDrawableUriAsString(context, largeImageUrl)!!
                else largeImageUrl

                Picasso.get().load(largeImageUrlToLoad).into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        if (headerImage != null) {
                            headerImage.setImageBitmap(bitmap)
                            headerImage.visibility = View.VISIBLE
                        }
                        isAdLoaded = true
                        mNativeAdListener?.onAdLoaded()
                    }

                    override fun onBitmapFailed(exception: Exception, errorDrawable: Drawable?) {
                        mNativeAdListener?.onAdLoadFailed(exception)
                        isAdLoaded = false
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                })
            }
            else headerImage?.visibility = View.GONE

            title!!.text = "White and color ambiance 1-pack B22"
          //  description!!.text = dialogModal.appDesc
            if (price != null) {
                price.visibility = View.VISIBLE
                if (dialogModal.appPrice!!.trim().isNotEmpty())
                    price.text = String.format(context.getString(R.string.price_format), dialogModal.appPrice)
                else
                    price.visibility = View.GONE
            }

            if (ratings != null) {
                ratings.visibility = View.VISIBLE
                if (dialogModal.appRating!!.toFloat() > 0)
                    ratings.rating = dialogModal.appRating!!.toFloat()
                else
                    ratings.visibility = View.GONE
            }

            if (callToActionView != null) {
                if (callToActionView is TextView) callToActionView.text = dialogModal.appCtaText
                if (callToActionView is Button) callToActionView.text = dialogModal.appCtaText
                require(callToActionView is TextView) { context.getString(R.string.error_cta_is_not_textview_instance) }

                callToActionView.setOnClickListener { view ->
                    if (callToActionListener != null)
                        callToActionListener!!.onCallToActionClicked(view)
                    else {
                        val packageOrUrl = dialogModal.appUri
                        if (packageOrUrl!!.trim().startsWith("http")) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(packageOrUrl)))
                        } else {
                            try {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageOrUrl")))
                            } catch (e: ActivityNotFoundException) {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=$packageOrUrl")))
                            }

                        }
                    }
                }
            }
        }
        })
    }

    private companion object {
        private val TAG = HouseAdsNative::class.java.simpleName.toString()
    }
}