package com.example.heitorcolangelo.espressotests.utils;

import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.v4.content.ContextCompat;

import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import static android.support.test.InstrumentationRegistry.getInstrumentation;

import static java.lang.Thread.sleep;

public class PermissionUtils {

    private static final int PERMISSIONS_DIALOG_DELAY = 3000;
    private static final int GRANT_BUTTON_INDEX = 1;

    public static void allowPermissionsIfNeeded(String permissionNeeded){
        try{
            Context context = InstrumentationRegistry.getTargetContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !hasNeededPermission(context, permissionNeeded)){
                sleep(PERMISSIONS_DIALOG_DELAY);
                UiDevice device = UiDevice.getInstance(getInstrumentation());
                UiObject allowPermissions = device.findObject(new UiSelector()
                .clickable(true)
                        .checkable(false)
                        .index(GRANT_BUTTON_INDEX));
                if (allowPermissions.exists()){
                    allowPermissions.click();
                }

            }
        }catch (UiObjectNotFoundException e){
            System.out.println("There is no permissions dialog to interact with");
        }
    }

    private static boolean hasNeededPermission(Context context, String permissionNeeded) {
        int permissionStatus = ContextCompat.checkSelfPermission(context, permissionNeeded);
        return permissionStatus == PackageManager.PERMISSION_GRANTED;
    }

    private static void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException("Cannot execute Thread.sleep()");
        }
    }
}
