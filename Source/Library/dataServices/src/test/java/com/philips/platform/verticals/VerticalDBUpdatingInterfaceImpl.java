package com.philips.platform.verticals;

import com.philips.platform.core.datatypes.Consent;
import com.philips.platform.core.datatypes.Moment;
import com.philips.platform.core.dbinterfaces.DBUpdatingInterface;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by 310218660 on 1/2/2017.
 */

public class VerticalDBUpdatingInterfaceImpl implements DBUpdatingInterface {

    @Override
    public void updateMoment(Moment ormMoment) {

    }

    @Override
    public void updateFailed(Exception e) {

    }

    @Override
    public boolean updateConsent(Consent consent) throws SQLException {
        return false;
    }
}
