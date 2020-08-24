package com.ecs.demotestuapp.jsonmodel;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class SubgroupItem implements Serializable {

    @SerializedName("button")
    private int button;

    @SerializedName("spinners")
    private List<Property> spinners;

    @SerializedName("name")
    private String name;

    @SerializedName("switches")
    private List<Property> switches;

    @SerializedName("checkBoxes")
    private List<Property> checkBoxes;

    @SerializedName("editTexts")
    private List<Property> editTexts;

    @SerializedName("editText")
    private int editText;

    @SerializedName("apiNumber")
    private int apiNumber;

    @SerializedName("spinner")
    private int spinner;

    public void setButton(int button) {
        this.button = button;
    }

    public int getButton() {
        return button;
    }

    public void setSpinners(List<Property> spinners) {
        this.spinners = spinners;
    }

    public List<Property> getSpinners() {
        return spinners;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setSwitches(List<Property> switches) {
        this.switches = switches;
    }

    public List<Property> getSwitches() {
        return switches;
    }

    public void setEditTexts(List<Property> editTexts) {
        this.editTexts = editTexts;
    }

    public List<Property> getEditTexts() {
        return editTexts;
    }

    public void setEditText(int editText) {
        this.editText = editText;
    }

    public int getEditText() {
        return editText;
    }

    public void setApiNumber(int apiNumber) {
        this.apiNumber = apiNumber;
    }

    public int getApiNumber() {
        return apiNumber;
    }

    public void setSpinner(int spinner) {
        this.spinner = spinner;
    }

    public int getSpinner() {
        return spinner;
    }

    public void setCheckBoxes(List<Property> checkBoxes) {
        this.checkBoxes = checkBoxes;
    }

    public List<Property> getCheckBoxes() {
        return checkBoxes;
    }
}