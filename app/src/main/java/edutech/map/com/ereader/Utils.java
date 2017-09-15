package edutech.map.com.ereader;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by DELL on 8/21/2017.
 */

public class Utils {

    Utils(Context con){
        getPrefs(con);
    }

    public static String firstInstall = "fisrtInstall";
    public static String firstInstall_flag = "true";

    public  static boolean storeSharePref(Context context, String prefName, String key, String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(prefName, MODE_PRIVATE).edit();
        editor.putString(key,value);
      return  editor.commit();
    }

            //SharedPreferences file name
            public static String SHARED_PREFS_FILE_NAME = "my_app_shared_prefs";
            //here you can centralize all your shared prefs keys
            public static String KEY_MY_SHARED_BOOLEAN = "my_shared_boolean";
            public static String KEY_MY_SHARED_FOO = "my_shared_foo";

            //get the SharedPreferences object instance
            //create SharedPreferences file if not present


            private static SharedPreferences getPrefs(Context context) {
                return context.getSharedPreferences(SHARED_PREFS_FILE_NAME, Context.MODE_PRIVATE);
            }

            //Save Booleans
            public static void savePref(Context context, String key, boolean value) {
                getPrefs(context).edit().putBoolean(key, value).commit();
            }

            //Get Booleans
            public static boolean getBoolean(Context context, String key) {
                return getPrefs(context).getBoolean(key, false);
            }

            //Get Booleans if not found return a predefined default value
            public static boolean getBoolean(Context context, String key, boolean defaultValue) {
                return getPrefs(context).getBoolean(key, defaultValue);
            }

            //Strings
            public static void save(Context context, String key, String value) {
                getPrefs(context).edit().putString(key, value).commit();
            }

            public static String getString(Context context, String key) {
                return getPrefs(context).getString(key, "");
            }

            public static String getString(Context context, String key, String defaultValue) {
                return getPrefs(context).getString(key, defaultValue);
            }

            //Integers
            public static void save(Context context, String key, int value) {
                getPrefs(context).edit().putInt(key, value).commit();
            }

            public static int getInt(Context context, String key) {
                return getPrefs(context).getInt(key, 0);
            }

            public static int getInt(Context context, String key, int defaultValue) {
                return getPrefs(context).getInt(key, defaultValue);
            }

            //Floats
            public static void save(Context context, String key, float value) {
                getPrefs(context).edit().putFloat(key, value).commit();
            }

            public static float getFloat(Context context, String key) {
                return getPrefs(context).getFloat(key, 0);
            }

            public static float getFloat(Context context, String key, float defaultValue) {
                return getPrefs(context).getFloat(key, defaultValue);
            }

            //Longs
            public static void save(Context context, String key, long value) {
                getPrefs(context).edit().putLong(key, value).commit();
            }

            public static long getLong(Context context, String key) {
                return getPrefs(context).getLong(key, 0);
            }

            public static long getLong(Context context, String key, long defaultValue) {
                return getPrefs(context).getLong(key, defaultValue);
            }

            //StringSets
            public static void save(Context context, String key, Set<String> value) {
                getPrefs(context).edit().putStringSet(key, value).commit();
            }

            public static Set<String> getStringSet(Context context, String key) {
                return getPrefs(context).getStringSet(key, null);
            }

            public static Set<String> getStringSet(Context context, String key, Set<String> defaultValue) {
                return getPrefs(context).getStringSet(key, defaultValue);
            }

}
