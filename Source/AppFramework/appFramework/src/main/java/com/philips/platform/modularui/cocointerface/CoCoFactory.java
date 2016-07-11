package com.philips.platform.modularui.cocointerface;

import com.philips.platform.appframework.UIConstants;

/**
 * Created by 310213373 on 7/7/2016.
 */
public class CoCoFactory {
    private static CoCoFactory instance = new CoCoFactory();

    private CoCoFactory() {

    }

    public static CoCoFactory getInstance() {
        if (null == instance) {
            instance = new CoCoFactory();
        }
        return instance;
    }

    public UICoCoInterface getCoCo(@UIConstants.UICoCoConstants int coCo) {

        switch (coCo) {
            case UIConstants.UI_COCO_PRODUCT_REGISTRATION:
                return new UICoCoProdRegImpl();
            case UIConstants.UI_COCO_USER_REGISTRATION:
                return new UICoCoUserRegImpl();
            case UIConstants.UI_COCO_CONSUMER_CARE:
                return new UICoCoConsumerCareImpl();
            default:
                return null;
        }
    }
}