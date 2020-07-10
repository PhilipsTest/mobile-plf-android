package com.philips.platform.ccb.constant;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

import androidx.annotation.NonNull;


public class SpannableHelper {
    @NonNull
    public static Spannable getSpannable(String text, String startCharacter, String endCharacter, LinkSpanClickListener spanClickListener) {
        int openingBracketIndex = text.indexOf(startCharacter);
        int closingBracketIndex = text.indexOf(endCharacter,openingBracketIndex+1);
        if (openingBracketIndex == -1 || closingBracketIndex == -1) {
            openingBracketIndex = 0;
            closingBracketIndex = text.length();
        }
        text = text.replace(startCharacter, "");
        text = text.replace(endCharacter, "");

        Spannable privacyNotice = new SpannableString(text);
        privacyNotice.setSpan(new StyleSpan(Typeface.BOLD), openingBracketIndex, closingBracketIndex - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return privacyNotice;
    }
}
