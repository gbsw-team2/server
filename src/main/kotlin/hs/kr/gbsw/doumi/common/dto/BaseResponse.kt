package hs.kr.gbsw.doumi.common.dto

import hs.kr.gbsw.doumi.common.status.ResponseCode

data class BaseResponse<T>(
    val responseCode: String = ResponseCode.SUCCESS.name,
    val data: T? = null,
    val message : String = ResponseCode.SUCCESS.msg,
)