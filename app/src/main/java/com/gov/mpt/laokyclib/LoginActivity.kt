package com.gov.mpt.laokyclib

import android.app.Dialog
import android.app.PendingIntent
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.google.gson.Gson
import com.gov.mpt.laokyclib.model.ModelRegisterAccount
import com.gov.mpt.laokyclib.oauth.Auth
import com.gov.mpt.laokyclib.oauth.AuthManager
import com.gov.mpt.laokyclib.oauth.SharedPreferencesRepository
import com.gov.mpt.laokyclib.utils.Keys
import com.gov.mpt.laokyclib.utils.NetworkUtil
import com.gov.mpt.laokyclib.utils.ProgressDialogUtil
import com.gov.mpt.laokyclib.utils.VolleySingleton
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.CodeVerifierUtil
import org.json.JSONObject

/**
 * Created by SBlab on 2020-10-29.
 */

class LoginActivity : AppCompatActivity() , View.OnClickListener {

    var btnSignInWithLaoKYC : Button? = null
    var dialogLaoKYC: Dialog? = null
    var etLoginPhonenumber: EditText? = null
    var btnLoginSubmit: Button? = null
    var ivLoginMainClose: ImageView? = null
    var dialog : ProgressDialogUtil? = null
    var _OperatorName : String? = null
    var _OperatorDB : String? = null

    private var PRIVATE_MODE = 0
    private val PREF_NAME = "Authen"
    private val PREF_TOKEN = "token"
    private val PREF_CodeVerifier = "CodeVerifier"
    var prefs : SharedPreferences? = null
    var prefsToken : SharedPreferences? = null
    var prefsCodeVerifier : SharedPreferences? = null
    private var _authen : String? = null
    var _access_token : String? = null
    var _allClaims : String? = null
    var msisdn : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        try {
            prefs = this.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            prefsToken = this.getSharedPreferences(PREF_TOKEN, PRIVATE_MODE)
            _authen  = prefs!!.getString(PREF_NAME, null)
            _access_token =  prefsToken!!.getString("access_token", null)
            _allClaims =  prefsToken!!.getString("allClaims", null)
            msisdn =  prefsToken!!.getString("msisdn", null)
        } catch (e : Exception) {

        }

        inits()

    }

    private fun inits() {
        dialog = ProgressDialogUtil(this)
        btnSignInWithLaoKYC = findViewById(R.id.btnSignInWithLaoKYC)
        btnSignInWithLaoKYC!!.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v!!.id == R.id.btnSignInWithLaoKYC) {

            if (_access_token.equals(null) || _access_token.equals("")) {

                dialogLaoKYC = Dialog(this)
                dialogLaoKYC!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialogLaoKYC!!.setCancelable(false)
                dialogLaoKYC!!.setContentView(R.layout.activity_dialog)

                ivLoginMainClose = dialogLaoKYC!!.findViewById(R.id.ivLoginMainClose)
                etLoginPhonenumber = dialogLaoKYC!!.findViewById(R.id.etLoginPhonenumber)
                btnLoginSubmit = dialogLaoKYC!!.findViewById(R.id.btnLoginSubmit)

                dialogLaoKYC!!.show()

                btnLoginSubmit!!.setOnClickListener({
                    val status: Int = NetworkUtil.getConnectivityStatusString(this)
                    if(status == 0) {

                        dialog!!.showKAlertDialog("ກະລຸນາເຊື່ອມຕໍ່ອິນເຕີເນັດ." , "warning")

                        return@setOnClickListener
                    }


                    if(etLoginPhonenumber!!.text.toString().trim().length == 0) {
                        dialog!!.showKAlertDialog("ກະລຸນາປ້ອນເບີໂທລະສັບຂອງທ່ານ." , "warning")
                        etLoginPhonenumber!!.requestFocus()
                        return@setOnClickListener
                    }

                    var _OperatorN = CheckOperatorByPhonenumber(etLoginPhonenumber!!.getText().toString())

                    if ( _OperatorN.equals("")) {
                        dialog!!.showKAlertDialog("ກະລຸນປ້ອນເບີໂທລະສັບຂອງທ່ານໃຫ້ຖືກຕ້ອງ (ເລີ່ມຕົ້ນດ້ວຍ 20 ຫຼື 30)" , "warning")

                        return@setOnClickListener
                    }

                    if (etLoginPhonenumber!!.getText().toString().substring(0, 2).equals(
                                    "20") || etLoginPhonenumber!!.getText().toString().substring(0, 2).equals("30")
                    ) else {
                        dialog!!.showKAlertDialog("ກະລຸນປ້ອນເບີໂທລະສັບຂອງທ່ານໃຫ້ຖືກຕ້ອງ (ເລີ່ມຕົ້ນດ້ວຍ 20 ຫຼື 30)" , "warning")

                        val pos: Int = etLoginPhonenumber!!.getText().length
                        etLoginPhonenumber!!.setSelection(pos)
                        etLoginPhonenumber!!.requestFocus()
                        return@setOnClickListener

                    }

                    // Process Send SMS OTP
                    getRequestOTP(etLoginPhonenumber!!.text.toString())
                })

                ivLoginMainClose!!.setOnClickListener({
                    dialogLaoKYC!!.dismiss()
                })

            } else {

                val mainIntent = Intent(this, DashboardActivity::class.java)
                mainIntent.putExtra("allClaims", _allClaims)
                mainIntent.putExtra("access_token" , _access_token)
                mainIntent.putExtra("msisdn" , msisdn)
                mainIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainIntent)

            }


        }
    }

    private fun CheckOperatorByPhonenumber(_TextNumber: String): String? {
        try {
            return if (_TextNumber.startsWith("20") == true) { // 20 - Pre-paid or Post-Paid Number
                if (_TextNumber.length == 10) {
                    if (_TextNumber.startsWith("205")) {
                        _OperatorName = "LTC"
                        _OperatorName
                    } else if (_TextNumber.startsWith("202")) {
                        _OperatorName = "ETL"
                        _OperatorName
                    } else if (_TextNumber.startsWith("207")) {
                        _OperatorName = "TPlus"
                        _OperatorName
                    } else if (_TextNumber.startsWith("209")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else if (_TextNumber.startsWith("208")) {
                        _OperatorName = "Sky"
                        _OperatorName
                    } else if (_TextNumber.startsWith("206")) {
                        _OperatorName = "LAOSAT"
                        _OperatorName
                    } else if (_TextNumber.startsWith("203")) {
                        _OperatorName = "Planet"
                        _OperatorName
                    } else {
                        _OperatorName = ""
                        _OperatorName
                    }
                } else {
                    _OperatorDB = ""
                    ""
                }
            } else if (_TextNumber.startsWith("30") == true) { // 30 -  WinPhone Number
                if (_TextNumber.length == 9) {
                    if (_TextNumber.startsWith("305")) {
                        _OperatorName = "LTC"
                        _OperatorName
                    } else if (_TextNumber.startsWith("302")) {
                        _OperatorName = "ETL"
                        _OperatorName
                    } else if (_TextNumber.startsWith("307")) {
                        _OperatorName = "TPlus"
                        _OperatorName
                    } else if (_TextNumber.startsWith("309")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else if (_TextNumber.startsWith("308")) {
                        _OperatorName = "Sky"
                        _OperatorName
                    } else if (_TextNumber.startsWith("306")) {
                        _OperatorName = "LAOSAT"
                        _OperatorName
                    } else if (_TextNumber.startsWith("303")) {
                        _OperatorName = "Planet"
                        _OperatorName
                    } else if (_TextNumber.startsWith("304")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else {
                        _OperatorName = ""
                        _OperatorName
                    }
                } else {
                    _OperatorName = ""
                    _OperatorName
                }
            } else {
                if (_TextNumber.length == 8) { // 5 - 2- 7 -9 All Operator
                    if (_TextNumber.startsWith("5")) {
                        _OperatorName = "LTC"
                        _OperatorName
                    } else if (_TextNumber.startsWith("2")) {
                        _OperatorName = "ETL"
                        _OperatorName
                    } else if (_TextNumber.startsWith("7")) {
                        _OperatorName = "TPlus"
                        _OperatorName
                    } else if (_TextNumber.startsWith("9")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else if (_TextNumber.startsWith("8")) {
                        _OperatorName = "Sky"
                        _OperatorName
                    } else if (_TextNumber.startsWith("6")) {
                        _OperatorName = "LAOSAT"
                        _OperatorName
                    } else if (_TextNumber.startsWith("3")) {
                        _OperatorName = "Planet"
                        _OperatorName
                    } else if (_TextNumber.startsWith("4")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else {
                        _OperatorDB = ""
                        _OperatorName
                    }
                } else if (_TextNumber.length == 7) {
                    if (_TextNumber.startsWith("5")) {
                        _OperatorName = "LTC"
                        _OperatorName
                    } else if (_TextNumber.startsWith("2")) {
                        _OperatorName = "ETL"
                        _OperatorName
                    } else if (_TextNumber.startsWith("7")) {
                        _OperatorName = "TPlus"
                        _OperatorName
                    } else if (_TextNumber.startsWith("9")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else if (_TextNumber.startsWith("8")) {
                        _OperatorName = "Sky"
                        _OperatorName
                    } else if (_TextNumber.startsWith("6")) {
                        _OperatorName = "LAOSAT"
                        _OperatorName
                    } else if (_TextNumber.startsWith("3")) {
                        _OperatorName = "Planet"
                        _OperatorName
                    } else if (_TextNumber.startsWith("4")) {
                        _OperatorName = "Unitel"
                        _OperatorName
                    } else {
                        _OperatorName
                    }
                } else {
                    _OperatorName = ""
                    _OperatorName
                }
            }
        } catch (e: java.lang.Exception) {
        }
        return null
    }

    private fun getRequestOTP(Msisdn: String) {

        dialog!!.showDialogProgress(  "ຂໍລະຫັດ OTP...")

        val url = Keys.API_URL_GATEWAY + "login"


        var DeviceID = Settings.Secure.getString(applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID)



        val params = HashMap<String,String>()
        params["phone"] = Msisdn
        params["Device"] = DeviceID
        val jsonObject = JSONObject(params as Map<*, *>)


        // Volley post request with parameters
        val request = JsonObjectRequest(
                Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    // Process the json
                    dialogLaoKYC!!.dismiss()
                    dialog!!.dismissDialogProgress()
                    try {

                        val strResp = response.toString()
                        val urlBodyGson = Gson().fromJson(strResp, ModelRegisterAccount::class.java)

                        if (urlBodyGson.code == 201) {
                            etLoginPhonenumber!!.text = null
                            AuthLogin(Msisdn)
                        } else if (urlBodyGson.code == 6009) {  // Not Gen OTP ຍັງບໍ່ໝົດເວລາ

                            AuthLogin(Msisdn)

                        } else if (urlBodyGson.code == 500) {
                            dialog!!.showKAlertDialog(urlBodyGson.message , "warning")
                        } else if (urlBodyGson.code == 600) {
                            dialog!!.showKAlertDialog(urlBodyGson.message , "warning")
                        } else if (urlBodyGson.code == 5004) {  // Not Gen OTP ຍັງບໍ່ໝົດເວລາ
                            dialog!!.showKAlertDialog(urlBodyGson.message , "warning")
                        } else if (urlBodyGson.code == 6007) {  // Not Gen OTP ຍັງບໍ່ໝົດເວລາ

                            AuthLogin(Msisdn)


                        } else {
                            val responseBody = urlBodyGson.message

                            if (responseBody.contains("PasswordRequiresNonAlphanumeric,PasswordRequiresUpper")) {

                                dialog!!.showKAlertDialog("ລະຫັດຜ່ານຍັງຂາດອັກສອນໂຕໃຫຍ່ ແລະ ຕົວອັກຄະລະພິເສດ.", "warning")

                            } else if (responseBody.contains("PasswordRequiresNonAlphanumeric")) {

                                dialog!!.showKAlertDialog("ລະຫັດຜ່ານຍັງຂາດຕົວອັກຄະລະພິເສດ." , "warning")

                            } else if (responseBody.contains("failure, account already exist")) {

                                dialog!!.showKAlertDialog(urlBodyGson.message , "warning")

                            }  else {

                                dialog!!.showKAlertDialog(urlBodyGson.message , "warning")
                            }
                        }

                    }catch (e:Exception){

                    }

                }, Response.ErrorListener{
            dialog!!.dismissDialogProgress()

            dialog!!.showKAlertDialog("ຂໍອະໄພ ລະບົບມີບັນຫາ ກະລຸນາລອງໃໝ່ພາຍຫຼັງ." , "warning")
        })


        // Volley request policy, only one time request to avoid duplicate transaction
        request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        // Add the volley post request to the request queue
        VolleySingleton.getInstance(this).addToRequestQueue(request)

    }

    private fun AuthLogin(Phone : String) {
        val authManager: AuthManager = AuthManager.getInstance(this)
        val authService: AuthorizationService = authManager.getAuthService()
        val auth: Auth = authManager.getAuth()

        val additionalParams: MutableMap<String, String> =
                HashMap()
        additionalParams["phone"] = Phone
        additionalParams["platform"] = "Android"

        val authRequestBuilder = AuthorizationRequest.Builder(
                authManager.getAuthConfig(),
                auth.getClientId(),
                auth.getResponseType(),
                Uri.parse(auth.getRedirectUri())
        )
                .setScope(auth.getScope())
                .setAdditionalParameters(additionalParams)
                .setPrompt("consent") // login / consent
                //.setPrompt("login")

        //Generate and save code verifier to be used later
        val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()

        prefsCodeVerifier = this.getSharedPreferences(PREF_CodeVerifier, PRIVATE_MODE)
        val editorCodeVerifier = prefsCodeVerifier!!.edit()
        editorCodeVerifier.putString("Verifier", codeVerifier)
        editorCodeVerifier.apply()

        val sharedPreferencesRepository =
                SharedPreferencesRepository(this)
        sharedPreferencesRepository.saveCodeVerifier(codeVerifier)

        authRequestBuilder.setCodeVerifier(codeVerifier)

        val authRequest = authRequestBuilder.build()
        val authIntent = Intent(this, LoginOnResultActivity::class.java)
        val pendingIntent =
                PendingIntent.getActivity(this, authRequest.hashCode(), authIntent, 0)

        authService.performAuthorizationRequest(
                authRequest,
                pendingIntent

        )



    }

}