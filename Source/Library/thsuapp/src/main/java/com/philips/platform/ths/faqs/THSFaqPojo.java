/* Copyright (c) Koninklijke Philips N.V., 2016
 * All rights are reserved. Reproduction or dissemination
 * in whole or in part is prohibited without the prior written
 * consent of the copyright holder.
 */

package com.philips.platform.ths.faqs;

import java.io.Serializable;
import java.util.List;

public class THSFaqPojo implements Serializable{

    private String section;
    private List<FaqBean> faq;

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<FaqBean> getFaq() {
        return faq;
    }

    public void setFaq(List<FaqBean> faq) {
        this.faq = faq;
    }
}
