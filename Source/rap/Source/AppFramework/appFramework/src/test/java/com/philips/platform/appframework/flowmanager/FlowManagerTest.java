package com.philips.platform.appframework.flowmanager;

import com.philips.platform.appframework.flowmanager.base.BaseCondition;
import com.philips.platform.appframework.flowmanager.base.BaseState;

import junit.framework.TestCase;

import org.junit.Before;

import java.util.HashMap;
import java.util.Map;


public class FlowManagerTest extends TestCase {

    FlowManager flowManager;
    Map<String, BaseState> uiStateMap;
    Map<String, BaseCondition> baseConditionMap;

    @Before
    public void setUp() throws Exception{
        super.setUp();
        flowManager = new FlowManager();
        uiStateMap = new HashMap<>();
        baseConditionMap = new HashMap<>();
    }

    public void testStateMapExists(){
        flowManager.populateStateMap(uiStateMap);
        assertTrue(uiStateMap.size() > 0);
    }

    public void testPopulateStateMap(){
        flowManager.populateStateMap(uiStateMap);
        assertEquals(29, uiStateMap.size());
    }

    public void testConditionMapExists(){
        flowManager.populateConditionMap(baseConditionMap);
        assertTrue(baseConditionMap.size() > 0);
    }

    public void testConditionStateMap(){
        flowManager.populateConditionMap(baseConditionMap);
        assertEquals(4, baseConditionMap.size());
    }


}
