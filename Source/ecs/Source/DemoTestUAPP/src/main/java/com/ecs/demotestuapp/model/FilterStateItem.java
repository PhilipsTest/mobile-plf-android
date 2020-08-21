package com.ecs.demotestuapp.model;

import com.philips.platform.ecs.microService.model.filter.ECSStockLevel;

public class FilterStateItem {
    private String title;
    private boolean selected;
    private ECSStockLevel ecsStockLevel;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public ECSStockLevel getEcsStockLevel() {
        return ecsStockLevel;
    }

    public void setEcsStockLevel(ECSStockLevel ecsStockLevel) {
        this.ecsStockLevel = ecsStockLevel;
    }

}
