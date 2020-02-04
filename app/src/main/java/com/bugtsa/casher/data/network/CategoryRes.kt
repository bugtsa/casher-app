package com.bugtsa.casher.data.network

import com.google.gson.annotations.SerializedName

data class CategoryRes(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)