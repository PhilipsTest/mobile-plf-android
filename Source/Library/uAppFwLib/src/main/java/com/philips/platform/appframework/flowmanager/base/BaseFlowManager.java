/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.platform.appframework.flowmanager.base;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.util.Log;

import com.philips.platform.appframework.flowmanager.exceptions.ConditionIdNotSetException;
import com.philips.platform.appframework.flowmanager.exceptions.JsonAlreadyParsedException;
import com.philips.platform.appframework.flowmanager.exceptions.JsonFileNotFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.JsonStructureException;
import com.philips.platform.appframework.flowmanager.exceptions.NoConditionFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoEventFoundException;
import com.philips.platform.appframework.flowmanager.exceptions.NoStateException;
import com.philips.platform.appframework.flowmanager.exceptions.NullEventException;
import com.philips.platform.appframework.flowmanager.exceptions.StateIdNotSetException;
import com.philips.platform.appframework.flowmanager.listeners.FlowManagerListener;
import com.philips.platform.appframework.flowmanager.models.AppFlowEvent;
import com.philips.platform.appframework.flowmanager.models.AppFlowModel;
import com.philips.platform.appframework.flowmanager.models.AppFlowNextState;
import com.philips.platform.appframework.flowmanager.stack.FlowManagerStack;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class BaseFlowManager {

    private Map<String, BaseState> stateMap;
    private Map<String, BaseCondition> conditionMap;
    private Map<String, List<AppFlowEvent>> appFlowMap;
    private BaseState currentState;
    private Context context;
    private String firstState;
    private FlowManagerStack flowManagerStack;
    private String BACK = "back";
    private Handler flowManagerHandler;

    @Deprecated
    /**
     * @deprecated  - Will be absolute soon , request to use default constructor with initialize api
     */
    public BaseFlowManager(final Context context, final String jsonPath) throws JsonAlreadyParsedException {
        parseFlowManagerJson(context, jsonPath, null);
    }

    public BaseFlowManager() {
    }

    /**
     * Should be called to initialize flow manager
     * @param context - context
     * @param jsonPath - Path of Flow manager JSON file
     * @param flowManagerListener - call back listener on parse success
     * @throws JsonFileNotFoundException
     * @throws JsonStructureException
     * @throws JsonAlreadyParsedException
     */

    public void initialize(@NonNull final Context context, @NonNull final String jsonPath, @NonNull final FlowManagerListener flowManagerListener) throws JsonFileNotFoundException, JsonStructureException, JsonAlreadyParsedException {
        if (appFlowMap != null) {
            throw new JsonAlreadyParsedException();
        } else {
            flowManagerHandler = getHandler(context);
            new Thread (new Runnable() {
                    @Override
                    public void run() {
                        parseFlowManagerJson(context, jsonPath, flowManagerListener);
                    }
                }).start();
        }

    }

    public void initialize(@NonNull final Context context, @RawRes final int resId, @NonNull final FlowManagerListener flowManagerListener) throws JsonFileNotFoundException, JsonStructureException, JsonAlreadyParsedException {
        if (appFlowMap != null) {
            throw new JsonAlreadyParsedException();
        } else {
            flowManagerHandler = getHandler(context);
            new Thread (new Runnable() {
                    @Override
                    public void run() {
                        parseFlowManagerJson(context, resId, flowManagerListener);
                    }
                }).start();
        }

    }

    @NonNull
    protected Handler getHandler(Context context) {
        return new Handler(context.getMainLooper());
    }

    private void parseFlowManagerJson(final @NonNull Context context, final @NonNull String jsonPath, final @NonNull FlowManagerListener flowManagerListener) {
        this.context = context;
        mapAppFlowStates(jsonPath);
        flowManagerStack = new FlowManagerStack();
        stateMap = new TreeMap<>();
        conditionMap = new TreeMap<>();
        populateStateMap(stateMap);
        populateConditionMap(conditionMap);
        postCallBack(flowManagerListener);
    }

    private void postCallBack(final FlowManagerListener flowManagerListener) {
        if (flowManagerHandler != null)
            flowManagerHandler.post(getRunnable(flowManagerListener));
    }

    private void parseFlowManagerJson(final @NonNull Context context, final @RawRes int resId, final @NonNull FlowManagerListener flowManagerListener) {
        this.context = context;
        mapAppFlowStates(context, resId);
        flowManagerStack = new FlowManagerStack();
        stateMap = new TreeMap<>();
        conditionMap = new TreeMap<>();
        populateStateMap(stateMap);
        populateConditionMap(conditionMap);
        postCallBack(flowManagerListener);
    }

    /**
     * This method will create and return the object of BaseCondition depending on Condition ID.
     *
     * @param conditionId Condition ID for which the BaseCondition object needs to be created.
     * @return Object of BaseCondition type.
     */
    public BaseCondition getCondition(String conditionId) throws ConditionIdNotSetException {
        if (conditionId != null && conditionId.length() != 0) {
            BaseCondition baseCondition = conditionMap.get(conditionId);
            if (baseCondition != null && baseCondition.getConditionID() != null && baseCondition.getConditionID().length() != 0)
                return baseCondition;
            else
                throw new ConditionIdNotSetException();
        }

        throw new ConditionIdNotSetException();
    }

    public abstract void populateStateMap(final Map<String, BaseState> uiStateMap);

    public abstract void populateConditionMap(final Map<String, BaseCondition> baseConditionMap);

    /**
     * Method to return the Object to BaseState based on AppFlowState ID.
     *
     * @param stateId state ID.
     * @return Object to BaseState if available or 'null'.
     */
    // TODO: Deepthi , enough validation is not done here to check params and return type, make sure enough test cases are added.
    @NonNull
    public BaseState getState(String stateId) {
        return stateMap.get(stateId);
    }

    @NonNull
    public BaseState getCurrentState() {
        return currentState;
    }

    private void setCurrentState(BaseState currentState) {
        this.currentState = currentState;
    }

    /**
     * Method to get object of next BaseState based on the current state of the App.
     *
     * @param currentState current state of the app.
     * @return Object to next BaseState if available or 'null'.
     */
    public BaseState getNextState(BaseState currentState, String eventId) throws NoEventFoundException, NoStateException, NoConditionFoundException, StateIdNotSetException, ConditionIdNotSetException {
        setCurrentState(currentState);
        return getNextState(eventId);
    }

    /**
     *  Method to get next state for passed event id
     * @param eventId
     * @return
     * @throws NoEventFoundException
     * @throws NoStateException
     * @throws NoConditionFoundException
     * @throws StateIdNotSetException
     * @throws ConditionIdNotSetException
     *
     */
    @NonNull
    public BaseState getNextState(String eventId) throws NoEventFoundException, NoStateException, NoConditionFoundException, StateIdNotSetException, ConditionIdNotSetException {
        if (null == eventId)
            throw new NullEventException();
        else if (null != currentState) {
            List<AppFlowEvent> appFlowEvents = getAppFlowEvents(currentState.getStateID());
            BaseState appFlowNextState = getStateForEventID(false, eventId, appFlowEvents);
            if (appFlowNextState != null) {
                setCurrentState(appFlowNextState);
                flowManagerStack.push(appFlowNextState);
                postLog(getClass() + "", "uApp current state ---- " + appFlowNextState.toString() + " with stack size = " + flowManagerStack.size());
                return appFlowNextState;
            }
        }
        if (currentState != null)
            postLog(getClass() + "", "No State Exception with current state = " + currentState.toString() + " with stack size = " + flowManagerStack.size());
        else
            postLog(getClass() + "", "No State Exception with current state = null with stack size = " + flowManagerStack.size());
        throw new NoStateException();
    }

    @Deprecated
    /**
     * @deprecated - Will be absolute soon
     */
    public BaseState getFirstState() throws NoStateException {
        BaseState firstState = stateMap.get(this.firstState);
        if (firstState != null) {
            setCurrentState(firstState);
            flowManagerStack.push(firstState);
            return firstState;
        }
        throw new NoStateException();
    }

    @Nullable
    private BaseState getStateForEventID(boolean isBack, String eventId, List<AppFlowEvent> appFlowEvents) throws NoEventFoundException, NoConditionFoundException, ConditionIdNotSetException {
        String appFlowEventId;
        if (appFlowEvents != null && appFlowEvents.size() != 0) {
            for (final AppFlowEvent appFlowEvent : appFlowEvents) {
                appFlowEventId = appFlowEvent.getEventId();
                if (appFlowEvent.getEventId() != null && appFlowEventId.equalsIgnoreCase(eventId)) {
                    final List<AppFlowNextState> appFlowNextStates = appFlowEvent.getNextStates();
                    return getUiState(appFlowNextStates);
                }
            }
        }
        if (!isBack) {
            if (currentState != null)
                postLog(getClass() + "", "No event Exception with current state = " + currentState.toString() + " with stack size = " + flowManagerStack.size());
            else
                postLog(getClass() + "", "No event Exception with current state = null with stack size = " + flowManagerStack.size());
            throw new NoEventFoundException();
        }
        else
            return null;
    }

    protected boolean postLog(final String key, final String value) {
        Log.i(key, value);
        return true;
    }

    /**
     *  Method to get State while navigating back
     * @param currentState
     * @return
     * @throws NoStateException
     * @throws NoConditionFoundException
     * @throws StateIdNotSetException
     * @throws ConditionIdNotSetException
     */
    public BaseState getBackState(BaseState currentState) throws NoStateException, NoConditionFoundException, StateIdNotSetException, ConditionIdNotSetException {
        return getBackState();
    }

    /**
     *  Method to get State while navigating back
     * @return
     * @throws NoStateException
     * @throws NoConditionFoundException
     * @throws StateIdNotSetException
     * @throws ConditionIdNotSetException
     */
    public BaseState getBackState() throws NoStateException, NoConditionFoundException, StateIdNotSetException, ConditionIdNotSetException {
        if (currentState != null && flowManagerStack.contains(currentState)) {
            List<AppFlowEvent> appFlowEvents = getAppFlowEvents(currentState.getStateID());
            BaseState nextState = null;
            try {
                nextState = getStateForEventID(true, BACK, appFlowEvents);
            } catch (NoEventFoundException e) {
                e.printStackTrace();
            }
            if (nextState != null) {
                if (flowManagerStack.contains(nextState)) {
                    flowManagerStack.pop(nextState);
                    setCurrentState(nextState);
                    return nextState;
                } else {
                    flowManagerStack.clear();
                    setCurrentState(nextState);
                    flowManagerStack.push(nextState);
                    return nextState;
                }
            } else {
                setCurrentState(flowManagerStack.pop());
                return null;
            }
        }
        throw new NoStateException();
    }

    public void clearStates() {
        flowManagerStack.clear();
    }

    private List<AppFlowEvent> getAppFlowEvents(String currentState) throws StateIdNotSetException {
        if (currentState != null && currentState.length() != 0)
            return appFlowMap.get(currentState);

        throw new StateIdNotSetException();
    }

    @Nullable
    private BaseState getUiState(final List<AppFlowNextState> appFlowNextStates) throws NoConditionFoundException, ConditionIdNotSetException {
        for (AppFlowNextState appFlowNextState : appFlowNextStates) {
            List<String> conditionsTypes = appFlowNextState.getCondition();
            boolean isConditionSatisfies = true;
            if (conditionsTypes != null && conditionsTypes.size() > 0) {
                isConditionSatisfies = isConditionSatisfies(conditionsTypes);
            }
            //Return the BaseState if the entry condition is satisfies.
            if (isConditionSatisfies) {
                return getState(appFlowNextState.getNextState());
            }
        }
        return null;
    }

    private boolean isConditionSatisfies(final List<String> conditionsTypes) throws NoConditionFoundException, ConditionIdNotSetException {
        boolean isConditionSatisfies = true;
        for (final String conditionType : conditionsTypes) {
            BaseCondition condition = getCondition(conditionType);
            if (condition != null) {
                isConditionSatisfies = condition.isSatisfied(context);
                if (isConditionSatisfies)
                    break;
            } else {
                throw new NoConditionFoundException();
            }
        }
        return isConditionSatisfies;
    }

    private void mapAppFlowStates(final String jsonPath) throws JsonFileNotFoundException, JsonStructureException {
        final AppFlowParser appFlowParser = new AppFlowParser();
        AppFlowModel appFlowModel = appFlowParser.getAppFlow(jsonPath);
        getFirstStateAndAppFlowMap(appFlowParser, appFlowModel);
    }

    private void mapAppFlowStates(Context context, final int resId) throws JsonFileNotFoundException, JsonStructureException {
        final AppFlowParser appFlowParser = new AppFlowParser(context);
        AppFlowModel appFlowModel = appFlowParser.getAppFlow(resId);
        getFirstStateAndAppFlowMap(appFlowParser, appFlowModel);
    }

    @NonNull
    protected Runnable getRunnable(final FlowManagerListener flowManagerListener) {
        return new Runnable() {
            @Override
            public void run() {
                if (flowManagerListener != null)
                    flowManagerListener.onParseSuccess();
            }
        } ;
    }

    private void getFirstStateAndAppFlowMap(AppFlowParser appFlowParser, AppFlowModel appFlowModel) {
        if (appFlowModel != null && appFlowModel.getAppFlow() != null) {
            firstState = appFlowModel.getAppFlow().getFirstState();
            appFlowMap = appFlowParser.getAppFlowMap(appFlowModel.getAppFlow());
        }
    }
}
