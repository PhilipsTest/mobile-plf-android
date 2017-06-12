/*
 * (C) 2015-2017 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.cdp2.commlib.lan.communication;

import android.util.Base64;

import com.philips.cdp.dicommclient.testutil.RobolectricTest;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The test for verifing the PublicKeyPin.
 * This is copied from TrustKit (https://github.com/datatheorem/TrustKit-Android/blob/master/trustkit/src/androidTest/java/com/datatheorem/android/trustkit/config/PublicKeyPinTest.java @ bf638e2)
 */
public class PublicKeyPinTest extends RobolectricTest {
    @Test
    public void testFromCertificate() throws CertificateException {
        String pemCertificate =
                "MIIDGTCCAgGgAwIBAgIJAI1jD1qixIPLMA0GCSqGSIb3DQEBBQUAMCMxITAfBgNV\n" +
                        "BAMMGGV2aWxjZXJ0LmRhdGF0aGVvcmVtLmNvbTAeFw0xNTEyMjAxMzU4NDNaFw0y\n" +
                        "NTEyMTcxMzU4NDNaMCMxITAfBgNVBAMMGGV2aWxjZXJ0LmRhdGF0aGVvcmVtLmNv\n" +
                        "bTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMdltqsRJtO7Nqypkehh\n" +
                        "4DSEirp9RM+hJXkBE9nRleTO+utV/snWqX/0wsUrz0wgWyPnAHybGOOXvkrWfXSt\n" +
                        "c2/8PyONOeFEU/9S/lWBXGZkaPhgTvkEzPmOOhf06rBMTwXUMGNDI45gKFgkO6Br\n" +
                        "bGPeSCuheQj0TKeWdwwNoJ+kczUE06IKu2tcuFRjHXci6VeHjANJzrfKro4ivIRy\n" +
                        "bewOGJj1onnpKbui/EOytsmW9MPpOSEXMoVksHOKBQ9nhpL6cDODRvG+t8u7qfFt\n" +
                        "mhphemK3IYNMNA4MMXpbJ+Au2hnPApZPEOit34bAwOiGi/batcS3iA+nl06dPYA9\n" +
                        "nPkCAwEAAaNQME4wHQYDVR0OBBYEFANxdSXS1JSvjdNtNbYBbRlgii93MB8GA1Ud\n" +
                        "IwQYMBaAFANxdSXS1JSvjdNtNbYBbRlgii93MAwGA1UdEwQFMAMBAf8wDQYJKoZI\n" +
                        "hvcNAQEFBQADggEBAAM78Bt2aLUgl2Yq4KMIGDeHdWYcRB7QPQ8sp3Q1TOQQzw0i\n" +
                        "AukRccl9iYNLgaSJDvlVMapD76jo3okydoWgDogWJhtZpMU/9xegIpukmu5hvF6i\n" +
                        "NpqE99PFO5E8BpMkNz+2nskwu//D0as6P9F3tA/o3jC6n6fWX0gt/e9th2ZgVwNQ\n" +
                        "9JTH1ZcyFbX9hdBI4xPAtzFX51AsSa8dpRdG+8DmI41Q/1ludoMZboExHldlUbQH\n" +
                        "zUuHKF8/T+aNo/9FfpqDz1fFnuoF7tuwyRh73B0YDyDVTNuq7LJ4tmzpVvqIt2tn\n" +
                        "RJnQoL4pLQ40SQsoUi4FYG/gxJMoQX6ROWe2nyg=";
        Certificate cert = certificateFromPem(pemCertificate);
        PublicKeyPin pin = new PublicKeyPin(cert);
        assertEquals("Ckvh+UFO2eHunqaB2w0jsrwrJJQcSoES+p9FUhVoszQ=", pin.toString());
    }

    @Test
    public void testFromString() {
        PublicKeyPin pin =
                new PublicKeyPin("rFjc3wG7lTZe43zeYTvPq8k4xdDEutCmIhI5dn4oCeE=");
        assertEquals(pin.toString(), "rFjc3wG7lTZe43zeYTvPq8k4xdDEutCmIhI5dn4oCeE=");
    }

    @Test
    public void testFromBadStringNotBase64() {
        boolean didReturnError = false;
        try {
            new PublicKeyPin("rFjc3wG7lTZe43zeYTvPq8k4xdDEutCmIh!5dn4oCeE=");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("bad base-64")) {
                didReturnError = true;
            } else {
                throw e;
            }
        }
        assertTrue(didReturnError);
    }

    @Test
    public void testFromBadStringBadLength() {
        boolean didReturnError = false;
        try {
            new PublicKeyPin("ZW5jb2U=");
        } catch (IllegalArgumentException e) {
            if (e.getMessage().startsWith("Invalid pin")) {
                didReturnError = true;
            } else {
                throw e;
            }
        }
        assertTrue(didReturnError);
    }

    private static Certificate certificateFromPem(String pemCertificate) {
        pemCertificate = pemCertificate.replace("-----BEGIN CERTIFICATE-----\n", "");
        pemCertificate = pemCertificate.replace("-----END CERTIFICATE-----", "");
        InputStream is = new ByteArrayInputStream(Base64.decode(pemCertificate, Base64.DEFAULT));
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            return cf.generateCertificate(is);
        } catch (CertificateException e) {
            throw new RuntimeException("Should never happen");
        }
    }
}