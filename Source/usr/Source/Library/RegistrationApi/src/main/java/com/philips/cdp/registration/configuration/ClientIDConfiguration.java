package com.philips.cdp.registration.configuration;

import com.philips.cdp.registration.ui.utils.RLog;

import java.util.HashMap;
import java.util.Map;

public class ClientIDConfiguration {

    private final String TAG = "ClientIDConfiguration";

    private final String DEV_CAPTURE_DOMAIN = "DEVELOPMENT";

    private final String TEST_CAPTURE_DOMAIN = "TESTING";

    private final String EVAL_CAPTURE_DOMAIN = "EVALUATION";

    private final String PROD_CAPTURE_DOMAIN = "PRODUCTION";

    private final String STAG_CAPTURE_DOMAIN = "STAGING";


    private final String DEV_CAPTURE_DOMAIN_CHINA = "DEVELOPMENT_CN";

    private final String DEV_CAPTURE_DOMAIN_CHINA_EU = "https://philips-china-eu.eu-dev.janraincapture.biz";

    private final String TEST_CAPTURE_DOMAIN_CHINA = "TESTING_CN";

    private final String TEST_CAPTURE_DOMAIN_CHINA_EU = "https://philips-china-test.eu-dev.janraincapture.biz";

    private final String EVAL_CAPTURE_DOMAIN_CHINA = "EVALUATION_CN";

    private final String PROD_CAPTURE_DOMAIN_CHINA = "PRODUCTION_CN";

    private final String STAG_CAPTURE_DOMAIN_CHINA = "STAGING_CN";


    private final String DEV_RUSSIA_CAPTURE_DOMAIN = "DEVELOPMENT_RU";

    private final String TEST_RUSSIA_CAPTURE_DOMAIN = "TESTING_RU";

    private final String EVAL_RUSSIA_CAPTURE_DOMAIN = "EVALUATION_RU";

    private final String PROD_RUSSIA_CAPTURE_DOMAIN = "PRODUCTION_RU";

    private final String STAG_RUSSIA_CAPTURE_DOMAIN = "STAGING_RU";


    private  final String DEV_CAPTURE_DOMAIN_CAPTUE_ID = "eupac7ugz25x8dwahvrbpmndf8";
    private  final String TEST_CAPTURE_DOMAIN_CAPTUE_ID = "x7nftvwfz8e8vcutz49p8eknqp";
    private  final String EVAL_CAPTURE_DOMAIN_CAPTUE_ID = "nt5dqhp6uck5mcu57snuy8uk6c";
    private  final String STAG_CAPTURE_DOMAIN_CAPTUE_ID = "nt5dqhp6uck5mcu57snuy8uk6c";
    private  final String PROD_CAPTURE_DOMAIN_CAPTUE_ID = "hffxcm638rna8wrxxggx2gykhc";


    private  final String DEV_CAPTURE_DOMAIN_ENAGAGE_ID = "bdbppnbjfcibijknnfkk";
    private  final String TEST_CAPTURE_DOMAIN_ENAGAGE_ID = "fhbmobeahciagddgfidm";
    private  final String EVAL_CAPTURE_DOMAIN_ENAGAGE_ID = "jgehpoggnhbagolnihge";
    private  final String STAG_CAPTURE_DOMAIN_ENAGAGE_ID = "jgehpoggnhbagolnihge";
    private  final String PROD_CAPTURE_DOMAIN_ENAGAGE_ID = "ddjbpmgpeifijdlibdio";


    private static final String DEV_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID = "rj95w5ghxqthxxy8jpug5a63wrbeykzk";
    private static final String TEST_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID = "suxgtg52ej3srf683t7u5gqzw4824avg";
    private static final String EVAL_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID = "h27n93rjva8xuvzgpeb7jf9jxq6dnnzr";
    private static final String STAG_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID = "h27n93rjva8xuvzgpeb7jf9jxq6dnnzr";
    private static final String PROD_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID = "h27n93rjva8xuvzgpeb7jf9jxq6dnnzr";

    private static final String DEV_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID = "xhrue999syb8g2csggp9acs6x87q8q3d";
    private static final String DEV_CAPTURE_DOMAIN_CHINA_EU_RESET_PASS_CLIENT_ID = "4c5tqzbneykdw2md7mkp75uycp23x3qz";
    private static final String TEST_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID = "v2s8qajf9ncfzsyy6ghjpqvsrju9xgvt";
    private static final String TEST_CAPTURE_DOMAIN_CHINA_EU_RESET_PASS_CLIENT_ID = "fh5mfvjqzwhn5t9gdwqqjnbcw9atd7mv";
    private static final String EVAL_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID = "mfvjprjmgbrhfbtn6cq6q2yupzhxn977";
    private static final String STAG_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID = "mfvjprjmgbrhfbtn6cq6q2yupzhxn977";
    private static final String PROD_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID = "65dzkyh48ux4vcguhvwsgvtk4bzyh2va";


    public String getCaptureId(String domain) {
        HashMap<String, String> map = new HashMap<>();
        map.putAll(addCaptureIdGlobalURLMapping());
        map.putAll(addCaptureIdRussianURLMapping());
        map.putAll(addCaptureIdChinaURLMapping());
        RLog.d(TAG, "getCaptureId: Capture Domain : " + domain);
        RLog.d(TAG, "getCaptureId : Capture Domain Map : " + map.get(domain));
        return map.get(domain);
    }

    private Map<String, String> addCaptureIdGlobalURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(TEST_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(EVAL_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(STAG_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(PROD_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_CAPTUE_ID);
        return map;
    }

    private Map<String, String> addCaptureIdRussianURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_RUSSIA_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(TEST_RUSSIA_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(EVAL_RUSSIA_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(STAG_RUSSIA_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_CAPTUE_ID);
        map.put(PROD_RUSSIA_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_CAPTUE_ID);
        return map;
    }

    private Map<String, String> addCaptureIdChinaURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        String DEV_CAPTURE_DOMAIN_CHINA_CAPTUE_ID = "7629q5uxm2jyrbk7ehuwryj7a4";
        map.put(DEV_CAPTURE_DOMAIN_CHINA, DEV_CAPTURE_DOMAIN_CHINA_CAPTUE_ID);
        String DEV_CAPTURE_DOMAIN_CHINA_EU_CAPTUE_ID = "euwkgsf83m56hqknjxgnranezh";
        map.put(DEV_CAPTURE_DOMAIN_CHINA_EU, DEV_CAPTURE_DOMAIN_CHINA_EU_CAPTUE_ID);
        String TEST_CAPTURE_DOMAIN_CHINA_CAPTUE_ID = "hqmhwxu7jtdcye758vvxux4ryb";
        map.put(TEST_CAPTURE_DOMAIN_CHINA, TEST_CAPTURE_DOMAIN_CHINA_CAPTUE_ID);
        String TEST_CAPTURE_DOMAIN_CHINA_EU_CAPTUE_ID = "vdgkb3z57jpv93mxub34x73mqu";
        map.put(TEST_CAPTURE_DOMAIN_CHINA_EU, TEST_CAPTURE_DOMAIN_CHINA_EU_CAPTUE_ID);
        String EVAL_CAPTURE_DOMAIN_CHINA_CAPTUE_ID = "czwfzs7xh23ukmpf4fzhnksjmd";
        map.put(EVAL_CAPTURE_DOMAIN_CHINA, EVAL_CAPTURE_DOMAIN_CHINA_CAPTUE_ID);
        String STAG_CAPTURE_DOMAIN_CHINA_CAPTUE_ID = "czwfzs7xh23ukmpf4fzhnksjmd";
        map.put(STAG_CAPTURE_DOMAIN_CHINA, STAG_CAPTURE_DOMAIN_CHINA_CAPTUE_ID);
        String PROD_CAPTURE_DOMAIN_CHINA_CAPTUE_ID = "zkr6yg4mdsnt7f8mvucx7qkja3";
        map.put(PROD_CAPTURE_DOMAIN_CHINA, PROD_CAPTURE_DOMAIN_CHINA_CAPTUE_ID);
        return map;
    }

    public String getEngageId(String domain) {
        HashMap<String, String> map = new HashMap<>();
        map.putAll(addEngageIdGlobalURLMapping());
        map.putAll(addEngageIdRussianURLMapping());
        map.putAll(addEngageIdChinaURLMapping());
        RLog.d(TAG, "Engagedi Domain : " + domain);
        RLog.d(TAG, "Engagedi Domain Map :" + map.get(domain));
        return map.get(domain);
    }


    private Map<String, String> addEngageIdGlobalURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(TEST_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(EVAL_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(STAG_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(PROD_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_ENAGAGE_ID);
        return map;
    }

    private Map<String, String> addEngageIdRussianURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_RUSSIA_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(TEST_RUSSIA_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(EVAL_RUSSIA_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(STAG_RUSSIA_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_ENAGAGE_ID);
        map.put(PROD_RUSSIA_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_ENAGAGE_ID);
        return map;
    }

    private Map<String, String> addEngageIdChinaURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        String DEV_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID = "ruaheighoryuoxxdwyfs";
        map.put(DEV_CAPTURE_DOMAIN_CHINA, DEV_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID);
        String DEV_CAPTURE_DOMAIN_CHINA_EU_ENAGAGE_ID = "bdbppnbjfcibijknnfkk";
        map.put(DEV_CAPTURE_DOMAIN_CHINA_EU, DEV_CAPTURE_DOMAIN_CHINA_EU_ENAGAGE_ID);
        String TEST_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID = "jndphelwbhuevcmovqtn";
        map.put(TEST_CAPTURE_DOMAIN_CHINA, TEST_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID);
        String TEST_CAPTURE_DOMAIN_CHINA_EU_ENAGAGE_ID = "fhbmobeahciagddgfidm";
        map.put(TEST_CAPTURE_DOMAIN_CHINA_EU, TEST_CAPTURE_DOMAIN_CHINA_EU_ENAGAGE_ID);
        String EVAL_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID = "uyfpympodtnesxejzuic";
        map.put(EVAL_CAPTURE_DOMAIN_CHINA, EVAL_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID);
        String STAG_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID = "uyfpympodtnesxejzuic";
        map.put(STAG_CAPTURE_DOMAIN_CHINA, STAG_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID);
        String PROD_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID = "cfwaqwuwcwzlcozyyjpa";
        map.put(PROD_CAPTURE_DOMAIN_CHINA, PROD_CAPTURE_DOMAIN_CHINA_ENAGAGE_ID);
        return map;
    }


    public String getResetPasswordClientId(String domain) {
        HashMap<String, String> map = new HashMap<>();
        map.putAll(addResetPasswordGlobalURLMapping());
        map.putAll(addResetPasswordRussianURLMapping());
        map.putAll(addResetPasswordChinaURLMapping());
        RLog.d(TAG, "ResetPasswordClientId Domain : " + domain);
        RLog.d(TAG, "ResetPasswordClientId Domain Map :" + map.get(domain));
        return map.get(domain);
    }


    private Map<String, String> addResetPasswordGlobalURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(TEST_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(EVAL_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(STAG_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(PROD_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        return map;
    }

    private Map<String, String> addResetPasswordRussianURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_RUSSIA_CAPTURE_DOMAIN, DEV_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(TEST_RUSSIA_CAPTURE_DOMAIN, TEST_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(EVAL_RUSSIA_CAPTURE_DOMAIN, EVAL_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(STAG_RUSSIA_CAPTURE_DOMAIN, STAG_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        map.put(PROD_RUSSIA_CAPTURE_DOMAIN, PROD_CAPTURE_DOMAIN_RESET_PASS_CLIENT_ID);
        return map;
    }

    private Map<String, String> addResetPasswordChinaURLMapping() {
        HashMap<String, String> map = new HashMap<>();
        map.put(DEV_CAPTURE_DOMAIN_CHINA, DEV_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID);
        map.put(DEV_CAPTURE_DOMAIN_CHINA_EU, DEV_CAPTURE_DOMAIN_CHINA_EU_RESET_PASS_CLIENT_ID);
        map.put(TEST_CAPTURE_DOMAIN_CHINA, TEST_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID);
        map.put(TEST_CAPTURE_DOMAIN_CHINA_EU, TEST_CAPTURE_DOMAIN_CHINA_EU_RESET_PASS_CLIENT_ID);
        map.put(EVAL_CAPTURE_DOMAIN_CHINA, EVAL_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID);
        map.put(STAG_CAPTURE_DOMAIN_CHINA, STAG_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID);
        map.put(PROD_CAPTURE_DOMAIN_CHINA, PROD_CAPTURE_DOMAIN_CHINA_RESET_PASS_CLIENT_ID);
        return map;
    }
}
