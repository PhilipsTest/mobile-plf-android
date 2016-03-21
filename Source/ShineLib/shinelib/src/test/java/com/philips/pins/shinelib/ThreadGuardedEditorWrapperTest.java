package com.philips.pins.shinelib;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class ThreadGuardedEditorWrapperTest {

    public static final String KEY = "key";
    public static final String VALUE = "value";

    public static final long NORMAL_EXECUTION_TIME = 10L;
    public static final long EXCEEDED_EXECUTION_TIME = 60L;

    private ThreadGuardedEditorWrapper wrapper;

    @Mock
    private SharedPreferences.Editor editorMock;

    @Before
    public void setUp() throws Exception {
        initMocks(this);

        wrapper = new ThreadGuardedEditorWrapperForTest(editorMock, NORMAL_EXECUTION_TIME);
    }

    @Test
    public void testPutString() throws Exception {
        wrapper.putString(KEY, VALUE);

        verify(editorMock).putString(KEY, VALUE);
    }

    @Test(expected = AssertionError.class)
    public void whenTimeOutExpiresThenAssertErrorIsGiven() throws Exception {
        wrapper = new ThreadGuardedEditorWrapperForTest(editorMock, EXCEEDED_EXECUTION_TIME);

        wrapper.putString(KEY, VALUE);
    }

    @Test
    public void whenPutStringIsCalledThenReturnTypeIsWrapper() throws Exception {
        SharedPreferences.Editor editor = wrapper.putString(KEY, VALUE);

        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testPutStringSet() throws Exception {
        Set<String> empty = Collections.emptySet();
        SharedPreferences.Editor editor = wrapper.putStringSet(KEY, empty);

        verify(editorMock).putStringSet(KEY, empty);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testPutInt() throws Exception {
        SharedPreferences.Editor editor = wrapper.putInt(KEY, 0);

        verify(editorMock).putInt(KEY, 0);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testPutLong() throws Exception {
        SharedPreferences.Editor editor = wrapper.putLong(KEY, 0);

        verify(editorMock).putLong(KEY, 0);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testPutFloat() throws Exception {
        SharedPreferences.Editor editor = wrapper.putFloat(KEY, 0);

        verify(editorMock).putFloat(KEY, 0);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testPutBoolean() throws Exception {
        SharedPreferences.Editor editor = wrapper.putBoolean(KEY, false);

        verify(editorMock).putBoolean(KEY, false);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testRemove() throws Exception {
        SharedPreferences.Editor editor = wrapper.remove(KEY);

        verify(editorMock).remove(KEY);
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testClear() throws Exception {
        SharedPreferences.Editor editor = wrapper.clear();

        verify(editorMock).clear();
        assertTrue(editor instanceof ThreadGuardedEditorWrapper);
    }

    @Test
    public void testCommit() throws Exception {
        wrapper.commit();

        verify(editorMock).commit();
    }

    @Test
    public void testApply() throws Exception {
        wrapper.apply();

        verify(editorMock).apply();
    }

    class ThreadGuardedEditorWrapperForTest extends ThreadGuardedEditorWrapper {

        private long start = 100;
        private long increment;

        public ThreadGuardedEditorWrapperForTest(SharedPreferences.Editor editor, long increment) {
            super(editor);
            this.increment = increment;
        }

        @Override
        protected long getCurrentTimeInMillis() {
            return start += increment;
        }
    }
}