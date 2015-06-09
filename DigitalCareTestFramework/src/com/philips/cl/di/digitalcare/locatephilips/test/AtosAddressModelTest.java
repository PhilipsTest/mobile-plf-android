package com.philips.cl.di.digitalcare.locatephilips.test;

import java.util.ArrayList;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.philips.cl.di.digitalcare.locatephilips.AtosAddressModel;
import com.philips.cl.di.digitalcare.locatephilips.AtosResponseModel;
import com.philips.cl.di.digitalcare.locatephilips.AtosResponseParser;
import com.philips.cl.di.digitalcare.locatephilips.AtosResultsModel;

public class AtosAddressModelTest extends InstrumentationTestCase {

	private final String TAG = AtosAddressModelTest.class.getSimpleName();
	private Context mContext, context = null;
	private AtosResponseParser mParser = null;

	protected void setUp() throws Exception {
		super.setUp();
		Log.d(TAG, "setUp..");
		mContext = getInstrumentation().getTargetContext();
		context = getInstrumentation().getContext();

		mParser = AtosResponseParser.getParserControllInstance(mContext);

	}

	public void testZip() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getZip();
		assertNotNull(received);
	}

	public void testwithoutZip() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos_no_zip.json");
		String received = atosAddressModel.getZip();
		assertTrue(received.isEmpty());
	}

	public void testPhone() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getPhone();
		assertNotNull(received);
	}

	public void testwithoutPhone() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos_no_phone.json");
		String received = atosAddressModel.getPhone();
		assertTrue(received.isEmpty());
	}

	public void testAddress1() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getAddress1();
		assertNotNull(received);
	}

	public void testAddress2() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getAddress2();
		assertNotNull(received);
	}

	public void testUrl() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getUrl();
		assertNotNull(received);
	}

	public void testCityState() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		String received = atosAddressModel.getCityState();
		assertNotNull(received);
	}

	public void testCityState_withoutstate() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos_no_state.json");
		String received = atosAddressModel.getCityState();
		assertNotNull(received);
	}

	public void testCityState_withoutcity() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos_no_city.json");
		String received = atosAddressModel.getCityState();
		assertNotNull(received);
	}

	public void testPhoneList() {
		AtosAddressModel atosAddressModel = getAtosAddressModel("atos.json");
		ArrayList<String> received = atosAddressModel.getPhoneList();
		assertNotNull(received);
	}

	private AtosAddressModel getAtosAddressModel(String jsonfile) {
		String response = AtosParserUtils.loadJSONFromAsset(jsonfile, context);
		mParser.processAtosResponse(response);
		AtosResponseModel atosResponseModel = mParser.getAtosResponse();
		ArrayList<AtosResultsModel> resultList = atosResponseModel
				.getResultsModel();
		AtosAddressModel atosAddressModel = resultList.get(0)
				.getmAddressModel();

		return atosAddressModel;
	}

}
