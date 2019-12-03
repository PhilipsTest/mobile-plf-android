package com.ecs.demotestuapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.ecs.demotestuapp.activity.ResultActivity;
import com.ecs.demotestuapp.jsonmodel.Property;
import com.ecs.demotestuapp.jsonmodel.SubgroupItem;
import com.ecs.demotestuapp.util.ECSDataHolder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.philips.cdp.di.ecs.constants.ModelConstants;
import com.philips.cdp.di.ecs.error.ECSError;
import com.philips.cdp.di.ecs.model.address.Country;
import com.philips.cdp.di.ecs.model.address.ECSAddress;
import com.philips.cdp.di.ecs.model.address.Region;
import com.philips.cdp.di.ecs.model.region.ECSRegion;
import com.philips.cdp.di.ecs.util.ECSConfiguration;
import com.philips.platform.pif.DataInterface.USR.UserDataInterfaceException;
import com.philips.platform.pif.DataInterface.USR.UserDetailConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public abstract class BaseFragment extends Fragment {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void gotoResultActivity(String result){

        Intent intent = new Intent(getActivity(), ResultActivity.class);
        intent.putExtra("result",result);
        startActivity(intent);
    }

    public  String getFailureString(Exception exception, ECSError ecsError){
        return "Error \n"+ exception.getLocalizedMessage() + "\nError type \n" + ecsError.getErrorType() + "\nError code \n" + ecsError.getErrorcode();
    }

    String getJsonStringFromObject(Object object){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return "Success\n" + gson.toJson(object);
    }


    public void inflateLayout(LinearLayout linearLayout, SubgroupItem subgroupItem) {

        List<Property> editTexts = subgroupItem.getEditTexts();
        List<Property> spinners = subgroupItem.getSpinners();
        List<Property> switches = subgroupItem.getSwitches();


        for (Property property:editTexts){

            int typeClassNumber = property.inputType;

            EditText myEditText = new EditText(getActivity());
            myEditText.setTag(property.tag);
            if(property.hint!=null){
                myEditText.setHint(property.hint);
            }

            if(typeClassNumber!=-1){
                myEditText.setInputType(typeClassNumber);
            }
            myEditText.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(myEditText);
        }

        for (Property property:spinners){

            Spinner spinner = new Spinner(getActivity());
            spinner.setTag(property.tag);
            spinner.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(spinner);
        }


        for (Property property:switches){

            Button button = new Button(getActivity());
            button.setTag(property.tag);
            button.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(button);
        }

    }

    public void fillSpinner(Spinner spinner, List<String> ctns){
        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, ctns);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinner.setAdapter(aa);
    }


    public void prepopulateText(LinearLayout linearLayout){

        EditText etFirstName = linearLayout.findViewWithTag("et_first_name");
        EditText etLastName = linearLayout.findViewWithTag("et_last_name");
        EditText etEmail = linearLayout.findViewWithTag("et_email");
        EditText etCountryCode = linearLayout.findViewWithTag("et_country_code");

        ArrayList<String> userDataMap = new ArrayList<>();
        HashMap<String, Object> userDetails = null;

        userDataMap.add(UserDetailConstants.GIVEN_NAME);
        userDataMap.add(UserDetailConstants.FAMILY_NAME);
        userDataMap.add(UserDetailConstants.EMAIL);
        try{
            if(ECSDataHolder.INSTANCE.getUserDataInterface()!=null)
            userDetails = ECSDataHolder.INSTANCE.getUserDataInterface().getUserDetails(userDataMap);

        } catch (UserDataInterfaceException e) {
            e.printStackTrace();
        }

        if(userDetails!=null){
            String firstname=  (String) userDetails.get(UserDetailConstants.GIVEN_NAME);
            String lastName=  (String) userDetails.get(UserDetailConstants.FAMILY_NAME);
            String email=  (String) userDetails.get(UserDetailConstants.EMAIL);

            etFirstName.setText(firstname);
            etLastName.setText(lastName);
            etEmail.setText(email);
            etCountryCode.setText(ECSConfiguration.INSTANCE.getCountry());
        }

    }

    public ECSAddress getECSAddress(LinearLayout linearLayout){

        EditText etFirstName = linearLayout.findViewWithTag("et_first_name");
        EditText etLastName = linearLayout.findViewWithTag("et_last_name");
        EditText etEmail = linearLayout.findViewWithTag("et_email");
        EditText etCountryCode = linearLayout.findViewWithTag("et_country_code");
        EditText etAddressLineOne = linearLayout.findViewWithTag("et_address_one");
        EditText etAddressLineTwo = linearLayout.findViewWithTag("et_address_two");
        EditText etPostalCode = linearLayout.findViewWithTag("et_postal_code");
        EditText etPhoneOne = linearLayout.findViewWithTag("et_phone_one");
        EditText etPhoneTwo = linearLayout.findViewWithTag("et_phone_two");
        EditText etTown = linearLayout.findViewWithTag("et_town");
        EditText etHouseNumber = linearLayout.findViewWithTag("et_house_no");


        Spinner spinnerSalutation = linearLayout.findViewWithTag("spinner_salutation");
        Spinner spinnerState= linearLayout.findViewWithTag("spinner_state");

        ECSAddress ecsAddress = new ECSAddress();

        ecsAddress.setFirstName(getTextFromEditText(etFirstName));
        ecsAddress.setLastName(getTextFromEditText(etLastName));

        Country country= new Country();
        country.setIsocode(ECSConfiguration.INSTANCE.getCountry());
        ecsAddress.setCountry(country);

        ecsAddress.setLine1(getTextFromEditText(etAddressLineOne));
        ecsAddress.setLine2(getTextFromEditText(etAddressLineTwo));
        ecsAddress.setPostalCode(getTextFromEditText(etPostalCode));
        ecsAddress.setPhone1(getTextFromEditText(etPhoneOne));
        ecsAddress.setPhone2(getTextFromEditText(etPhoneTwo));
        ecsAddress.setEmail(getTextFromEditText(etEmail));
        ecsAddress.setTown(getTextFromEditText(etTown));
        ecsAddress.setHouseNumber(getTextFromEditText(etHouseNumber));

        if(getTextFromSpinner(spinnerSalutation)!=null)
        ecsAddress.setTitleCode(getTextFromSpinner(spinnerSalutation).toLowerCase(Locale.getDefault()));
        ecsAddress.setRegion(getRegionFromName(getTextFromSpinner(spinnerState)));

        getTextFromSpinner(spinnerState);

        return ecsAddress;
    }

    public void populateAddress(LinearLayout linearLayout,ECSAddress ecsAddress){

        EditText etFirstName = linearLayout.findViewWithTag("et_first_name");
        EditText etLastName = linearLayout.findViewWithTag("et_last_name");
        EditText etCountryCode = linearLayout.findViewWithTag("et_country_code");
        EditText etAddressLineOne = linearLayout.findViewWithTag("et_address_one");
        EditText etAddressLineTwo = linearLayout.findViewWithTag("et_address_two");
        EditText etPostalCode = linearLayout.findViewWithTag("et_postal_code");
        EditText etPhoneOne = linearLayout.findViewWithTag("et_phone_one");
        EditText etPhoneTwo = linearLayout.findViewWithTag("et_phone_two");
        EditText etEmail = linearLayout.findViewWithTag("et_email");
        EditText etTown = linearLayout.findViewWithTag("et_town");
        EditText etHouseNumber = linearLayout.findViewWithTag("et_house_no");

        etFirstName.setText(ecsAddress.getFirstName());
        etLastName.setText(ecsAddress.getFirstName());
        etCountryCode.setText(ECSConfiguration.INSTANCE.getCountry());
        etAddressLineOne.setText(ecsAddress.getLine1());
        etAddressLineTwo.setText(ecsAddress.getLine2());

        etPostalCode.setText(ecsAddress.getPostalCode());
        etPhoneOne.setText(ecsAddress.getPhone1());
        etPhoneTwo.setText(ecsAddress.getPhone2());

        etEmail.setText(ecsAddress.getEmail());
        etTown.setText(ecsAddress.getTown());
        etHouseNumber.setText(ecsAddress.getHouseNumber());

        Spinner spinnerSalutation = linearLayout.findViewWithTag("spinner_salutation");
        Spinner spinnerState = linearLayout.findViewWithTag("spinner_state");

        fillSpinnerDataForSalutation(spinnerSalutation,ecsAddress);
        fillSpinnerDataForState(spinnerState,ecsAddress);

    }

    private void fillSpinnerDataForSalutation(Spinner spinner,ECSAddress ecsAddress) {

        if(ecsAddress==null) return;

        String salutation = ecsAddress.getTitleCode();

        if(salutation == null) return;

        List<String> list = new ArrayList<>();
        list.add("Mr.");
        list.add("Ms.");

        int position =0;
        for (int i =0;i<list.size();i++){
            if(salutation.equalsIgnoreCase(list.get(i))){
                position = i;
            }
        }

        fillSpinner(spinner,list);

        spinner.setSelection(position);
    }

    private void fillSpinnerDataForState(Spinner spinner,ECSAddress ecsAddress) {

        List<ECSRegion> ecsRegions = ECSDataHolder.INSTANCE.getEcsRegions();

        if(ecsRegions==null || ecsAddress ==null) return;

        List<String> list = new ArrayList<String>();
        Region region = ecsAddress.getRegion();

        int position = 0;

        for (int i=0;i< ecsRegions.size();i++){

            list.add(ecsRegions.get(i).getName());
            if(region!=null && region.getIsocode()!=null && region.getIsocode().equalsIgnoreCase(ecsRegions.get(i).getIsocode())){

                position = i;
            }
        }

        fillSpinner(spinner,list);
        spinner.setSelection(position);
    }

    private Region getRegionFromName(String stateName) {

        if(stateName == null || ECSDataHolder.INSTANCE.getEcsRegions()==null) return null;

        for(ECSRegion ecsRegion: ECSDataHolder.INSTANCE.getEcsRegions()){

            if(stateName.equalsIgnoreCase(ecsRegion.getName())){

                Region region = new Region();
                region.setIsocodeShort(ecsRegion.getIsocode());
                return region;
            }
        }
      return null;
    }

    private String getTextFromSpinner(Spinner spinner) {
       return  (String)spinner.getSelectedItem();
    }

    String getTextFromEditText(EditText et){

        if(et.getText()!=null && !et.getText().toString().isEmpty()){
            return  et.getText().toString();
        }
        return null;
    }

   abstract public void clearData();

}
