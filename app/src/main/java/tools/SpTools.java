package tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Locale;

/**
 * Created by jsion on 15/11/19.
 */
public class SpTools {
    private static final String LOACLE_LANGUAGE = "loacle_language";
    private String SPName_UserConfig = "my_police";
    private Context mContext;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor edit;
    private static SpTools mUserConfig;

    public static synchronized SpTools getInstance(Context paramContext) {
        if (null == mUserConfig) {
            mUserConfig = new SpTools(paramContext);
        }
        return mUserConfig;
    }

    private SpTools(Context paramContext) {
        this.mContext = paramContext;
        this.sharedPreferences = this.mContext.getSharedPreferences(this.SPName_UserConfig, Context.MODE_PRIVATE);
        this.edit = this.sharedPreferences.edit();
    }



    public void saveCurrentLanguage(String lan) {
        edit.putString(LOACLE_LANGUAGE, lan);
        edit.commit();
    }

    public String readCurrentLanguage() {
        String country = sharedPreferences.getString(LOACLE_LANGUAGE, Locale.US.getCountry());
        return country;
    }
}
