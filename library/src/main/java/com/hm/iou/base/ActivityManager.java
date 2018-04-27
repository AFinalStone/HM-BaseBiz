package com.hm.iou.base;

import android.app.Activity;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by hjy on 18/4/27.<br>
 */

public class ActivityManager {

    private static ActivityManager INSTANCE = new ActivityManager();

    public static ActivityManager getInstance() {
        return INSTANCE;
    }

    private Set<Activity> allActivities;

    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<Activity>();
        }
        allActivities.add(act);
    }

    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }
    }

    /**
     * 退出所有的Activity
     */
    public void exitAllActivities() {
        if (allActivities != null) {
            for (Activity act : allActivities) {
                act.finish();
            }
            allActivities.clear();
        }
    }

}
