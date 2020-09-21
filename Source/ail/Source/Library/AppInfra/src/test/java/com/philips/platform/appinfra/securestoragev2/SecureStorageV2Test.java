/*
 * Copyright (c) 2015-2018 Koninklijke Philips N.V.
 * All rights reserved.
 */

package com.philips.platform.appinfra.securestoragev2;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import androidx.annotation.NonNull;

import com.philips.platform.appinfra.AppInfra;
import com.philips.platform.appinfra.AppInfraInterface;
import com.philips.platform.appinfra.logging.LoggingInterface;
import com.philips.platform.appinfra.securestorage.SecureStorageInterface;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.security.Key;

import javax.crypto.SecretKey;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by abhishek on 1/24/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 29)
public class SecureStorageV2Test {

    @Mock
    private AppInfra appInfra;

    @Mock
    private LoggingInterface loggingInterface;

    @Mock
    private Context context;

    @Mock
    private SSFileCache ssFileCache;

    @Mock
    private SSEncoderDecoder SSEncoderDecoder;

    @Mock
    private ApplicationInfo applicationInfo;

    @Mock
    private SSKeyProvider ssKeyProvider;

    @Mock
    private SecretKey secretKey;

    @Mock
    private SecureStorageInterface.DataEncryptionListener dataEncryptionListener;

    @Mock
    private SecureStorageInterface.DataDecryptionListener dataDecryptionListener;

    @Mock
    private HandlerThread handlerThread;

    @Mock
    private Handler handler;

    private SecureStorageInterface secureStorage;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(appInfra.getAppInfraContext()).thenReturn(context);
        when(appInfra.getAppInfraLogInstance()).thenReturn(loggingInterface);
        when(context.getApplicationInfo()).thenReturn(applicationInfo);
        when(handler.post(any(Runnable.class))).thenAnswer((Answer) invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        });

        secureStorage = new SecureStorageV2ImplForTest(appInfra);
    }

    @Test
    public void testForEmptyKey() {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        assertFalse(secureStorage.storeValueForKey("", "value", sse));
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey, sse.getErrorCode());
    }

    @Test
    public void testForSpaceKey() {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();

        assertFalse(secureStorage.storeValueForKey(" ", "val", sse)); // value can be empty
        assertFalse(secureStorage.storeValueForKey("   ", "val", sse)); // value can be empty
    }

    @Test
    public void testStoreValueForKey_Should_Return_True() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn("value1".getBytes());
        when(ssFileCache.putEncryptedString(any(String.class), any())).thenReturn(true);

        assertTrue(secureStorage.storeValueForKey("key1", "value1", sse));
    }

    @Test
    public void testStoreValueForKey_KeyProvider_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));

        assertFalse(secureStorage.storeValueForKey("key1", "value1", sse));
    }

    @Test
    public void testStoreValueForKey_EncodeDecode_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new SSEncodeDecodeException(""));

        assertFalse(secureStorage.storeValueForKey("key1", "value1", sse));
    }

    @Test
    public void testStoreValueForKey_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new IllegalArgumentException(""));

        assertFalse(secureStorage.storeValueForKey("key1", "value1", sse));
    }

    @Test
    public void testFetchValueForNullKey() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        assertNull(secureStorage.fetchValueForKey(null, sse));
        assertEquals(sse.getErrorCode(), SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey);
        assertNull(secureStorage.fetchValueForKey("NotSavedKey", sse));
    }


    @Test
    public void testFetchValueForEmptyKey() {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        assertNull(secureStorage.fetchValueForKey("", sse));
        assertEquals(sse.getErrorCode(), SecureStorageInterface.SecureStorageError.secureStorageError.UnknownKey);
    }

    @Test
    public void testFetchValueForKey_Positive_Scenario() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn("value".getBytes());
        when(ssFileCache.getEncryptedString(any(String.class))).thenReturn("ashdjsjas");
        assertEquals("value", secureStorage.fetchValueForKey("key1", sse));
    }


    @Test
    public void testFetchValueForKey_KeyProvider_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));
        ;
        assertNull(secureStorage.fetchValueForKey("key1", sse));

    }

    @Test
    public void testFetchValueForKey_EncodeDecode_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new SSEncodeDecodeException(""));
        when(ssFileCache.getEncryptedString(any(String.class))).thenReturn("ashdjsjas");
        assertNull(secureStorage.fetchValueForKey("key1", sse));

    }

    @Test
    public void testFetchValueForKey_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new IllegalArgumentException(""));
        when(ssFileCache.getEncryptedString(any(String.class))).thenReturn("ashdjsjas");
        assertNull(secureStorage.fetchValueForKey("key1", sse));

    }

    private String getLargeString() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource("big_string");
        File file = new File(resource.getPath());
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }
        return text.toString();
    }

    @Test
    public void removeValueForKey_userKey_NULL_Should_return_false() {
        assertFalse(secureStorage.removeValueForKey(null));
    }

    @Test
    public void removeValueForKey_Should_Return_True() {
        when(ssFileCache.deleteEncryptedData(any(String.class))).thenReturn(true);
        assertTrue(secureStorage.removeValueForKey("abcd"));
    }

    @Test
    public void createKeyTest_Should_Return_True() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenReturn(secretKey);
        assertTrue(secureStorage.createKey(SecureStorageInterface.KeyTypes.AES, "abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void createKeyTest_Should_Return_False_Key_Provider_Exception() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenThrow(new SSKeyProviderException(""));
        assertFalse(secureStorage.createKey(SecureStorageInterface.KeyTypes.AES, "abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void createKeyTest_Should_Return_False_IllegalArgumentException() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenThrow(new IllegalArgumentException());
        assertFalse(secureStorage.createKey(SecureStorageInterface.KeyTypes.AES, "abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void getKeyTest_Should_Return_Key() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenReturn(secretKey);
        assertNotNull(secureStorage.getKey("abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void getKeyTest_Should_Return_Null_Key_Provider_Exception() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenThrow(new SSKeyProviderException(""));
        assertNull(secureStorage.getKey("abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void getKeyTest_Should_Return_Null_IllegalArgumentException() throws Exception {
        when(ssKeyProvider.getSecureKey("abcd")).thenThrow(new IllegalArgumentException());
        assertNull(secureStorage.getKey("abcd", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void testClearKey_Should_return_false_empty_key() {
        assertFalse(secureStorage.clearKey("", new SecureStorageInterface.SecureStorageError()));
    }

    @Test
    public void testClearKey_Should_return_false() {
        assertFalse(secureStorage.clearKey("abcd", new SecureStorageInterface.SecureStorageError()));
    }


    @Test
    public void testByteDataEncryptionForNullValue() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        assertNull(secureStorage.encryptData(null, sse));
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.NullData, sse.getErrorCode());
    }

    @Test
    public void testByteDataEncryption_Should_Not_Be_Null() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        byte[] encryptedBytes = "dcba".getBytes();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn(encryptedBytes);
        assertNotNull(secureStorage.encryptData("abcd".getBytes(), sse));
    }

    @Test
    public void testByteDataEncryption_Should_Return_AccessKeyFailure_Key_Provider_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));
        secureStorage.encryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.AccessKeyFailure, sse.getErrorCode());
    }

    @Test
    public void testByteDataEncryption_Should_Return_EncryptionError_Encode_Decode_exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new SSEncodeDecodeException(""));
        secureStorage.encryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.EncryptionError, sse.getErrorCode());
    }

    @Test
    public void testByteDataEncryption_Should_return_Encryption_Error_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new IllegalArgumentException(""));
        secureStorage.encryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.EncryptionError, sse.getErrorCode());
    }

    @Test
    public void testByteDataDecryptionForNullValue() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        assertNull(secureStorage.decryptData(null, sse));
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.NullData, sse.getErrorCode());
    }

    @Test
    public void testByteDataDecryption_Should_Not_Be_Null() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        byte[] encryptedBytes = "dcba".getBytes();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn(encryptedBytes);
        assertNotNull(secureStorage.decryptData("abcd".getBytes(), sse));
    }

    @Test
    public void testByteDataDecryption_Should_Return_AccessKeyFailure_Key_Provider_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));
        secureStorage.decryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.AccessKeyFailure, sse.getErrorCode());
    }

    @Test
    public void testByteDataDecryption_Should_Return_EncryptionError_Encode_Decode_exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new SSEncodeDecodeException(""));
        secureStorage.decryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.DecryptionError, sse.getErrorCode());
    }

    @Test
    public void testByteDataDecryption_Should_return_Encryption_Error_Exception() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenThrow(new IllegalArgumentException(""));
        secureStorage.decryptData("abcd".getBytes(), sse);
        assertEquals(SecureStorageInterface.SecureStorageError.secureStorageError.DecryptionError, sse.getErrorCode());
    }

    @Test
    public void testEncryptAsyncBulkData() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        byte[] encryptedBytes = "dcba".getBytes();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn(encryptedBytes);
        secureStorage.encryptBulkData("abcd".getBytes(), dataEncryptionListener);
        verify(dataEncryptionListener).onEncryptionSuccess(any(byte[].class));
    }

    @Test
    public void testEncryptAsyncBulkData_Error_Scenario() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));
        secureStorage.encryptBulkData("abcd".getBytes(), dataEncryptionListener);
        verify(dataEncryptionListener).onEncryptionError(any(SecureStorageInterface.SecureStorageError.class));
    }

    @Test
    public void testAsyncDecryptBulkData() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        byte[] encryptedBytes = "dcba".getBytes();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenReturn(secretKey);
        when(SSEncoderDecoder.encodeDecodeData(anyInt(), any(Key.class), any(byte[].class))).thenReturn(encryptedBytes);
        secureStorage.decryptBulkData("abcd".getBytes(), dataDecryptionListener);
        verify(dataDecryptionListener).onDecryptionSuccess(any(byte[].class));
    }

    @Test
    public void testAsyncDecryptBulkData_Error_Scenario() throws Exception {
        SecureStorageInterface.SecureStorageError sse = new SecureStorageInterface.SecureStorageError();
        when(ssKeyProvider.getSecureKey(any(String.class))).thenThrow(new SSKeyProviderException(""));
        secureStorage.decryptBulkData("abcd".getBytes(), dataDecryptionListener);
        verify(dataDecryptionListener).onDecyptionError(any(SecureStorageInterface.SecureStorageError.class));
    }

    private class SecureStorageV2ImplForTest extends SecureStorageV2 {

        SecureStorageV2ImplForTest(AppInfraInterface bAppInfra) {
            super(bAppInfra);
        }

        @NonNull
        @Override
        protected SSEncoderDecoder getSSEncoderDecoder() {
            return SSEncoderDecoder;
        }

        @Override
        protected SSFileCache getSecureStorageFileCache() {
            return ssFileCache;
        }

        @Override
        protected SSKeyProvider getSecureStorageKeyprovider() {
            return ssKeyProvider;
        }

        @NonNull
        @Override
        protected Handler getHandler(Looper looper) {
            return handler;
        }

        @NonNull
        @Override
        protected HandlerThread getWorkerThread() {
            return handlerThread;
        }

        @Override
        protected byte[] getDecode(String encryptedString) {
            return "value".getBytes();
        }
    }
}