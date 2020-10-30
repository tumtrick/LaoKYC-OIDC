package com.gov.mpt.laokyclib.oauth;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.gov.mpt.laokyclib.utils.BuildConfigs;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.BuildConfig;
import net.openid.appauth.TokenResponse;

/**
 * Created by SBlab on 2020-10-29.
 */

public class AuthManager {
	private static AuthManager instance;
	private AuthState mAuthState;
	private Auth mAuth;
	private AuthorizationServiceConfiguration mAuthConfig;
	private SharedPreferencesRepository mSharedPrefRep;
	private AuthorizationService mAuthService;

	public static AuthManager getInstance(Context context) {
		if (instance == null) {
			instance = new AuthManager(context);
		}
		return instance;
	}

	private AuthManager(Context context){
		mSharedPrefRep = new SharedPreferencesRepository(context);
		setAuthData();

		mAuthConfig = new AuthorizationServiceConfiguration(
				Uri.parse(mAuth.getAuthorizationEndpointUri()),
				Uri.parse(mAuth.getTokenEndpointUri()),
				null);
		mAuthState = mSharedPrefRep.getAuthState();

		AppAuthConfiguration.Builder appAuthConfigBuilder = new AppAuthConfiguration.Builder();

		//To Allow Http in requests in debug mode
		if(BuildConfig.DEBUG)
			appAuthConfigBuilder.setConnectionBuilder(AppAuthConnectionBuilderForTesting.INSTANCE);

		AppAuthConfiguration appAuthConfig = appAuthConfigBuilder.build();
		mAuthService = new AuthorizationService(context, appAuthConfig);
	}



	public AuthorizationServiceConfiguration getAuthConfig() {
		return mAuthConfig;
	}

	public Auth getAuth() {
		if(mAuth == null){
           setAuthData();
        }
        return mAuth;
	}

	public AuthState getAuthState(){
		return mAuthState;
	}

	public void updateAuthState(TokenResponse response, AuthorizationException ex){
		mAuthState.update(response,ex);
		mSharedPrefRep.saveAuthState(mAuthState);
	}

	public void clearAuthState(TokenResponse response, AuthorizationException ex){
		mAuthState.update(response,ex);
		mSharedPrefRep.saveAuthState(mAuthState);
	}

	public void setAuthState(AuthorizationResponse response, AuthorizationException ex){
		if(mAuthState == null)
			mAuthState = new AuthState(response,ex);

        mSharedPrefRep.saveAuthState(mAuthState);
	}

	public AuthorizationService getAuthService(){
		return mAuthService;
	}

	private void setAuthData(){

        mAuth = new Auth();
        mAuth.setClientId(BuildConfigs.CLIENT_ID);
        mAuth.setAuthorizationEndpointUri(BuildConfigs.AUTHORIZSTION_END_POINT_URI);
        mAuth.setClientSecret(BuildConfigs.CLIENT_SECRET);
        mAuth.setRedirectUri(BuildConfigs.REDIRECT_URI);
        mAuth.setScope(BuildConfigs.SCOPE);
        mAuth.setTokenEndpointUri(BuildConfigs.TOKEN_END_POINT_URI);
        mAuth.setResponseType(BuildConfigs.RESPONSE_TYPE);
    }


	public void saveAuthOffline(Context context , String IdToken , String FirstName , String SurName , String PhotoLink , String Phone , String Flag ) {

		SharedPreferences prefs = context.getSharedPreferences("auth" ,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();

		editor.putString("id_token" ,  IdToken);
		editor.putString("first_name" ,  FirstName);
		editor.putString("sur_name" ,  SurName);
		editor.putString("photo_link" ,  PhotoLink);
		editor.putString("phone" ,  Phone);
		editor.putString("flg" ,  Flag);
		editor.apply();
	}


}
