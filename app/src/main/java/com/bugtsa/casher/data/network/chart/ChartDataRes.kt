package com.bugtsa.casher.data.network.chart

data class ChartDataRes(
        val requestYear: String? = null,
        val requestMonth: String? = null,
        val categorizedMap: Map<String, String>? = null,
        val costAllPayments: String? = null
)
