package test.ewoxej.com.testapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.widget.Button;

public class AppState
{
    private static final String APP_PREF = "preferences";
    private static final String APP_PREF_DEGRAD = "degrad";
    private static final String APP_PREF_PREC = "prec";
    private SharedPreferences mSetting;
    private ComplexNumber.UnitType unitType;
    private int precision;
    public AppState(Context Context)
    {
        mSetting = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
        settingRestore();
    }

    public void settingRestore()
    {
        if (mSetting.contains(APP_PREF_DEGRAD)) {
            boolean unitTypeAsBool = mSetting.getBoolean(APP_PREF_DEGRAD, true);
            unitType = (unitTypeAsBool)? ComplexNumber.UnitType.Degree : ComplexNumber.UnitType.Radians;
        }
        if(mSetting.contains(APP_PREF_PREC)){
            precision=mSetting.getInt(APP_PREF_PREC,3);
        }
    }

    public void setPrecision(int prec)
    {
        SharedPreferences.Editor editor=mSetting.edit();
        editor.putInt(APP_PREF_PREC,prec);
        editor.apply();
    }

    public void setUnitType(ComplexNumber.UnitType type)
    {
        SharedPreferences.Editor editor=mSetting.edit();
        editor.putBoolean(APP_PREF_DEGRAD,(type== ComplexNumber.UnitType.Degree));
        editor.apply();
    }
}
