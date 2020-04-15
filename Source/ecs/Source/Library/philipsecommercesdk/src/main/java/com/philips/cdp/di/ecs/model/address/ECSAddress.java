/* Copyright (c) Koninklijke Philips N.V., 2018
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.cdp.di.ecs.model.address;




import com.philips.cdp.di.ecs.constants.NetworkConstants;

import java.io.Serializable;

/**
 * The type Ecs address which contains all the data related to address.
 * The values here are set to createAddress
 * This object is returned when fetchSavedAddresses
 * When address is updated in updateAddress, the updated list is returned
 * It is passed as input to set it as delivery address, to delete the address and
 * while making payment it is passed as billing address and shipping address
 *
 */
public class ECSAddress implements Serializable{
    private static final long serialVersionUID = -2423291510688767513L;
    private Region region;
    private Country country;

    private String id;
    private String title; //Mr.
    private String titleCode; //mr
    private String firstName;

    public void setRegion(Region region) {
        this.region = region;
    }

    private String lastName;
    private String line1;
    private String houseNumber;

    public void setCountry(Country country) {
        this.country = country;
    }

    private String line2;
    private String phone1;
    private String phone2;
    private String town;
    private String postalCode;

    public void setTitleCode(String titleCode) {
        this.titleCode = titleCode;
    }

    private String formattedAddress;
    private String email;

    private boolean shippingAddress;
    private boolean visibleInAddressBook;

    private boolean isDefaultAddress = false;

    public String getTitle() {
        return title;
    }

    public String getPhone1() {
        return phone1;
    }

    public String getPhone2() {
        if (phone2 == null) {
            phone2 = NetworkConstants.EMPTY_RESPONSE;
        }
        return phone2;
    }

    public Country getCountry() {
        return country;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        if (line2 == null) {
            line2 = NetworkConstants.EMPTY_RESPONSE;
        }
        return line2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Region getRegion() {
        return region;
    }

    public boolean isShippingAddress() {
        return shippingAddress;
    }

    public String getTitleCode() {
        if (titleCode == null) {
            titleCode = NetworkConstants.EMPTY_RESPONSE;
        }
        return titleCode;
    }

    public String getTown() {
        return town;
    }

    public boolean isVisibleInAddressBook() {
        return visibleInAddressBook;
    }

    public String getEmail() {
        return email;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    //Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setShippingAddress(boolean shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setVisibleInAddressBook(boolean visibleInAddressBook) {
        this.visibleInAddressBook = visibleInAddressBook;
    }

    public boolean isDefaultAddress() {
        return isDefaultAddress;
    }

    /**
     * Sets default address.
     *
     * @param defaultAddress is set to true, to make it as default delivery address
     */
    public void setDefaultAddress(boolean defaultAddress) {
        isDefaultAddress = defaultAddress;
    }

 /*   public String getFullAddress(){


        String formattedAddress = ""
        String regionDisplayName =  if (ecsAddress.region?.name !=null)  ecsAddress.region?.name else  ecsAddress.region?.isocodeShort
        String countryDisplayName = if (ecsAddress.country?.name !=null)  ecsAddress.country?.name else  ecsAddress.country?.isocode


        formattedAddress = if (!houseNumber.isNullOrEmpty()) formattedAddress+ houseNumber +"," else formattedAddress
        formattedAddress = if (!line1.isNullOrEmpty()) formattedAddress+ line1 + ","+"\n" else formattedAddress
        formattedAddress = if (!line2.isNullOrEmpty()) formattedAddress+ line2 + ","+"\n" else formattedAddress
        formattedAddress = if (!town.isNullOrEmpty()) formattedAddress+ town + ","+"\n" else formattedAddress
        formattedAddress = if (!regionDisplayName.isNullOrEmpty()) formattedAddress+ regionDisplayName + ","+"\n" else formattedAddress
        formattedAddress = if (!postalCode.isNullOrEmpty()) formattedAddress+ postalCode + ","+"\n" else formattedAddress
        formattedAddress = if (!countryDisplayName.isNullOrEmpty()) formattedAddress+ countryDisplayName  else formattedAddress

        return formattedAddress


    }*/
}
