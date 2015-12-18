package tools;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by jsion on 15/12/9.
 */
public class SharedPreferencesTools {
    private static final String SP_FLAG = "SP";
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private final SharedPreferences.Editor mEditor;

    public SharedPreferencesTools(Context mContext) {
        this.mContext = mContext;
        mSharedPreferences = mContext.getSharedPreferences(SP_FLAG, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public void saveBooleanFlag(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public boolean getBooleanFlag(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }


}
