package com.philips.cdp.registration.configuration;

import com.philips.cdp.registration.ui.utils.RLog;
import com.philips.platform.appinfra.logging.LoggingInterface;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ClientIDConfigurationTest extends TestCase{

    @Mock
    private LoggingInterface mockLoggingInterface;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        RLog.setMockLogger(mockLoggingInterface);
    }

    @Test
    public void testChinaDevelopResetPassword() {
        assertEquals("xhrue999syb8g2csggp9acs6x87q8q3d", new ClientIDConfiguration().getResetPasswordClientId("DEVELOPMENT_CN"));
    }

    @Test
    public void testChinaEUDevelopResetPassword() {
        assertEquals("4c5tqzbneykdw2md7mkp75uycp23x3qz", new ClientIDConfiguration().getResetPasswordClientId("https://philips-china-eu.eu-dev.janraincapture.biz"));
    }

    @Test
    public void testChinaTestResetPassword() {
        assertEquals("v2s8qajf9ncfzsyy6ghjpqvsrju9xgvt", new ClientIDConfiguration().getResetPasswordClientId("TESTING_CN"));
    }

    @Test
    public void testChinaEUTestResetPassword() {
        assertEquals("fh5mfvjqzwhn5t9gdwqqjnbcw9atd7mv", new ClientIDConfiguration().getResetPasswordClientId("https://philips-china-test.eu-dev.janraincapture.biz"));
    }

    @Test
    public void testChinaEvalResetPassword() {
        assertEquals("mfvjprjmgbrhfbtn6cq6q2yupzhxn977", new ClientIDConfiguration().getResetPasswordClientId("EVALUATION_CN"));
    }



    @Test
    public void testChinaProductResetPassword() {
        assertEquals("65dzkyh48ux4vcguhvwsgvtk4bzyh2va", new ClientIDConfiguration().getResetPasswordClientId("PRODUCTION_CN"));
    }




    @Test
    public void testChinaDevelopCaptureId() {
        assertEquals("7629q5uxm2jyrbk7ehuwryj7a4", new ClientIDConfiguration().getCaptureId("DEVELOPMENT_CN"));
    }

    @Test
    public void testChinaEUDevelopCaptureId() {
        assertEquals("euwkgsf83m56hqknjxgnranezh", new ClientIDConfiguration().getCaptureId("https://philips-china-eu.eu-dev.janraincapture.biz"));
    }

    @Test
    public void testChinaTestCaptureId() {
        assertEquals("hqmhwxu7jtdcye758vvxux4ryb", new ClientIDConfiguration().getCaptureId("TESTING_CN"));
    }

    @Test
    public void testChinaEUTestCaptureId() {
        assertEquals("vdgkb3z57jpv93mxub34x73mqu", new ClientIDConfiguration().getCaptureId("https://philips-china-test.eu-dev.janraincapture.biz"));
    }

    @Test
    public void testChinaEvalCaptureId() {
        assertEquals("czwfzs7xh23ukmpf4fzhnksjmd", new ClientIDConfiguration().getCaptureId("EVALUATION_CN"));
    }



    @Test
    public void testChinaProductCaptureId() {
        assertEquals("zkr6yg4mdsnt7f8mvucx7qkja3", new ClientIDConfiguration().getCaptureId("PRODUCTION_CN"));
    }

    @Test
    public void testChinaDevelopEngageId() {
        assertEquals("ruaheighoryuoxxdwyfs", new ClientIDConfiguration().getEngageId("DEVELOPMENT_CN"));
    }

    @Test
    public void testChinaEUDevelopEngageId() {
        assertEquals("bdbppnbjfcibijknnfkk", new ClientIDConfiguration().getEngageId("DEVELOPMENT"));
    }

    @Test
    public void testChinaTestEngageId() {
        assertEquals("jndphelwbhuevcmovqtn", new ClientIDConfiguration().getEngageId("TESTING_CN"));
    }

    @Test
    public void testChinaEUTestEngageId() {
        assertEquals("fhbmobeahciagddgfidm", new ClientIDConfiguration().getEngageId("TESTING"));
    }

    @Test
    public void testChinaEvalEngageId() {
        assertEquals("uyfpympodtnesxejzuic", new ClientIDConfiguration().getEngageId("STAGING_CN"));
    }



    @Test
    public void testChinaProductEngageId() {
        assertEquals("cfwaqwuwcwzlcozyyjpa", new ClientIDConfiguration().getEngageId("PRODUCTION_CN"));
    }







    @Test
    public void testRussiaDevelopCaptureId() {
        assertEquals("eupac7ugz25x8dwahvrbpmndf8", new ClientIDConfiguration().getCaptureId("DEVELOPMENT"));
    }
    @Test
    public void testRussiaTestCaptureId() {
        assertEquals("x7nftvwfz8e8vcutz49p8eknqp", new ClientIDConfiguration().getCaptureId("TESTING"));
    }

    @Test
    public void testRussiaEvalCaptureId() {
        assertEquals("nt5dqhp6uck5mcu57snuy8uk6c", new ClientIDConfiguration().getCaptureId("EVALUATION"));
    }

    @Test
    public void testRussiaProductCaptureId() {
        assertEquals("hffxcm638rna8wrxxggx2gykhc", new ClientIDConfiguration().getCaptureId("PRODUCTION"));
    }



    @Test
    public void testRussiaDevelopEngageId() {
        assertEquals("bdbppnbjfcibijknnfkk", new ClientIDConfiguration().getEngageId("DEVELOPMENT"));
    }
    @Test
    public void testRussiaTestEngageId() {
        assertEquals("fhbmobeahciagddgfidm", new ClientIDConfiguration().getEngageId("TESTING"));
    }

    @Test
    public void testRussiaEvalEngageId() {
        assertEquals("jgehpoggnhbagolnihge", new ClientIDConfiguration().getEngageId("EVALUATION"));
    }

    @Test
    public void testRussiaProductEngageId() {
        assertEquals("ddjbpmgpeifijdlibdio", new ClientIDConfiguration().getEngageId("PRODUCTION"));
    }


    @Test
    public void testRussialDevelopResetPassword() {
        assertEquals("rj95w5ghxqthxxy8jpug5a63wrbeykzk", new ClientIDConfiguration().getResetPasswordClientId("DEVELOPMENT"));
    }
    @Test
    public void testRussiaTestResetPassword() {
        assertEquals("suxgtg52ej3srf683t7u5gqzw4824avg", new ClientIDConfiguration().getResetPasswordClientId("TESTING"));
    }

    @Test
    public void testRussiaEvalResetPassword() {
        assertEquals("h27n93rjva8xuvzgpeb7jf9jxq6dnnzr", new ClientIDConfiguration().getResetPasswordClientId("EVALUATION_RU"));
    }

    @Test
    public void testRussiaProductResetPassword() {
        assertEquals("h27n93rjva8xuvzgpeb7jf9jxq6dnnzr", new ClientIDConfiguration().getResetPasswordClientId("STAGING_RU"));
    }

    @Test
    public void testGlobalDevelopCaptureId() {
        assertEquals("eupac7ugz25x8dwahvrbpmndf8", new ClientIDConfiguration().getCaptureId("DEVELOPMENT"));
    }
    @Test
    public void testGlobalTestCaptureId() {
        assertEquals("x7nftvwfz8e8vcutz49p8eknqp", new ClientIDConfiguration().getCaptureId("TESTING"));
    }

    @Test
    public void testGlobalEvalCaptureId() {
        assertEquals("nt5dqhp6uck5mcu57snuy8uk6c", new ClientIDConfiguration().getCaptureId("STAGING_RU"));
    }

    @Test
    public void testGlobalProductCaptureId() {
        assertEquals("hffxcm638rna8wrxxggx2gykhc", new ClientIDConfiguration().getCaptureId("PRODUCTION"));
    }



    @Test
    public void testGlobalDevelopEngageId() {
        assertEquals("bdbppnbjfcibijknnfkk", new ClientIDConfiguration().getEngageId("DEVELOPMENT"));
    }
    @Test
    public void testGlobalTestEngageId() {
        assertEquals("fhbmobeahciagddgfidm", new ClientIDConfiguration().getEngageId("TESTING"));
    }

    @Test
    public void testGlobalEvalEngageId() {
        assertEquals("jgehpoggnhbagolnihge", new ClientIDConfiguration().getEngageId("STAGING"));
    }

    @Test
    public void testGlobalProductEngageId() {
        assertEquals("ddjbpmgpeifijdlibdio", new ClientIDConfiguration().getEngageId("PRODUCTION_RU"));
    }


    @Test
    public void testGlobalDevelopResetPassword() {
        assertEquals("rj95w5ghxqthxxy8jpug5a63wrbeykzk", new ClientIDConfiguration().getResetPasswordClientId("DEVELOPMENT"));
    }
    @Test
    public void testGlobalTestResetPassword() {
        assertEquals("suxgtg52ej3srf683t7u5gqzw4824avg", new ClientIDConfiguration().getResetPasswordClientId("TESTING"));
    }

    @Test
    public void testGlobalEvalResetPassword() {
        assertEquals("h27n93rjva8xuvzgpeb7jf9jxq6dnnzr", new ClientIDConfiguration().getResetPasswordClientId("STAGING"));
    }

    @Test
    public void testGlobalProductResetPassword() {
        assertEquals("h27n93rjva8xuvzgpeb7jf9jxq6dnnzr", new ClientIDConfiguration().getResetPasswordClientId("EVALUATION"));
    }

}