package com.philips.cdp.model;

import com.google.gson.Gson;

import junit.framework.TestCase;

import org.json.JSONObject;
import org.mockito.Mock;

/**
 * Created by 310230979 on 3/24/2016.
 */
public class ProdRegMetaDataTest extends TestCase {
    ProdRegMetaData prodRegMetaData;
    @Mock
    ProdRegMetaDataResponse data;

    @Override
    public void setUp() throws Exception {
        prodRegMetaData = new ProdRegMetaData();
    }

    public void testGetData() throws Exception {
        prodRegMetaData.setData(data);
        assertEquals(data, prodRegMetaData.getData());

    }

}