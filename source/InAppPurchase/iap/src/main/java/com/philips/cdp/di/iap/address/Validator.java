
/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */

package com.philips.cdp.di.iap.address;

import android.telephony.PhoneNumberUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private static final String TOWN_PATTERN = "^[a-zA-Z\\s]{1,35}$";
    private static final String FIRST_NAME = "^[a-zA-Z]{1,17}$";
    private static final String LAST_NAME = "^[a-zA-Z]{1,17}$";
    private static final String ADDRESS = "^[a-zA-Z0-9(.,#/\\-)_\\s]{1,35}$";
    private static final String POSTAL_CODE = "^[A-Z0-9]{1,10}$";
    private static final String COUNTRY = "^[A-Z]{2,2}$";

    private boolean isValidString(String stringToBeValidated) {
        return stringToBeValidated != null && !stringToBeValidated.equalsIgnoreCase("");
    }

    public boolean isValidFirstName(String stringToBeValidated) {
        if (!isValidString(stringToBeValidated)) {
            return false;
        } else {
            Pattern pattern = Pattern.compile(FIRST_NAME);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        }
    }

    public boolean isValidLastName(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            Pattern pattern = Pattern.compile(LAST_NAME);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public boolean isValidEmail(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(stringToBeValidated).matches();
        } else {
            return false;
        }
    }

    public boolean isValidPostalCode(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            Pattern pattern = Pattern.compile(POSTAL_CODE);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public boolean isValidPhoneNumber(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            return PhoneNumberUtils.isGlobalPhoneNumber(stringToBeValidated.toString());
        } else {
            return false;
        }
    }

    public boolean isValidAddress(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            Pattern pattern = Pattern.compile(ADDRESS);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public boolean isValidTown(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            Pattern pattern = Pattern.compile(TOWN_PATTERN);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        } else {
            return false;
        }
    }

    public boolean isValidCountry(String stringToBeValidated) {
        if (isValidString(stringToBeValidated)) {
            Pattern pattern = Pattern.compile(COUNTRY);
            Matcher matcher = pattern.matcher(stringToBeValidated);
            return matcher.matches();
        } else {
            return false;
        }
    }
}
