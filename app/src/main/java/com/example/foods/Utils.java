package com.example.foods;

import android.app.Activity;
import android.content.Intent;
public class Utils
{
    private static int sTheme;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_YELLOW = 1;
    public final static int THEME_GREEN = 2;
    public final static int THEME_VIOLET = 3;

    /**
     * Set the theme of the Activity, and restart it by creating a new Activity of the same type.
     */
    public static void changeToTheme(Activity activity, int theme)
    {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    /** Set the theme of the activity, according to the configuration. */
    public static void onActivityCreateSetTheme(Activity activity)
    {
        switch (sTheme)
        {
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppThemeEggBlue);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppThemeSelectiveYellow);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppThemeOGGREEN);
                break;
            case THEME_VIOLET:
                activity.setTheme(R.style.AppThemeViolet);
                break;
        }
    }
}