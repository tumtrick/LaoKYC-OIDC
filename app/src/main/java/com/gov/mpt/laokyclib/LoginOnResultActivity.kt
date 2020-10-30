package com.gov.mpt.laokyclib


import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.gov.mpt.laokyc.model.claimmodel.ClaimModel
import com.gov.mpt.laokyclib.oauth.AuthManager
import com.gov.mpt.laokyclib.oauth.SharedPreferencesRepository
import com.gov.mpt.laokyclib.oauth.TokenService
import net.openid.appauth.*
import net.openid.appauth.AuthorizationService.TokenResponseCallback

/**
 * Created by SBlab on 2020-10-29.
 */

class LoginOnResultActivity : AppCompatActivity() {


    var _account: String? = null
    private var mAuthService: AuthorizationService? = null
    var _AccessToken: String? = null

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "Authen"
    private val PREF_TOKEN = "token"
    var prefs : SharedPreferences? = null
    var prefsToken : SharedPreferences? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)


        prefs = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        prefsToken = this.getSharedPreferences(PREF_TOKEN, PRIVATE_MODE)

        val resp = AuthorizationResponse.fromIntent(intent)
        val ex =
                AuthorizationException.fromIntent(intent)

        val authManager = AuthManager.getInstance(this)
        try {
            authManager.setAuthState(resp, ex)
        } catch (e:Exception) {

        }



        if (resp != null) {
            val clientSecretPost =
                    ClientSecretPost(authManager.auth.clientSecret)
            val tokenRequest = TokenRequest.Builder(
                    authManager.authConfig,
                    authManager.auth.clientId
            )
                    .setAuthorizationCode(resp.authorizationCode)
                    .setRedirectUri(Uri.parse(authManager.auth.redirectUri))
                    .setCodeVerifier(SharedPreferencesRepository(this).codeVerifier)
                    .build()
            mAuthService = authManager.authService

            mAuthService!!.performTokenRequest(
                    tokenRequest,
                    clientSecretPost,
                    TokenResponseCallback { response, ex ->
                        if (ex == null) {
                            authManager.updateAuthState(response, ex)
                            MyApp.Token = authManager.authState.idToken
                            _AccessToken =
                                    authManager.authState.accessToken
                            val jwt = JWT(MyApp.Token)
                            val issuer = jwt.issuer
                            val idToken = authManager.authState.idToken
                            val subject = jwt.subject
                            val audience =
                                    jwt.audience
                            val expiresAt = jwt.expiresAt
                            val notBefore = jwt.notBefore
                            val issuedAt = jwt.issuedAt
                            val id = jwt.id
                            val allClaims =
                                    jwt.claims
                            val gson = Gson()
                            val _allClaims = gson.toJson(allClaims)

                            // Check Msisdn Already
                            val _result = gson.fromJson(_allClaims, ClaimModel::class.java)

                            _account = _result.account!!.value.toString()

                            if (_account.equals("exist")) {

                                startService(Intent(this@LoginOnResultActivity, TokenService::class.java))


                                val editor = prefs!!.edit()
                                editor.putString(PREF_NAME, "In")
                                editor.apply()


                                val editorToken = prefsToken!!.edit()
                                editorToken.putString("msisdn", _result.preferredUsername!!.value)
                                editorToken.putString("allClaims", _allClaims)
                                editorToken.putString("access_token", _AccessToken)
                                editorToken.apply()


                                val mainIntent = Intent(this@LoginOnResultActivity, DashboardActivity::class.java)
                                mainIntent.putExtra("issuer", issuer)
                                mainIntent.putExtra("subject", subject)
                                mainIntent.putExtra("audience", audience.toString())
                                mainIntent.putExtra("expiresAt", expiresAt.toString())
                                mainIntent.putExtra("notBefore", notBefore.toString())
                                mainIntent.putExtra("issuedAt", issuedAt.toString())
                                mainIntent.putExtra("id", id)
                                mainIntent.putExtra("allClaims", _allClaims)
                                mainIntent.putExtra("access_token" , _AccessToken)
                                mainIntent.putExtra("msisdn" , _result.preferredUsername!!.value)
                                mainIntent.putExtra("id_token" , idToken)
                                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(mainIntent)


                            } else if (_account.equals("new")) {
                                startService(Intent(this@LoginOnResultActivity, TokenService::class.java))

                            }
                        } else {
                            val loginIntent =
                                    Intent(this@LoginOnResultActivity, LoginActivity::class.java)
                            startActivity(loginIntent)
                            finish()
                        }
                    })
            // authorization completed
        } else { // authorization failed, check ex for more details
            val loginIntent = Intent(this@LoginOnResultActivity, LoginActivity::class.java)
            startActivity(loginIntent)
            finish()
        }

    }


}
