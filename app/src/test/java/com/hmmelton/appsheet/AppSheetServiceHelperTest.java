package com.hmmelton.appsheet;

import com.hmmelton.appsheet.helpers.AppSheetServiceHelper;
import com.hmmelton.appsheet.interfaces.GetUsersCallback;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Scheduler;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.internal.schedulers.ExecutorScheduler;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * Created by harrison on 4/18/17.
 * This is a test class for AppSheetServiceHelper
 */

public class AppSheetServiceHelperTest {

    // Helper object for interacting with AppSheet web service
    private AppSheetServiceHelper mHelper;
    
    @Before
    public void setUpHelper() {
        mHelper = new AppSheetServiceHelper();
    }

    @BeforeClass
    public static void setUpRxSchedulers() {
        Scheduler immediate = new Scheduler() {
            @Override
            public Scheduler.Worker createWorker() {
                return new ExecutorScheduler.ExecutorWorker(Runnable::run);
            }
        };

        // Mock RxJava features
        RxJavaPlugins.setInitIoSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitComputationSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitNewThreadSchedulerHandler(scheduler -> immediate);
        RxJavaPlugins.setInitSingleSchedulerHandler(scheduler -> immediate);
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> immediate);
    }
    
    @Test
    public void validatePhoneNumbers() {
        try {
            // Get private method from helper's class
            Method method = getPrivateMethod("validatePhoneNumber", String.class);

            // Test method with different inputs
            // Should be true
            Assert.assertTrue( (Boolean) method.invoke(mHelper, "425-785-0322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "425 785-0322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "(425) 785-0322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "(425)785-0322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "785-0322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "4257850322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "7850322"));
            Assert.assertTrue((Boolean) method.invoke(mHelper, "785 0322"));
            // Should be false
            Assert.assertFalse((Boolean) method.invoke(mHelper, "12345"));
            Assert.assertFalse((Boolean) method.invoke(mHelper, "345"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test_orderOfUsers() {
        try {
            // Get private methods from helper
            // Set up Retrofit
            Method retrofitMethod = getPrivateMethod("setUpRetrofit");
            retrofitMethod.invoke(mHelper);
            // Get users
            Method getUserListMethod = getPrivateMethod("getUsers", List.class,
                    GetUsersCallback.class);
            // Create new Integer array list
            List<Integer> list = new ArrayList<>();
            // Add values 1-26 to list
            for (int i = 1; i <= 26; i++) {
                list.add(i);
            }
            getUserListMethod.invoke(mHelper, list, (GetUsersCallback) users -> {
                // Iterate through to make sure users are sorted by age
                for (int i = 0; i < users.size() - 1; i++) {
                    Assert.assertTrue("users not sorted by age",
                            users.get(i).getAge() < users.get(i + 1).getAge());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method returns a private method from {@link #mHelper}.
     * @param methodName name of method to fetch
     * @return method
     * @throws NoSuchMethodException thrown if method with given name and parameters is not found
     */
    private Method getPrivateMethod(String methodName, Class... args) throws NoSuchMethodException{
        // Get private method from helper's class
        Method method = mHelper.getClass()
                .getDeclaredMethod(methodName, args);
        // Set accessible to allow use of private method
        method.setAccessible(true);

        return method;
    }
}
