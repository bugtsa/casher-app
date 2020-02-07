package com.bugtsa.casher.presentation.optional


sealed class ProgressState {
    data class Progress(val isCancelable: Boolean,
                        val delayToShow : Long = 0) : ProgressState()
    object Hide : ProgressState()
}