package com.gov.mpt.laokyclib

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.gov.mpt.laokyc.model.claimmodel.ClaimModel
import com.gov.mpt.laokyclib.oauth.SharedPreferencesRepository
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import net.openid.appauth.AuthState

/**
 * Created by SBlab on 2020-10-29.
 */

class DashboardActivity : AppCompatActivity() , View.OnClickListener {

    var _msisdn : String?= null
    var _ClaimToken : String? = null
    var _AccessToken : String? = null

    var ivCovidDashboardPhotoProfile : CircleImageView? = null
    var tvCovidDashboardPhoneNumber : TextView? = null
    var tvCovidFirstNameAndSurName : TextView? = null
    var btnDashboardLogout : Button? = null
    var _firstName : String? = null
    var _surName : String? = null

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "Authen"
    var prefs : SharedPreferences? = null
    private var _authen : String? = null
    private val PREF_TOKEN = "token"
    private val PREF_CodeVerifier = "CodeVerifier"
    var prefsCodeVerifier : SharedPreferences? = null
    private var _code_verifier : String? = null

    var _result : ClaimModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        try {
            prefs = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            _authen  = prefs!!.getString(PREF_NAME, null)
        } catch (e : Exception) {

        }

        _msisdn = intent.getStringExtra("msisdn")
        _ClaimToken = intent.getStringExtra("allClaims")
        _AccessToken = intent.getStringExtra("access_token")
        val gson = Gson()
        _result = gson.fromJson(_ClaimToken, ClaimModel::class.java)

        _firstName = _result!!.name!!.value.toString()
        _surName = _result!!.familyName!!.value.toString()

        inits()

    }

    private fun inits() {

        btnDashboardLogout = findViewById(R.id.btnDashboardLogout)
        ivCovidDashboardPhotoProfile = findViewById(R.id.ivCovidDashboardPhotoProfile)
        tvCovidDashboardPhoneNumber = findViewById(R.id.tvCovidDashboardPhoneNumber)
        tvCovidFirstNameAndSurName = findViewById(R.id.tvCovidFirstNameAndSurName)
        btnDashboardLogout!!.setOnClickListener(this)

        Picasso.get().load("https://gateway.sbg.la/api/render/MyPhoto/" + _msisdn + "?")
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .resize(512, 670).into(ivCovidDashboardPhotoProfile)
        tvCovidDashboardPhoneNumber!!.text = _msisdn
        tvCovidFirstNameAndSurName!!.text = _firstName + " " + _surName

    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.btnDashboardLogout) {

            val settings: SharedPreferences = getSharedPreferences(PREF_TOKEN, PRIVATE_MODE)
            settings.edit().remove("msisdn").commit()
            settings.edit().remove("allClaims").commit()
            settings.edit().remove("access_token").commit()


            try {
                prefsCodeVerifier = this.getSharedPreferences(PREF_CodeVerifier, PRIVATE_MODE)
                _code_verifier  = prefsCodeVerifier!!.getString("Verifier", null)
            } catch (e : Exception) {

            }

            val sharedPreferencesRepository =
                    SharedPreferencesRepository(this)
            sharedPreferencesRepository.saveCodeVerifier(_code_verifier)

            val currentState: AuthState = sharedPreferencesRepository.getAuthState()
            val clearedState = AuthState(currentState!!.authorizationServiceConfiguration!!)
            if (currentState != null) {
                clearedState.update(currentState.lastRegistrationResponse)
                sharedPreferencesRepository.saveAuthState(clearedState)

                val mainIntent = Intent(this, LoginActivity::class.java)
                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)
            }
        }
    }




}