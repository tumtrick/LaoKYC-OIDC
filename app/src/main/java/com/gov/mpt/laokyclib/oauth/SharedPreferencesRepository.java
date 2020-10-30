package com.gov.mpt.laokyclib.oauth;

import android.content.Context;
import android.preference.PreferenceManager;

import net.openid.appauth.AuthState;

import org.json.JSONException;


/**
 * Created by SBlab on 2020-10-29.
 */

public class SharedPreferencesRepository{

    private Context mContext;

    public SharedPreferencesRepository(Context context){
        mContext = context;
    }


    public void saveCodeVerifier(String codeVerifier) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("Auth.CodeVerifier",codeVerifier).apply();
    }

    public String getCodeVerifier() {
        return PreferenceManager.getDefaultSharedPreferences(mContext).getString("Auth.CodeVerifier",null);
    }

     public void saveAuthState(AuthState authState) {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("AuthState",authState.jsonSerializeString()).apply();
    }

    public void clearAuthState() {
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("AuthState",null).apply();

    }

     public AuthState getAuthState() {
        String authStateString =  PreferenceManager.getDefaultSharedPreferences(mContext).getString("AuthState",null);
        if(authStateString != null){
            try {
                return AuthState.jsonDeserialize(authStateString);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;

    }
}
