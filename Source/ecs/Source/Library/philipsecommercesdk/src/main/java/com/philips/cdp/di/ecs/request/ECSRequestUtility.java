/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.cdp.di.ecs.request;

import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.model.address.ECSAddress;

import java.util.HashMap;
import java.util.Locale;


public class ECSRequestUtility {

    public static HashMap<String, String>  getAddressParams(ECSAddress addresses){
        HashMap<String, String> addressHashMap = new HashMap<>();


        if(addresses.getId()!=null && !addresses.getId().trim().isEmpty())
            addressHashMap.put(ModelConstants.ADDRESS_ID, addresses.getId()); // required in case of preparePayment

        if(addresses.getFirstName()!=null && !addresses.getFirstName().trim().isEmpty())
            addressHashMap.put(ModelConstants.FIRST_NAME, addresses.getFirstName());

        if(addresses.getLastName()!=null &&  !addresses.getLastName().trim().isEmpty())
            addressHashMap.put(ModelConstants.LAST_NAME, addresses.getLastName());

        if(addresses.getTitleCode()!=null &&  !addresses.getTitleCode().trim().isEmpty())
            addressHashMap.put(ModelConstants.TITLE_CODE,addresses.getTitleCode().toLowerCase(Locale.getDefault()));

        if(addresses.getCountry()!=null && addresses.getCountry().getIsocode()!=null)
            addressHashMap.put(ModelConstants.COUNTRY_ISOCODE,addresses.getCountry().getIsocode());

        if(addresses.getHouseNumber()!=null && !addresses.getHouseNumber().trim().isEmpty())
            addressHashMap.put(ModelConstants.HOUSE_NO,addresses.getHouseNumber());

        if(addresses.getLine1()!=null && !addresses.getLine1().trim().isEmpty())
            addressHashMap.put(ModelConstants.LINE_1,addresses.getLine1());

        if(addresses.getLine2()!=null && !addresses.getLine2().trim().isEmpty())
            addressHashMap.put(ModelConstants.LINE_2, addresses.getLine2());

        if(addresses.getPostalCode()!=null && !addresses.getPostalCode().trim().isEmpty())
            addressHashMap.put(ModelConstants.POSTAL_CODE, addresses.getPostalCode());

        if(addresses.getTown()!=null && !addresses.getTown().trim().isEmpty())
            addressHashMap.put(ModelConstants.TOWN, addresses.getTown());

        if(addresses.getPhone1()!=null && !addresses.getPhone1().trim().isEmpty())
            addressHashMap.put(ModelConstants.PHONE_1,addresses.getPhone1());

        if(addresses.getPhone2()!=null && !addresses.getPhone2().trim().isEmpty())
            addressHashMap.put(ModelConstants.PHONE_2, addresses.getPhone2());


        if(addresses.getRegion()!=null && addresses.getRegion().getIsocodeShort()!=null) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE, addresses.getRegion().getIsocodeShort());
        }

        addressHashMap.put(ModelConstants.DEFAULT_ADDRESS, String.valueOf(addresses.isDefaultAddress()));

        return addressHashMap;
    }
}
