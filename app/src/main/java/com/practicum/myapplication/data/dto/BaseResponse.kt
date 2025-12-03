package com.practicum.myapplication.data.dto

open class BaseResponse() {
    var resultCode: Int = 0
    var errorMessage: String? = null
}