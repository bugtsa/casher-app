package com.bugtsa.casher.data.dto

import com.bugtsa.casher.data.network.CredentialAuthRes
import com.bugtsa.casher.utils.ConstantManager.Constants.EMPTY

class AuthDto(res: CredentialAuthRes) {

    val email: String
    val accessToken: String
    val refreshToken: String

    init {
        email = res.email ?: EMPTY
        accessToken = res.accessToken ?: EMPTY
        refreshToken = res.refreshToken ?: EMPTY
    }
}