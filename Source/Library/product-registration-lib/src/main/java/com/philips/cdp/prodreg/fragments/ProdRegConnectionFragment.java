package com.philips.cdp.prodreg.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.philips.cdp.prodreg.constants.EnhancedLinkMovementMethod;
import com.philips.cdp.prodreg.constants.ProdRegConstants;
import com.philips.cdp.prodreg.listener.ProdRegBackListener;
import com.philips.cdp.product_registration_lib.R;

/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
public class ProdRegConnectionFragment extends ProdRegBaseFragment implements ProdRegBackListener {

    public static final String TAG = ProdRegConnectionFragment.class.getName();

    @Override
    public String getActionbarTitle() {
        return getActivity().getString(R.string.PPR_NavBar_Title);
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.prodreg_connection, container, false);
        Button backButton = (Button) view.findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                onBackPressed();
            }
        });
        TextView tv = (TextView) view.findViewById(R.id.link_tv);
        // Linkify the TextView
        Spannable spannable = new SpannableString(Html.fromHtml((String) tv.getText()));
        Linkify.addLinks(spannable, Linkify.WEB_URLS);

        // Replace each URLSpan by a LinkSpan
        URLSpan[] spans = spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (URLSpan urlSpan : spans) {
            LinkSpan linkSpan = new LinkSpan(urlSpan.getURL());
            int spanStart = spannable.getSpanStart(urlSpan);
            int spanEnd = spannable.getSpanEnd(urlSpan);
            spannable.setSpan(linkSpan, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.removeSpan(urlSpan);
        }

        // Make sure the TextView supports clicking on Links
        tv.setMovementMethod(EnhancedLinkMovementMethod.getInstance());
        tv.setText(spannable, TextView.BufferType.SPANNABLE);
        return view;
    }

    @Override
    public boolean onBackPressed() {
        final FragmentActivity activity = getActivity();
        if (activity != null && !activity.isFinishing()) {
            return clearFragmentStack();
        }
        return true;
    }

    private class LinkSpan extends URLSpan {
        private LinkSpan(String url) {
            super(url);
        }

        @Override
        public void onClick(View view) {
            String url = getURL();
            final ProdRegWebViewFragment processRegWebViewFragment = new ProdRegWebViewFragment();
            Bundle bundle = new Bundle();
            bundle.putString(ProdRegConstants.WEB_URL, url);
            processRegWebViewFragment.setArguments(bundle);
            showFragment(processRegWebViewFragment);
        }
    }
}
