package com.bugtsa.casher.global.extentions

sealed class KeyboardState

object AllHidden : KeyboardState()
object SoftShown : KeyboardState()