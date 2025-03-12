package hs.kr.gbsw.doume.common.dto

import hs.kr.gbsw.doume.common.status.ResponseCode

data class BaseResponse<T>(
    val responseCode: String = ResponseCode.SUCCESS.name,
    val data: T? = null,
    val message : String = ResponseCode.SUCCESS.msg,
)