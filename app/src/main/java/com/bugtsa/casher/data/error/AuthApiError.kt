package com.bugtsa.casher.data.error

import com.google.gson.annotations.SerializedName

data class AuthApiError (
        @field:SerializedName("error")
        val error: String? = null,

        @field:SerializedName("error_description")
        val errorDescription: String? = null

)