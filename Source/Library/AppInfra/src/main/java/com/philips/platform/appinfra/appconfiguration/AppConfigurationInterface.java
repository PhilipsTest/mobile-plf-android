/* Copyright (c) Koninklijke Philips N.V. 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.appinfra.appconfiguration;

/**
 * Created by 310238114 on 7/25/2016.
 */
public interface AppConfigurationInterface {

    /**
     * Gets property for key.
     *
     * @param key         the group name
     * @param group       the key
     * @param configError the config configError as OUT parameter
     * @return the value for key mapped by name, or null if no such mapping exists
     * if value in literal then 'String' Object is returned
     * if value in number then 'Integer' Object is returned
     * if value is array of literal then 'array of String' Object is returned
     * if value is array of number then 'array of Integer' Object is returned
     *  HashMap<String,String> is returned if value is so</>
     *  HashMap<String,Integer> is returned if value is so</>
     */
    Object getPropertyForKey(String key, String group, AppConfigurationError configError) throws
            IllegalArgumentException;

    /**
     * Sets property for key.
     *
     * @param key         the group name
     * @param group       the key
     * @param object      the object (String/Integer/String[]/Integer[]/null/HashMap<String,String>/HashMap<String,Integer></>)   null to be passed to delete key
     * @param configError the configError object as OUT parameter
     * @return the set operation status (success/failure)
     */
    boolean setPropertyForKey(String key, String group, Object object, AppConfigurationError configError)
            throws IllegalArgumentException;


    /**
     * Gets property for key from static json file.
     *
     * @param key         the group name
     * @param group       the key
     * @param configError the config configError as OUT parameter
     * @return the value for key mapped by name, or null if no such mapping exists
     * if value in literal then 'String' Object is returned
     * if value in number then 'Integer' Object is returned
     * if value is array of literal then 'array of String' Object is returned
     * if value is array of number then 'array of Integer' Object is returned
     */
    Object getDefaultPropertyForKey(String key, String group, AppConfigurationError configError) throws IllegalArgumentException;

    /**
     * The type Config error.
     */
    public class AppConfigurationError {
        /**
         * The enum Config error enum.
         */
        public enum AppConfigErrorEnum {
            InvalidKey, GroupNotExists, KeyNotExists, FatalError, DeviceStoreError,
            NoDataFoundForKey, SecureStorageError
        }

        private AppConfigErrorEnum errorCode = null;

        /**
         * Gets error code.
         *
         * @return the error code
         */
        public AppConfigErrorEnum getErrorCode() {
            return errorCode;
        }

        /**
         * Sets error code.
         *
         * @param errorCode the error code
         */
        void setErrorCode(AppConfigErrorEnum errorCode) {
            this.errorCode = errorCode;
        }

    }
}
