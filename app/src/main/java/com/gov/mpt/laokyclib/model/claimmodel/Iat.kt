package com.gov.mpt.laokyc.model.claimmodel


import com.google.gson.annotations.SerializedName

/**
 * Created by SBlab on 2020-10-29.
 */

data class Iat(
    @SerializedName("value")
    val value: Int?
)