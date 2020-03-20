/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */
package com.philips.platform.aildemo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.demo.R;
import com.philips.platform.uid.thememanager.AccentRange;
import com.philips.platform.uid.thememanager.ContentColor;
import com.philips.platform.uid.thememanager.NavigationColor;
import com.philips.platform.uid.thememanager.ThemeConfiguration;
import com.philips.platform.uid.thememanager.UIDHelper;

import java.util.ArrayList;

public class AppInfraMainActivity extends AppCompatActivity {

    AppInfraInterface mAppInfra;
    ListView listView;
    String appInfraComponents[] = {"Secure Storage", "AppTagging", "Logging","AppIdentity",
            "Internationalization", "ServiceDiscovery", "TimeSync", "Config", "Rest Client", " A/B Testing",  "WhiteBox API", "Internet Check", "Language Pack",
            "Resolution locale","App Update"};
    byte[] plainByte;
    byte[] encryptedByte;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTheme();
        setContentView(R.layout.activity_app_infra_main);
        mAppInfra = AILDemouAppInterface.getInstance().getAppInfra();
        final TextView componentIDTextView = (TextView) findViewById(R.id.appInfraComponentID);
        componentIDTextView.setText(((AppInfra)mAppInfra).getComponentId());
        final TextView versionTextView = (TextView) findViewById(R.id.appInfraVersion);
        versionTextView.setText(((AppInfra)mAppInfra).getVersion());
        listView = (ListView) findViewById(R.id.listViewAppInfraComponents);
        listView.setAdapter(new AppInfraListAdapter());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                launchAppInfraActivities(position);
            }
        });

        final ArrayList arryaLsit = new ArrayList();
        arryaLsit.add("appinfra.testing.service");
        arryaLsit.add("userreg.janrain.cdn.v2");
        arryaLsit.add("userreg.landing.emailverif");
        arryaLsit.add("userreg.landing.resetpass");

       /* AppInfraApplication.gAppInfra.getServiceDiscovery().getServicesWithCountryPreference(arryaLsit, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveyService> urlMap) {
                for (int i = 0; i < urlMap.size(); i++)
                {
                    Log.i("SDTest", ""+urlMap.get(arryaLsit.get(i)).getConfigUrls());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });
        AppInfraApplication.gAppInfra.getServiceDiscovery().getServicesWithLanguagePreference(arryaLsit, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveyService> urlMap) {
                for (int i = 0; i < urlMap.size(); i++)
                {
                    Log.i("SDTest", ""+urlMap.get(arryaLsit.get(i)).getConfigUrls());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });

        AppInfraApplication.gAppInfra.getServiceDiscovery().getServicesWithCountryPreference(arryaLsit, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveyService> urlMap) {
                for (int i = 0; i < urlMap.size(); i++)
                {
                    Log.i("SD", ""+urlMap.get(arryaLsit.get(i)).getConfigUrls());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });
        AppInfraApplication.gAppInfra.getServiceDiscovery().getServicesWithLanguagePreference(arryaLsit, new ServiceDiscoveryInterface.OnGetServiceUrlMapListener() {
            @Override
            public void onSuccess(Map<String, ServiceDiscoveyService> urlMap) {
                for (int i = 0; i < urlMap.size(); i++)
                {
                    Log.i("SD", ""+urlMap.get(arryaLsit.get(i)).getConfigUrls());
                }
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });

        AppInfraApplication.gAppInfra.getServiceDiscovery().getServiceUrlWithCountryPreference("userreg.janrain.api.v2", new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
            @Override
            public void onSuccess(URL url) {
                Log.i("SD", ""+url);
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });
        AppInfraApplication.gAppInfra.getServiceDiscovery().getServiceUrlWithCountryPreference("userreg.janrain.cdn", new ServiceDiscoveryInterface.OnGetServiceUrlListener() {
            @Override
            public void onSuccess(URL url) {
                Log.i("SD", ""+url);
            }

            @Override
            public void onError(ERRORVALUES error, String message) {
                Log.i("SD", ""+message);
            }
        });
*/
    }


    private void launchAppInfraActivities(int position) {
        switch (position) {
            case 0:
               /* Toast toast = Toast.makeText(getContext(), "Launch your activity here", Toast.LENGTH_SHORT);
                toast.show();*/
                Intent intent = new Intent(AppInfraMainActivity.this, SecureStorageMenuActivity.class);
                startActivity(intent);
                break;
            case 1:
                //                AppTagging.enableAppTagging(true);
//                //Mandatory to set
//                AppTagging.setTrackingIdentifier(ANALYTICS_APP_ID);
//                AppTagging.init(Locale.CHINA, getActivity(), "App Framwork demo app");
                Intent i = new Intent(AppInfraMainActivity.this, AIATDemoPage.class);
                startActivity(i);
                break;
            case 2:
                Intent intentLoggingActivity = new Intent(AppInfraMainActivity.this, LoggingActivity.class);
                startActivity(intentLoggingActivity);

                break;

            case 3:
                Intent intentAppIdentityActivity = new Intent(AppInfraMainActivity.this,
                        AppIndentityDemoPage.class);
                startActivity(intentAppIdentityActivity);

                break;
            case 4:
                Intent intentLocalMainActivity = new Intent(AppInfraMainActivity.this,
                        InternationalizationDemoPage.class);
                startActivity(intentLocalMainActivity);

                break;
            case 5:
                Intent intentServiceDiscoveryActivity = new Intent(AppInfraMainActivity.this,
                        ServiceDiscoverySelectionActivity.class);
                startActivity(intentServiceDiscoveryActivity);

                break;
            case 6:
                Intent intentTimeSyncActivity = new Intent(AppInfraMainActivity.this,
                        TimeSyncDemo.class);
                startActivity(intentTimeSyncActivity);

                break;
            case 7:

                Intent configActivity = new Intent(AppInfraMainActivity.this,
                        AppConfigurationActivity.class);
                startActivity(configActivity);
                break;

            case 8:

                Intent restClientActivity = new Intent(AppInfraMainActivity.this,
                        RestMenuActivity.class);
                startActivity(restClientActivity);
                break;

            case 9 :
                Intent abTesting = new Intent(AppInfraMainActivity.this , AbTestingDemo.class );
                startActivity(abTesting);
                break;


            case 10:
                Intent whiteBoxAPISignInIntent = new Intent(AppInfraMainActivity.this,
                        WhiteBoxAPIActivity.class);
                startActivity(whiteBoxAPISignInIntent);
                break;
            case 11:
                Intent secureDbIntent = new Intent(AppInfraMainActivity.this,
                        InternetCheckActivity.class);
                startActivity(secureDbIntent);
                break;

            case 12:
                Intent languagePackIntent = new Intent(AppInfraMainActivity.this,
                        LanguagePackActivity.class);
                startActivity(languagePackIntent);
                break;
            case 13:
                Intent resolutionLocaleIntent = new Intent(AppInfraMainActivity.this,
                        ResolutionLocaleActivity.class);
                startActivity(resolutionLocaleIntent);
                break;

            case 14:
                Intent appUpdateActivity = new Intent(AppInfraMainActivity.this , AppUpdateActivity.class);
                startActivity(appUpdateActivity);
                break;

            
        }

    }

    class AppInfraListAdapter extends BaseAdapter {
        ViewHolder viewHolder;

        @Override
        public int getCount() {
            return appInfraComponents.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.app_infra_list_row, null);
                viewHolder = new ViewHolder();
                viewHolder.testModeLabel = (TextView) convertView.findViewById(R.id.AppInfraListRowTextLabel);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.testModeLabel.setText(appInfraComponents[position]);
            return convertView;
        }

        class ViewHolder {
            TextView testModeLabel;
        }
    }

    private void initTheme() {
        int themeResourceID = new ThemeHelper(this).getThemeResourceId();
        int themeIndex = themeResourceID;
        if (themeIndex <= 0) {
            themeIndex = R.style.Theme_DLS_Blue_UltraLight;
        }
        getTheme().applyStyle(themeIndex, true);
        UIDHelper.init(new ThemeConfiguration(this, ContentColor.ULTRA_LIGHT, NavigationColor.BRIGHT, AccentRange.ORANGE));

    }
}
