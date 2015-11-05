/*
 * © Koninklijke Philips N.V., 2015.
 *   All rights reserved.
 */

package com.philips.cdp.dicommclient.port.common;

import com.philips.cdp.dicommclient.communication.CommunicationStrategy;
import com.philips.cdp.dicommclient.networknode.NetworkNode;
import com.philips.cdp.dicommclient.port.DICommPort;
import com.philips.cdp.dicommclient.request.Error;
import com.philips.cdp.dicommclient.request.ResponseHandler;
import com.philips.cdp.dicommclient.util.DICommLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ScheduleListPort extends DICommPort<ScheduleListPortInfo> {

    private static final String KEY_SCHEDULECOMMAND = "command";
    private static final String KEY_SCHEDULEPORT = "port";
    private static final String KEY_SCHEDULEPRODUCTID = "product";
    private static final String KEY_SCHEDULEDAYS = "days";
    private static final String KEY_SCHEDULETIME = "time";
    private static final String KEY_SCHEDULEENABLED = "enabled";
    private static final String KEY_SCHEDULENAME = "name";

    private final String SCHEDULELISTPORT_NAME = "schedules";
    private final int SCHEDULELISTPORT_PRODUCTID = 0;
    private List<ScheduleListPortInfo> mSchedulerPortInfoList;
    private SchedulePortListener mSchedulePortListener;

    public static final String ERROR_OUT_OF_MEMORY = "out of memory";
    public static final int MAX_SCHEDULES_REACHED = 1;
    public static final int DEFAULT_ERROR = 999;

    public ScheduleListPort(NetworkNode networkNode, CommunicationStrategy communicationStrategy) {
        super(networkNode, communicationStrategy);
    }

    @Override
    public boolean isResponseForThisPort(String jsonResponse) {
        if (parseResponseAsSingleSchedule(jsonResponse) != null) return true;
        if (parseResponseAsScheduleList(jsonResponse) != null) return true;
        return false;
    }

    @Override
    public void processResponse(String jsonResponse) {
        //TODO: DIComm Refactor, implement
        DICommLog.e(DICommLog.SCHEDULELISTPORT, "Method Not Implemented, SchedulerActivity should be refactored");
    }

    @Override
    public String getDICommPortName() {
        return SCHEDULELISTPORT_NAME;
    }

    @Override
    public int getDICommProductId() {
        return SCHEDULELISTPORT_PRODUCTID;
    }

    @Override
    public boolean supportsSubscription() {
        return false;
    }

    public List<ScheduleListPortInfo> getSchedulePortInfoList() {
        return mSchedulerPortInfoList;
    }

    public void setSchedulePortInfoList(List<ScheduleListPortInfo> schedulePortInfoList) {
        mSchedulerPortInfoList = schedulePortInfoList;
    }

    private String getDICommNestedPortName(int scheduleNumber) {
        return String.format("%s/%d", getDICommPortName(), scheduleNumber);
    }

    public void setSchedulePortListener(SchedulePortListener listener) {
        mSchedulePortListener = listener;
    }

    public void clearSchedulePortListener() {
        mSchedulePortListener = null;
    }

    public void getSchedules() {
        mCommunicationStrategy.getProperties(getDICommPortName(), getDICommProductId(), mNetworkNode, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleSuccessResponse(data);
            }

            @Override
            public void onError(Error error, String errorData) {
                handleErrorResponse();
            }
        });
    }

    public void getScheduleDetails(int scheduleNumber) {
        mCommunicationStrategy.getProperties(getDICommNestedPortName(scheduleNumber), getDICommProductId(), mNetworkNode, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleSuccessResponse(data);
            }

            @Override
            public void onError(Error error, String errorData) {
                handleErrorResponse();
            }
        });
    }

    //todo: extra param 'name' should be added
    public void addSchedule(String portName, int productId, String time, String days, boolean enabled, Map<String, Object> commandMap) {
        Map<String, Object> dataMap = createDataMap(portName, productId, time, days, enabled, commandMap);

        mCommunicationStrategy.addProperties(dataMap, getDICommPortName(), getDICommProductId(), mNetworkNode, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleSuccessResponse(data);
            }

            @Override
            public void onError(Error error, String errorData) {
                handleErrorResponse();
            }
        });
    }

    //todo: extra param 'name' should be added
    public void updateSchedule(int scheduleNumber, String portName, int productId, String time, String days, boolean enabled, Map<String, Object> commandMap) {
        Map<String, Object> dataMap = createDataMap(portName, productId, time, days, enabled, commandMap);

        mCommunicationStrategy.putProperties(dataMap, getDICommNestedPortName(scheduleNumber), getDICommProductId(), mNetworkNode, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleSuccessResponse(data);
            }

            @Override
            public void onError(Error error, String errorData) {
                handleErrorResponse();
            }
        });
    }

    public void deleteSchedule(int scheduleNumber) {
        mCommunicationStrategy.deleteProperties(getDICommNestedPortName(scheduleNumber), getDICommProductId(), mNetworkNode, new ResponseHandler() {

            @Override
            public void onSuccess(String data) {
                handleSuccessResponse(data);
            }

            @Override
            public void onError(Error error, String errorData) {
                handleErrorResponse();
            }
        });
    }

    //todo: add extra param 'name'
    private Map<String, Object> createDataMap(String portName, int productId, String time, String days, boolean enabled, Map<String, Object> commandMap) {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(KEY_SCHEDULENAME, time); //todo: use the name param here
        dataMap.put(KEY_SCHEDULEENABLED, enabled);
        dataMap.put(KEY_SCHEDULETIME, time);
        dataMap.put(KEY_SCHEDULEDAYS, days);
        dataMap.put(KEY_SCHEDULEPRODUCTID, productId);
        dataMap.put(KEY_SCHEDULEPORT, portName);
        dataMap.put(KEY_SCHEDULECOMMAND, commandMap);
        return dataMap;
    }

    private void handleErrorResponse() {
        if (mSchedulePortListener != null) {
            mSchedulePortListener.onError(ScheduleListPort.DEFAULT_ERROR);
        }
    }

    private void handleSuccessResponse(String data) {
        ScheduleListPortInfo schedulePortInfo = parseResponseAsSingleSchedule(data);
        if (schedulePortInfo != null && mSchedulePortListener != null) {
            mSchedulePortListener.onScheduleReceived(schedulePortInfo);
            return;
        }
        List<ScheduleListPortInfo> schedulePortInfoList = parseResponseAsScheduleList(data);
        if (schedulePortInfoList != null && mSchedulePortListener != null) {
            mSchedulePortListener.onSchedulesReceived(schedulePortInfoList);
            return;
        }

        if (data.contains(ScheduleListPort.ERROR_OUT_OF_MEMORY)) {
            if (mSchedulePortListener != null) {
                mSchedulePortListener.onError(ScheduleListPort.MAX_SCHEDULES_REACHED);
            }
        }
    }

    ScheduleListPortInfo parseResponseAsSingleSchedule(String response) {
        //TODO: DIComm Refactor
        if (response == null || response.isEmpty()) return null;
        ScheduleListPortInfo scheduleListPortInfo = new ScheduleListPortInfo();
        DICommLog.i(DICommLog.SCHEDULELISTPORT, response);
        try {
            JSONObject scheduleJson = new JSONObject(response);
            JSONObject scheduleJsonViaCPP = scheduleJson.optJSONObject("data");
            if (scheduleJsonViaCPP != null) {
                scheduleJson = scheduleJsonViaCPP;
            }
            scheduleListPortInfo.setName(scheduleJson.getString(KEY_SCHEDULENAME));
            scheduleListPortInfo.setEnabled(scheduleJson.getBoolean(KEY_SCHEDULEENABLED));
            scheduleListPortInfo.setDays(scheduleJson.getString(KEY_SCHEDULEDAYS));

            //TODO: mode is not defined, command is.. furthermore it should be string not jsonobject
            scheduleListPortInfo.setMode(scheduleJson.getJSONObject(KEY_SCHEDULECOMMAND).getString("om"));
            scheduleListPortInfo.setScheduleTime(scheduleJson.getString(KEY_SCHEDULETIME));
        } catch (JSONException e) {
            scheduleListPortInfo = null;
            DICommLog.e(DICommLog.SCHEDULELISTPORT, "Exception: " + "Error: " + e.getMessage());
        } catch (Exception e) {
            scheduleListPortInfo = null;
            DICommLog.e(DICommLog.SCHEDULELISTPORT, "Exception: " + "Error: " + e.getMessage());
        }
        return scheduleListPortInfo;
    }

    List<ScheduleListPortInfo> parseResponseAsScheduleList(String response) {
        //TODO: DIComm Refactor
        if (response == null || response.isEmpty()) return null;
        DICommLog.i(DICommLog.SCHEDULELISTPORT, response);
        List<ScheduleListPortInfo> schedulesList = new ArrayList<ScheduleListPortInfo>();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONObject schedulerJsonFromCPP = jsonObject.optJSONObject("data");
            if (schedulerJsonFromCPP != null) {
                jsonObject = schedulerJsonFromCPP;
            }
            Iterator<String> iterator = jsonObject.keys();
            String key = null;
            while (iterator.hasNext()) {
                key = iterator.next();
                ScheduleListPortInfo schedules = new ScheduleListPortInfo();
                JSONObject schedule;
                schedule = jsonObject.getJSONObject(key);
                schedules.setName((String) schedule.get(KEY_SCHEDULENAME));
                schedules.setScheduleNumber(Integer.parseInt(key));
                schedulesList.add(schedules);
            }
        } catch (JSONException e) {
            schedulesList = null;
            DICommLog.e(DICommLog.SCHEDULELISTPORT, "JsonIOException: " + "Error: " + e.getMessage());
        } catch (Exception e) {
            schedulesList = null;
            DICommLog.e(DICommLog.SCHEDULELISTPORT, "JsonIOException : " + "Error: " + e.getMessage());
        }
        return schedulesList;
    }
}
