/**
 * (C) Koninklijke Philips N.V., 2015.
 * All rights reserved.
 */
package com.philips.platform.core.datatypes;

import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.Collection;

public interface Insight extends BaseAppData, Serializable {

    String INSIGHT_NEVER_SYNCED_AND_DELETED_GUID = "-1";

    void setGUId(String GUId);

    void setLastModified(String lastModified);

    void setInactive(boolean inactive);

    void setVersion(int version);

    void setRuleId(String ruleId);

    void setSubjectId(String subjectId);

    void setMomentId(String momentId);

    void setType(String type);

    void setTimeStamp(String timeStamp);

    void setTitle(String title);

    void setProgram_minVersion(int program_minversion);

    void setProgram_maxVersion(int program_maxversion);

    String getGUId();

    String getLastModified();

    boolean isInactive();

    int getVersion();

    String getRuleId();

    String getSubjectId();

    String getMomentId();

    String getType();

    String getTimeStamp();

    String getTitle();

    int getProgram_minVersion();

    int getProgram_maxVersion();

    @Nullable
    com.philips.platform.core.datatypes.SynchronisationData getSynchronisationData();

    void setSynchronisationData(com.philips.platform.core.datatypes.SynchronisationData synchronisationData);

    void setSynced(boolean isSynced);

    boolean getSynced();

    void setId(int id);

    int getId();

    Collection<? extends InsightMetadata> getInsightMetaData();

    void addInsightMetaData(InsightMetadata insightMetadata);
}
