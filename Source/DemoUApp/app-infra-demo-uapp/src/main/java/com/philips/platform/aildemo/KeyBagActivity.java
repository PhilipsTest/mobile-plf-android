/*
 * Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination in whole or in part
 * is prohibited without the prior written consent of the copyright holder.
 */
package com.philips.platform.aildemo;


import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.philips.platform.appinfra.demo.R;
import com.philips.platform.appinfra.keybag.KeyBagInterface;
import com.philips.platform.appinfra.keybag.exception.KeyBagJsonFileNotFoundException;
import com.philips.platform.appinfra.keybag.model.AIKMService;
import com.philips.platform.appinfra.servicediscovery.ServiceDiscoveryInterface;
import com.philips.platform.appinfra.servicediscovery.model.AISDResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeyBagActivity extends AppCompatActivity {


	private EditText serviceIdEditText;
	private TextView responseTextView;
	private AISDResponse.AISDPreference aikmServiceDiscoveryPreference = AISDResponse.AISDPreference.AISDCountryPreference;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keybag);
		serviceIdEditText = (EditText) findViewById(R.id.service_id_edt);
		responseTextView = (TextView) findViewById(R.id.response_view);

		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radio_group);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
				if(checkedId == R.id.rbtn_language) {
					aikmServiceDiscoveryPreference = AISDResponse.AISDPreference.AISDLanguagePreference;
				} else
					aikmServiceDiscoveryPreference = AISDResponse.AISDPreference.AISDCountryPreference;
			}
		});
	}

	public void onClick(View view) {
		final KeyBagInterface keyBagInterface = AILDemouAppInterface.getInstance().getAppInfra().getKeyBagInterface();
		String[] serviceIds = serviceIdEditText.getText().toString().split(",");
		try {
			keyBagInterface.getServicesForServiceIds(new ArrayList<>(Arrays.asList(serviceIds)), aikmServiceDiscoveryPreference, null, new ServiceDiscoveryInterface.OnGetKeyBagMapListener() {
                @Override
                public void onSuccess(List<AIKMService> aikmServices) {
                    updateView(aikmServices);
                }

                @Override
                public void onError(ERRORVALUES error, String message) {
                    Log.e(getClass().getSimpleName(), message);
                }
            });
		} catch (KeyBagJsonFileNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void updateView(List<AIKMService> aikmServices) {
		StringBuilder stringBuilder = new StringBuilder();
		if (aikmServices != null && aikmServices.size() != 0) {
			for (int i = 0; i < aikmServices.size(); i++) {
				stringBuilder.append("ServiceId: ");
				stringBuilder.append(aikmServices.get(i).getServiceId());
				stringBuilder.append(", ");
				stringBuilder.append("Url:");
				stringBuilder.append("  ");
				stringBuilder.append(aikmServices.get(i).getConfigUrls());
				stringBuilder.append(", ");
				Map keyBag = aikmServices.get(i).getKeyBag();
				if (keyBag != null) {
					for (Object object : keyBag.entrySet()) {
						Map.Entry pair = (Map.Entry) object;
						String key = (String) pair.getKey();
						String value = (String) pair.getValue();

						stringBuilder.append("KeyBag Data --- ");
						stringBuilder.append(key);
						stringBuilder.append(":");
						stringBuilder.append(value);
						stringBuilder.append("  ");
					}
				}
				AIKMService.KEY_BAG_ERROR keyBagError = aikmServices.get(i).getKeyBagError();
				if (null != keyBagError) {
					stringBuilder.append("error -- ");
					stringBuilder.append(keyBagError.name());
				}
				stringBuilder.append("\n");
				stringBuilder.append("\n");
			}

		}

		responseTextView.setText(stringBuilder.toString());
	}
}
