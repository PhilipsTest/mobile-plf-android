/* Copyright (c) Koninklijke Philips N.V., 2016
* All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
*/
package com.philips.cdp.prodreg.model.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.philips.cdp.prxclient.datamodels.summary.Price;
import com.philips.cdp.prxclient.datamodels.summary.ReviewStatistics;
import com.philips.cdp.prxclient.datamodels.summary.Brand;



public class Data extends  com.philips.cdp.prxclient.datamodels.summary.Data implements Serializable  {

    private static final long serialVersionUID = 3102143091240290063L;

    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}
