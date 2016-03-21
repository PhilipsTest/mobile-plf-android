/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.security;

import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.util.DICommLog;

import java.nio.charset.Charset;

public class DISecurity {

    public interface EncryptionDecryptionFailedListener {
        void onDecryptionFailed(NetworkNode networkNode);

        void onEncryptionFailed(NetworkNode networkNode);
    }

    private EncryptionDecryptionFailedListener mEncryptionDecryptionFailedListener;

    public void setEncryptionDecryptionFailedListener(EncryptionDecryptionFailedListener encryptionDecryptionFailedListener) {
        this.mEncryptionDecryptionFailedListener = encryptionDecryptionFailedListener;
    }

    public String encryptData(String data, NetworkNode networkNode) {
        if (networkNode == null) {
            DICommLog.i(DICommLog.SECURITY, "Did not encrypt data - NetworkNode is null");
            return null;
        }

        String key = networkNode.getEncryptionKey();
        if (key == null || key.isEmpty()) {
            DICommLog.i(DICommLog.SECURITY, "Did not encrypt data - Key is null or Empty");
            return null;
        }
        if (data == null || data.isEmpty()) {
            DICommLog.i(DICommLog.SECURITY, "Did not encrypt data - Data is null or Empty");
            return null;
        }

        String encryptedBase64Str = null;
        try {
            byte[] encrypDatas = EncryptionUtil.aesEncryptData(data, key);
            encryptedBase64Str = ByteUtil.encodeToBase64(encrypDatas);
            DICommLog.i(DICommLog.SECURITY, "Encrypted data: " + encryptedBase64Str);
        } catch (Exception e) {
            e.printStackTrace();
            DICommLog.i(DICommLog.SECURITY, "Failed to encrypt data with key - " + "Error: " + e.getMessage());
        }
        return encryptedBase64Str;
    }

    public String decryptData(String data, NetworkNode networkNode) {
        DICommLog.i(DICommLog.SECURITY, "decryptData data:  " + data);

        if (networkNode == null) {
            DICommLog.i(DICommLog.SECURITY, "Did not encrypt data - NetworkNode is null");
            return null;
        }
        String key = networkNode.getEncryptionKey();
        DICommLog.i(DICommLog.SECURITY, "Decryption - Key   " + key);
        String decryptData = null;

        if (data == null || data.isEmpty()) {
            DICommLog.i(DICommLog.SECURITY, "Did not decrypt data - data is null");
            return null;
        }

        if (key == null || key.isEmpty()) {
            DICommLog.i(DICommLog.SECURITY, "Did not decrypt data - key is null");
            DICommLog.i(DICommLog.SECURITY, "Failed to decrypt data");

            notifyDecryptionFailedListener(networkNode);
            return null;
        }

        data = data.trim();

        try {
            byte[] bytesEncData = ByteUtil.decodeFromBase64(data.trim());
            byte[] bytesDecData = EncryptionUtil.aesDecryptData(bytesEncData, key);
            //For remove random bytes
            byte[] bytesDecData1 = ByteUtil.removeRandomBytes(bytesDecData);

            decryptData = new String(bytesDecData1, Charset.defaultCharset());

            DICommLog.i(DICommLog.SECURITY, "Decrypted data: " + decryptData);
        } catch (Exception e) {
            e.printStackTrace();
            DICommLog.i(DICommLog.SECURITY, "Failed to decrypt data");
        }

        if (decryptData == null) {
            notifyDecryptionFailedListener(networkNode);
        }

        return decryptData;
    }

    private void notifyDecryptionFailedListener(NetworkNode networkNode) {
        if (mEncryptionDecryptionFailedListener != null) {
            mEncryptionDecryptionFailedListener.onDecryptionFailed(networkNode);
        }
    }

    public void notifyEncryptionFailedListener(NetworkNode networkNode) {
        if (mEncryptionDecryptionFailedListener != null) {
            mEncryptionDecryptionFailedListener.onEncryptionFailed(networkNode);
        }
    }
}
