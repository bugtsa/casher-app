package com.bugtsa.casher.data.network

import com.google.gson.annotations.SerializedName

data class CredentialAuthRes(

	@field:SerializedName("access_token")
	val accessToken: String? = null,

	@field:SerializedName("refresh_token")
	val refreshToken: String? = null,

	@field:SerializedName("scope")
	val scope: String? = null,

	@field:SerializedName("token_type")
	val tokenType: String? = null,

	@field:SerializedName("expires_in")
	val expiresIn: Int? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("jti")
	val jti: String? = null
)