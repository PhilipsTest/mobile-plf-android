package com.philips.platform.datasync.insights;

import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.datatypes.Insight;
import com.philips.platform.core.datatypes.InsightMetadata;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.UuidGenerator;
import com.philips.platform.datasync.UCoreAccessProvider;
import com.philips.platform.datasync.UCoreAdapter;
import com.philips.testing.verticals.OrmCreatorTest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.converter.GsonConverter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.MockitoAnnotations.initMocks;

public class InsightConverterTest {
    private InsightConverter mInsightConverter;
    @Mock
    private UuidGenerator mUuidGenerator;
    @Mock
    private AppComponent mAppComponent;
    @Mock
    private UCoreAdapter mUCoreAdapter;
    @Mock
    private UCoreAccessProvider mUCoreAccessProvider;
    @Mock
    InsightClient mInsightClient;
    @Mock
    GsonConverter mGsonConverter;

    @Before
    public void setUp() {
        initMocks(this);
        BaseAppDataCreator mBaseAppDataCreator = new OrmCreatorTest(new UuidGenerator());
        DataServicesManager.getInstance().setAppComponant(mAppComponent);
        mInsightConverter = new InsightConverter();
        mInsightConverter.dataCreator = mBaseAppDataCreator;
    }

    @Test
    public void shouldReturnAppInsight() {
        UCoreInsightList uCoreInsightList = new UCoreInsightList();
        List<UCoreInsight> uCoreInsights = new ArrayList<>();
        UCoreInsight uCoreInsight = new UCoreInsight();
        uCoreInsight.setGuid("893r980");
        uCoreInsight.setLastModified("0980-0");
        uCoreInsight.setInactive(false);
        uCoreInsight.setVersion(2);
        uCoreInsight.setRuleId("ruleID");
        uCoreInsight.setSubjectId("subjectID");
        uCoreInsight.setMomentId("momentID");
        uCoreInsight.setType("type");
        uCoreInsight.setTimeStamp("timeStamp");
        uCoreInsight.setTitle("title");
        uCoreInsight.setProgram_maxversion(2);
        uCoreInsight.setProgram_minversion(1);

        Map<String, String> data = new HashMap<>();
        data.put("avg", "200");
        data.put("max", "300");
        data.put("min", "100");
        uCoreInsight.setMetadata(data);
        uCoreInsights.add(uCoreInsight);
        uCoreInsightList.setInsights(uCoreInsights);

        List<Insight> appInsightList = mInsightConverter.convertToAppInsights(uCoreInsightList);
        assertThat(appInsightList).isNotNull();
        assertThat(appInsightList.get(0)).isInstanceOf(Insight.class);
    }

    @Test
    public void shouldReturnUCoreInsight() {
        Insight appInsight = mInsightConverter.dataCreator.createInsight();
        appInsight.setGUId("893r980");
        appInsight.setLastModified("0980-0");
        appInsight.setInactive(false);
        appInsight.setVersion(2);
        appInsight.setRuleId("ruleID");
        appInsight.setSubjectId("subjectID");
        appInsight.setMomentId("momentID");
        appInsight.setType("type");
        appInsight.setTimeStamp("timeStamp");
        appInsight.setTitle("title");
        appInsight.setProgram_maxVersion(2);
        appInsight.setProgram_minVersion(1);

        InsightMetadata insightMetadata = mInsightConverter.dataCreator.createInsightMetaData("avg", "200", appInsight);
        appInsight.addInsightMetaData(insightMetadata);

        List<Insight> insightList = new ArrayList<>();
        insightList.add(appInsight);

        UCoreInsightList uCoreInsightList = mInsightConverter.convertToUCoreInsights(insightList);
        assertThat(uCoreInsightList).isNotNull();
        assertThat(uCoreInsightList).isInstanceOf(UCoreInsightList.class);
    }
}