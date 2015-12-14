package com.philips.pins.shinelib.utility;

import com.philips.pins.shinelib.capabilities.SHNCapabilityDeviceInformation;

import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;

import java.text.ParseException;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.longThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeviceInformationCacheTest {
    public static final SHNCapabilityDeviceInformation.SHNDeviceInformationType INFORMATION_TYPE = SHNCapabilityDeviceInformation.SHNDeviceInformationType.HardwareRevision;
    public static final String TEST_MESSAGE = "TEST_MESSAGE";
    public static final Date TEST_DATE = new Date();

    @Mock
    private SHNDevicePreferenceWrapper preferenceWrapperMock;

    private DeviceInformationCache deviceInformationCache;

    @Before
    public void setUp() throws ParseException {
        initMocks(this);


        deviceInformationCache = new DeviceInformationCache(preferenceWrapperMock);
    }

    @Test
    public void ShouldReturnValueOfCachedType_WhenQueried() {
        when(preferenceWrapperMock.getString(INFORMATION_TYPE.name())).thenReturn(TEST_MESSAGE);

        String value = deviceInformationCache.getValue(INFORMATION_TYPE);

        assertThat(value).isEqualTo(TEST_MESSAGE);
    }

    @Test
    public void ShouldReturnDateOfCachedType_WhenQueried() throws ParseException {
        when(preferenceWrapperMock.getLong(INFORMATION_TYPE.name() + DeviceInformationCache.DATE_SUFFIX)).thenReturn(TEST_DATE.getTime());

        Date date = deviceInformationCache.getDate(INFORMATION_TYPE);

        assertThat(date).isEqualTo(TEST_DATE);
    }

    @Test
    public void ShouldPersistValueAndDate_WhenSaveIsCalled() {
        deviceInformationCache.save(INFORMATION_TYPE, TEST_MESSAGE);

        verify(preferenceWrapperMock).putString(INFORMATION_TYPE.name(), TEST_MESSAGE);
        verify(preferenceWrapperMock).putLong(eq(INFORMATION_TYPE.name() + DeviceInformationCache.DATE_SUFFIX), longThat(new ArgumentMatcher<Long>() {

            private long max;
            private long min;

            @Override
            public boolean matches(final Object argument) {
                boolean res = false;

                if (argument instanceof Long) {
                    long millis = ((Long) argument).longValue();
                    min = TEST_DATE.getTime();
                    max = new Date().getTime();

                    res = min <= millis;
                    res &= millis <= max;
                }

                return res;
            }

            @Override
            public void describeTo(final Description description) {
                description.appendText(String.format("Expected value between %d and %d", min, max));
            }
        }));
    }
}
