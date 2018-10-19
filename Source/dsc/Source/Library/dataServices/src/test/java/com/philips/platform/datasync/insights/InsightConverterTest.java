package com.philips.platform.datasync.insights;

import com.philips.platform.core.BaseAppDataCreator;
import com.philips.platform.core.datatypes.Insight;
import com.philips.platform.core.datatypes.InsightMetadata;
import com.philips.platform.core.injection.AppComponent;
import com.philips.platform.core.trackers.DataServicesManager;
import com.philips.platform.core.utils.UuidGenerator;
import com.philips.testing.verticals.TestEntityCreator;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.MockitoAnnotations.initMocks;

public class InsightConverterTest {

    private InsightConverter mInsightConverter;

    @Mock
    private AppComponent mAppComponent;

    @Before
    public void setUp() {
        initMocks(this);
        BaseAppDataCreator mBaseAppDataCreator = new TestEntityCreator(new UuidGenerator());
        DataServicesManager.getInstance().setAppComponent(mAppComponent);
        mInsightConverter = new InsightConverter();
        mInsightConverter.dataCreator = mBaseAppDataCreator;
    }

    @Test
    public void shouldReturnAppInsight() {
        UCoreInsightList uCoreInsightList = new UCoreInsightList();
        List<UCoreInsight> uCoreInsights = new ArrayList<>();
        UCoreInsight uCoreInsight = new UCoreInsight();
        uCoreInsight.setGuid("aefe5623-a7ac-4b4a-b789-bdeaf23add9f");
        uCoreInsight.setLastModified("2017-03-21T10:19:51.706Z");
        uCoreInsight.setInactive(false);
        uCoreInsight.setVersion(2);
        uCoreInsight.setRuleId("ruleID");
        uCoreInsight.setSubjectId("subjectID");
        uCoreInsight.setMomentId("momentID");
        uCoreInsight.setType("type");
        uCoreInsight.setTimeStamp("2018-01-01T07:07:14.000Z");
        uCoreInsight.setTitle("title");
        uCoreInsight.setProgram_maxversion(2);
        uCoreInsight.setProgram_minversion(1);

        Map<String, String> data = new HashMap<>();
        data.put("avg", "200");
        data.put("max", "300");
        data.put("min", "100");
        uCoreInsight.setMetadata(data);
        uCoreInsights.add(uCoreInsight);
        uCoreInsightList.setSyncurl("Url");
        assertTrue(uCoreInsightList.getSyncurl().equals("Url"));
        uCoreInsightList.setInsights(uCoreInsights);

        List<Insight> appInsightList = mInsightConverter.convertToAppInsights(uCoreInsightList);
        assertThat(appInsightList).isNotNull();
        assertThat(appInsightList.get(0)).isInstanceOf(Insight.class);
    }

    @Test
    public void shouldReturnUCoreInsight() {
        Insight appInsight = mInsightConverter.dataCreator.createInsight();
        appInsight.getSynchronisationData().setGuid("aefe5623-a7ac-4b4a-b789-bdeaf23add9f");
        appInsight.getSynchronisationData().setLastModified(DateTime.parse("2017-03-21T10:19:51.706Z", ISODateTimeFormat.dateTime()));
        appInsight.getSynchronisationData().setInactive(false);
        appInsight.getSynchronisationData().setVersion(2);
        appInsight.setRuleId("ruleID");
        appInsight.setSubjectId("subjectID");
        appInsight.setMomentId("momentID");
        appInsight.setType("type");
        appInsight.setTimeStamp("2018-01-01T07:07:14.000Z");
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

    @Test
    public void givenConverterExists_whenConvertToAppInsights_andExpirationDateNull_thenShouldReturnNonNull() {
        UCoreInsightList uCoreInsightList = new UCoreInsightList();
        List<UCoreInsight> uCoreInsights = new ArrayList<>();
        UCoreInsight uCoreInsight = new UCoreInsight();
        uCoreInsight.setGuid("aefe5623-a7ac-4b4a-b789-bdeaf23add9f");
        uCoreInsight.setLastModified("2017-03-21T10:19:51.706Z");
        uCoreInsight.setInactive(false);
        uCoreInsight.setVersion(2);
        uCoreInsight.setRuleId("ruleID");
        uCoreInsight.setSubjectId("subjectID");
        uCoreInsight.setMomentId("momentID");
        uCoreInsight.setType("type");
        uCoreInsight.setTimeStamp("2018-01-01T07:07:14.000Z");
        uCoreInsight.setTitle("title");
        uCoreInsight.setProgram_maxversion(2);
        uCoreInsight.setProgram_minversion(1);
        uCoreInsight.setExpirationDate(null);

        Map<String, String> data = new HashMap<>();
        data.put("avg", "200");
        data.put("max", "300");
        data.put("min", "100");
        uCoreInsight.setMetadata(data);
        uCoreInsights.add(uCoreInsight);
        uCoreInsightList.setSyncurl("Url");
        assertTrue(uCoreInsightList.getSyncurl().equals("Url"));
        uCoreInsightList.setInsights(uCoreInsights);

        List<Insight> appInsightList = mInsightConverter.convertToAppInsights(uCoreInsightList);
        assertThat(appInsightList).isNotNull();
        assertThat(appInsightList.get(0).getExpirationDate()).isNotNull();
    }

    @Test
    public void givenConverterExists_whenConvertToAppInsights_andExpirationDateSet_thenShouldReturnNonNull() {
        // Given
        UCoreInsightList uCoreInsightList = new UCoreInsightList();
        List<UCoreInsight> uCoreInsights = new ArrayList<>();
        UCoreInsight uCoreInsight = new UCoreInsight();
        uCoreInsight.setGuid("aefe5623-a7ac-4b4a-b789-bdeaf23add9f");
        uCoreInsight.setLastModified("2017-03-21T10:19:51.706Z");
        uCoreInsight.setInactive(false);
        uCoreInsight.setVersion(2);
        uCoreInsight.setRuleId("ruleID");
        uCoreInsight.setSubjectId("subjectID");
        uCoreInsight.setMomentId("momentID");
        uCoreInsight.setType("type");
        uCoreInsight.setTimeStamp("2018-01-01T07:07:14.000Z");
        uCoreInsight.setTitle("title");
        uCoreInsight.setProgram_maxversion(2);
        uCoreInsight.setProgram_minversion(1);
        uCoreInsight.setExpirationDate("1993-03-22");

        Map<String, String> data = new HashMap<>();
        data.put("avg", "200");
        data.put("max", "300");
        data.put("min", "100");
        uCoreInsight.setMetadata(data);
        uCoreInsights.add(uCoreInsight);
        uCoreInsightList.setSyncurl("Url");
        assertTrue(uCoreInsightList.getSyncurl().equals("Url"));
        uCoreInsightList.setInsights(uCoreInsights);

        // When
        List<Insight> appInsightList = mInsightConverter.convertToAppInsights(uCoreInsightList);

        // Then
        assertThat(appInsightList).isNotNull();
        assertThat(appInsightList.get(0).getExpirationDate()).isInstanceOf(DateTime.class);
    }

    @Test
    public void givenConverterExists_whenConvertToAppInsights_andExpirationDateSetWithTime_thenShouldReturnNonNull() {
        // Given
        UCoreInsightList uCoreInsightList = new UCoreInsightList();
        List<UCoreInsight> uCoreInsights = new ArrayList<>();
        UCoreInsight uCoreInsight = new UCoreInsight();
        uCoreInsight.setGuid("aefe5623-a7ac-4b4a-b789-bdeaf23add9f");
        uCoreInsight.setLastModified("2017-03-21T10:19:51.706Z");
        uCoreInsight.setInactive(false);
        uCoreInsight.setVersion(2);
        uCoreInsight.setRuleId("ruleID");
        uCoreInsight.setSubjectId("subjectID");
        uCoreInsight.setMomentId("momentID");
        uCoreInsight.setType("type");
        uCoreInsight.setTimeStamp("2018-01-01T07:07:14.000Z");
        uCoreInsight.setTitle("title");
        uCoreInsight.setProgram_maxversion(2);
        uCoreInsight.setProgram_minversion(1);
        uCoreInsight.setExpirationDate("2019-04-23T10:47:10.853Z");

        Map<String, String> data = new HashMap<>();
        data.put("avg", "200");
        data.put("max", "300");
        data.put("min", "100");
        uCoreInsight.setMetadata(data);
        uCoreInsights.add(uCoreInsight);
        uCoreInsightList.setSyncurl("Url");
        assertTrue(uCoreInsightList.getSyncurl().equals("Url"));
        uCoreInsightList.setInsights(uCoreInsights);

        // When
        List<Insight> appInsightList = mInsightConverter.convertToAppInsights(uCoreInsightList);

        // Then
        assertThat(appInsightList).isNotNull();
        assertThat(appInsightList.get(0).getExpirationDate()).isInstanceOf(DateTime.class);
    }
}