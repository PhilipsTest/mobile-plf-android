package com.philips.cdp.digitalcare.rateandreview.productreview;


import com.bazaarvoice.BazaarEnvironment;
import com.bazaarvoice.BazaarRequest;
import com.bazaarvoice.OnBazaarResponse;
import com.bazaarvoice.SubmissionParams;
import com.bazaarvoice.types.Action;
import com.bazaarvoice.types.ApiVersion;
import com.bazaarvoice.types.RequestType;
import com.philips.cdp.digitalcare.DigitalCareConfigManager;
import com.philips.cdp.digitalcare.rateandreview.productreview.model.BazaarReviewModel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * BazaarFunctions.java <br>
 * ReviewSubmissionExample<br>
 * <p/>
 * BazaarVoiceWrapper class is used as utility class. This is interface between BazaarVoice library and
 * GUI.
 * <p/>
 * This is a suite of functions that leverage the BazaarvoiceSDK. This class
 * consolidates the usage of these functions for easier understanding of how to
 * use the SDK.
 * <p/>
 *
 * @author : Ritesh.jha@philips.com
 * @since : 11 Sep 2015
 */

public class BazaarVoiceWrapper {

    private static final String TAG = "BazaarFunctions";
    private static final String API_URL_STAGING = "stg.api.bazaarvoice.com"; //Staging server
    private static final String API_URL_PRODCUTION = "api.bazaarvoice.com"; //Production Server
    private static final String API_KEY_TEST = "2cpdrhohmgmwfz8vqyo48f52g";
    private static final ApiVersion API_VERSION = ApiVersion.FIVE_FOUR;
    private static String CLIENT_URL = API_URL_STAGING;
    private static String API_KEY = API_KEY_TEST;
    private static HashMap<String, String> mApiKeyProduction = null;
    private static BazaarEnvironment ENVIRONMENT = BazaarEnvironment.staging;

    /**
     * Submits the given review for the given product as a submission. This
     * means that it will be entered into the system and be ready for display
     * soon.
     *
     * @param prodId   the product ID
     * @param review   the full review
     * @param listener the callback function for handling the response
     */
    public static void submitReview(String prodId, BazaarReviewModel review,
                                    OnBazaarResponse listener) {
        reviewAction(prodId, review, listener, true);
    }

    /**
     * Builds a review request and sends it off as either a preview or a
     * submission.
     *
     * @param prodId   the product ID
     * @param review   the full review
     * @param listener the callback function for handling the response
     * @param submit   true to submit, false to preview
     */
    private static void reviewAction(String prodId, BazaarReviewModel review,
                                     OnBazaarResponse listener, boolean submit) {
        SubmissionParams params = new SubmissionParams();
        if (submit)
            params.setAction(Action.SUBMIT);
        else
            params.setAction(Action.PREVIEW);

        params.setProductId(prodId);
        params.setRating((int) review.getRating());
        params.setTitle(review.getSummary());
        params.setReviewText(review.getReview());
        params.setUserNickname(review.getNickname());
        params.setUserEmail(review.getEmail());

        if (!review.getEmail().equals("null"))
            params.setUserId(review.getEmail());
        else if (!(review.getNickname().equals("null") || "".equals(review.getNickname().trim())))
            params.setUserId(review.getNickname());
        else
            params.setUserId("Anonymous");

        String keyAvailable = DigitalCareConfigManager.getInstance().getBazaarVoiceKey();
        if (keyAvailable != null) {
            API_KEY = keyAvailable;
            ENVIRONMENT = BazaarEnvironment.production;
            CLIENT_URL = API_URL_PRODCUTION;
        }
        BazaarRequest submission = new BazaarRequest(CLIENT_URL, API_KEY, ENVIRONMENT , API_VERSION);
        submission.postSubmission(RequestType.REVIEWS, params, listener);
    }

    public static boolean isValidEmail(String email) {
        if (email == null)
            return false;
        if (email.length() == 0)
            return false;
        String emailPattern = "[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}";
        Pattern pattern = Pattern.compile(emailPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
