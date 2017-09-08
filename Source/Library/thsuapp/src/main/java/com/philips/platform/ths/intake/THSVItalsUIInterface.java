package com.philips.platform.ths.intake;

import com.philips.platform.ths.base.THSBaseView;

public interface THSVItalsUIInterface extends THSBaseView {
    boolean validate();
    void updateUI(THSVitals thsVitals);
    void updateVitalsData();
    void launchMedicationFragment();
    THSVitals getTHSVitals();

}
