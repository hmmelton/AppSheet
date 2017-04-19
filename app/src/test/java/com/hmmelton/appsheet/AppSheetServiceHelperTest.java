package com.hmmelton.appsheet;

import com.hmmelton.appsheet.helpers.AppSheetServiceHelper;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;

/**
 * Created by harrison on 4/18/17.
 * This is a test class for AppSheetServiceHelper
 */

public class AppSheetServiceHelperTest {
    @Test
    public void validatePhoneNumbers() {
        AppSheetServiceHelper helper = new AppSheetServiceHelper(null);

        try {
            // Get private method from helper's class
            Method method = helper.getClass()
                    .getDeclaredMethod("validatePhoneNumber", String.class);
            // Set accessible to allow use of private method
            method.setAccessible(true);

            // Test method with different inputs
            Assert.assertTrue( (Boolean) method.invoke(helper, "425-785-0322"));
            Assert.assertTrue((Boolean) method.invoke(helper, "425 785-0322"));
            Assert.assertTrue((Boolean) method.invoke(helper, "(425) 785-0322"));
            Assert.assertTrue((Boolean) method.invoke(helper, "(425)785-0322"));
            Assert.assertTrue((Boolean) method.invoke(helper, "785-0322"));
            Assert.assertTrue((Boolean) method.invoke(helper, "4257850322"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
