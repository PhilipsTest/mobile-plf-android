/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package cdp.philips.com.mydemoapp.database.table;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.core.datatypes.DCSync;
import com.philips.platform.core.datatypes.InsightMetadata;

import org.joda.time.DateTime;

import java.io.Serializable;

import cdp.philips.com.mydemoapp.database.annotations.DatabaseConstructor;

@DatabaseTable
public class OrmInsightMetaData implements InsightMetadata, Serializable {

    public static final long serialVersionUID = 11L;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = true)
    private String meta_key;

    @DatabaseField(canBeNull = true)
    private String meta_value;

    @DatabaseField(foreign = true, foreignAutoRefresh = false, canBeNull = false)
    private OrmInsight ormInsight;

    @DatabaseConstructor
    OrmInsightMetaData() {
    }

    public OrmInsightMetaData(@NonNull final String meta_key, @NonNull final String meta_value, OrmInsight ormInsight) {
        this.meta_key = meta_key;
        this.meta_value = meta_value;
        this.ormInsight = ormInsight;
    }

    @Override
    public String getKey() {
        return meta_key;
    }

    @Override
    public void setKey(String key) {
        this.meta_key = key;
    }

    @Override
    public String getValue() {
        return meta_value;
    }

    @Override
    public void setValue(String value) {
        this.meta_value = value;
    }
}
