package com.gov.mpt.laokyc.model.claimmodel


import com.google.gson.annotations.SerializedName

/**
 * Created by SBlab on 2020-10-29.
 */

data class ClaimModel(
    @SerializedName("amr")
    val amr: Amr?,
    @SerializedName("at_hash")
    val atHash: AtHash?,
    @SerializedName("aud")
    val aud: Aud?,
    @SerializedName("auth_time")
    val authTime: AuthTime?,
    @SerializedName("exp")
    val exp: Exp?,
    @SerializedName("family_name")
    val familyName: FamilyName?,
    @SerializedName("iat")
    val iat: Iat?,
    @SerializedName("idp")
    val idp: Idp?,
    @SerializedName("iss")
    val iss: Iss?,
    @SerializedName("name")
    val name: Name?,
    @SerializedName("nbf")
    val nbf: Nbf?,
    @SerializedName("phone")
    val phone: Phone?,
    @SerializedName("picture")
    val picture: Picture?,
    @SerializedName("preferred_username")
    val preferredUsername: PreferredUsername?,
    @SerializedName("s_hash")
    val sHash: SHash?,
    @SerializedName("sid")
    val sid: Sid?,
    @SerializedName("sub")
    val sub: Sub?,
    @SerializedName("website")
    val website: Website?,
    @SerializedName("account")
    val account: Account?
)