package com.bugtsa.casher.data.dto

import com.google.gson.annotations.SerializedName

data class CategoryRes(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
)