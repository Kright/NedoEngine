package com.vk.lgorsl.TryGLgame;

import android.annotation.TargetApi;
import android.os.Build;
import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.vk.lgorsl.TryGLgame.MyActivityTest \
 * com.vk.lgorsl.TryGLgame.tests/android.test.InstrumentationTestRunner
 */
@TargetApi(Build.VERSION_CODES.CUPCAKE)
public class MyActivityTest extends ActivityInstrumentationTestCase2<MyActivity> {

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public MyActivityTest() {
        super("com.vk.lgorsl.TryGLgame", MyActivity.class);
    }

    public void testNothing(){
        assertTrue(true);
    }
}
