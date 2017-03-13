/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package cdp.philips.com.mydemoapp.database.table;

import android.support.annotation.Nullable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.philips.platform.core.datatypes.Insight;
import com.philips.platform.core.datatypes.InsightMetadata;
import com.philips.platform.core.datatypes.Settings;
import com.philips.platform.core.datatypes.SynchronisationData;

import java.io.Serializable;
import java.util.Collection;

import cdp.philips.com.mydemoapp.database.EmptyForeignCollection;
import cdp.philips.com.mydemoapp.database.annotations.DatabaseConstructor;


@DatabaseTable
public class OrmInsight implements Insight, Serializable {

    public static final long serialVersionUID = 11L;

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(canBeNull = false)
    private String guid;

    @DatabaseField(canBeNull = false)
    private String last_modified;

    @DatabaseField(canBeNull = false)
    private boolean inactive;

    @DatabaseField(canBeNull = false)
    private int version;

    @DatabaseField(canBeNull = false)
    private String rule_id;

    @DatabaseField(canBeNull = false)
    private String subjectID;

    @DatabaseField(canBeNull = false)
    private String moment_id;

    @DatabaseField(canBeNull = false)
    private String type;

    @DatabaseField(canBeNull = false)
    private String time_stamp;

    @DatabaseField(canBeNull = false)
    private String title;

    @DatabaseField(canBeNull = false)
    private int program_min_version;

    @DatabaseField(canBeNull = false)
    private int program_max_version;

    @DatabaseField
    private boolean synced;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, canBeNull = true)
    private OrmSynchronisationData synchronisationData;

    @ForeignCollectionField(eager = true)
    ForeignCollection<OrmInsightMetaData> ormInsightMetaDatas = new EmptyForeignCollection<>();

    @DatabaseConstructor
    public OrmInsight() {
    }

    @Override
    public String toString() {
        return "[OrmInsight, InsightID = " + guid + ", MomentID = " + moment_id + ", Title = " + title;
    }

    @Override
    public void setGUId(String GU_ID) {
        this.guid = GU_ID;
    }

    @Override
    public void setLastModified(String lastModified) {
        this.last_modified = lastModified;
    }

    @Override
    public void setInactive(boolean inactive) {
        this.inactive = inactive;
    }

    @Override
    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public void setRuleId(String ruleId) {
        this.rule_id = ruleId;
    }

    @Override
    public void setSubjectId(String subjectId) {
        this.subjectID = subjectId;
    }

    @Override
    public void setMomentId(String momentId) {
        this.moment_id = momentId;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setTimeStamp(String timeStamp) {
        this.time_stamp = timeStamp;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setProgram_minVersion(int program_minVersion) {
        this.program_min_version = program_minVersion;
    }

    @Override
    public void setProgram_maxVersion(int program_maxversion) {
        this.program_max_version = program_maxversion;
    }

    @Override
    public String getGUId() {
        return guid;
    }

    @Override
    public String getLastModified() {
        return last_modified;
    }

    @Override
    public boolean isInactive() {
        return inactive;
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public String getRuleId() {
        return rule_id;
    }

    @Override
    public String getSubjectId() {
        return subjectID;
    }

    @Override
    public String getMomentId() {
        return moment_id;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getTimeStamp() {
        return time_stamp;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getProgram_minVersion() {
        return program_min_version;
    }

    @Override
    public int getProgram_maxVersion() {
        return program_max_version;
    }


    @Nullable
    @Override
    public OrmSynchronisationData getSynchronisationData() {
        return synchronisationData;
    }

    @Override
    public void setSynchronisationData(SynchronisationData synchronisationData) {
        this.synchronisationData = (OrmSynchronisationData) synchronisationData;
    }

    @Override
    public void setSynced(boolean isSynced) {
        this.synced = isSynced;
    }

    @Override
    public boolean getSynced() {
        return synced;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Collection<? extends OrmInsightMetaData> getInsightMetaData() {
        return ormInsightMetaDatas;
    }

    @Override
    public void addInsightMetaData(InsightMetadata insightMetadata) {
        ormInsightMetaDatas.add((OrmInsightMetaData) insightMetadata);
    }

}
