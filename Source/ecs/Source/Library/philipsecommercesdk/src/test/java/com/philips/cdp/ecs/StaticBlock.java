package com.philips.cdp.ecs;

import com.philips.platform.ecs.constants.ModelConstants;
import com.philips.platform.ecs.model.address.ECSAddress;
import com.philips.platform.ecs.model.address.Country;
import com.philips.platform.ecs.model.address.Region;
import com.philips.platform.ecs.util.ECSConfiguration;

import java.util.HashMap;
import java.util.Locale;

public class StaticBlock {

public static final String  mockAccessToken = "acceesstoken";

    public static void initialize(){
        ECSConfiguration.INSTANCE.setBaseURL("acc.us.pil.shop.philips.com/");
        ECSConfiguration.INSTANCE.setSiteId("US_Tuscany");
        ECSConfiguration.INSTANCE.setPropositionID("Tuscany2016");
        ECSConfiguration.INSTANCE.setLocale("en_US");
        ECSConfiguration.INSTANCE.setRootCategory("TuscanyCampaign");
        ECSConfiguration.INSTANCE.setAuthToken(mockAccessToken);
    }

    public static  String getBaseURL(){
        return ECSConfiguration.INSTANCE.getBaseURL();
    }

    public static String getSiteID(){
        return ECSConfiguration.INSTANCE.getSiteId();
    }

    public static String getRootCategory(){
        return ECSConfiguration.INSTANCE.getRootCategory();
    }

    public static String getPropositionID(){
        return ECSConfiguration.INSTANCE.getPropositionID();
    }

    public static String getLocale(){
        return ECSConfiguration.INSTANCE.getLocale();
    }


    public static ECSAddress getAddressesObject(){

        ECSAddress addressRequest = new ECSAddress();
        addressRequest.setId("1234567");
        addressRequest.setFirstName("First name");
        addressRequest.setLastName("Second name");
        addressRequest.setTitleCode("Mr");
        Country country= new Country();
        country.setIsocode("DE");
        //country.se
        addressRequest.setCountry(country); // iso
        addressRequest.setLine1("Line 1");
        //   addressRequest.setLine2(shippingAddressFields.getLine2());
        addressRequest.setPostalCode("10111");
        addressRequest.setTown("Berlin");
        addressRequest.setPhone1("5043323");
        addressRequest.setPhone2("5043323");
        Region region = new Region();
        region.setIsocode("US");
        addressRequest.setRegion(region); // set Region eg State for US and Canada
        addressRequest.setHouseNumber("12A");
        addressRequest.setDefaultAddress(true);
        return addressRequest;
    }

    public static HashMap<String, String> getAddressParams(ECSAddress addresses){
        HashMap<String, String> addressHashMap = new HashMap<>();


        if(addresses.getId()!=null)
            addressHashMap.put(ModelConstants.ADDRESS_ID, addresses.getId()); // required in case of preparePayment

        if(addresses.getFirstName()!=null)
            addressHashMap.put(ModelConstants.FIRST_NAME, addresses.getFirstName());

        if(addresses.getLastName()!=null)
            addressHashMap.put(ModelConstants.LAST_NAME, addresses.getLastName());

        if(addresses.getTitleCode()!=null)
            addressHashMap.put(ModelConstants.TITLE_CODE,addresses.getTitleCode().toLowerCase(Locale.getDefault()));

        if(addresses.getCountry()!=null && addresses.getCountry().getIsocode()!=null)
            addressHashMap.put(ModelConstants.COUNTRY_ISOCODE,addresses.getCountry().getIsocode());

        if(addresses.getHouseNumber()!=null)
            addressHashMap.put(ModelConstants.HOUSE_NO,addresses.getHouseNumber());

        if(addresses.getLine1()!=null)
            addressHashMap.put(ModelConstants.LINE_1,addresses.getLine1());

        if(addresses.getLine2()!=null)
            addressHashMap.put(ModelConstants.LINE_2, addresses.getLine2());

        if(addresses.getPostalCode()!=null)
            addressHashMap.put(ModelConstants.POSTAL_CODE, addresses.getPostalCode());

        if(addresses.getTown()!=null)
            addressHashMap.put(ModelConstants.TOWN, addresses.getTown());

        if(addresses.getPhone1()!=null)
            addressHashMap.put(ModelConstants.PHONE_1,addresses.getPhone1());

        if(addresses.getPhone2()!=null)
            addressHashMap.put(ModelConstants.PHONE_2, addresses.getPhone2());


        if(addresses.getRegion()!=null && addresses.getRegion().getIsocode()!=null) {
            addressHashMap.put(ModelConstants.REGION_ISOCODE, addresses.getRegion().getIsocodeShort());
        }

        addressHashMap.put(ModelConstants.DEFAULT_ADDRESS, String.valueOf(addresses.isDefaultAddress()));


        return addressHashMap;
    }
}
