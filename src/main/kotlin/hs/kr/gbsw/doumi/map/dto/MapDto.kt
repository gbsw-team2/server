package hs.kr.gbsw.doumi.map.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

data class MapRequestDto(
    val ctpvNm: String?,
    val sggNm: String?,
)

data class MapResponseDto(
    val items: List<CenterItem>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class CenterItem(
    val cnterNm: String,
    val cnterChNm: String,
    val operMbyCn: String,
    val operModeCn: String,
    val ctpvNm: String,
    val sggNm: String,
    val roadNmAddr: String,
    val lotnoAddr: String,
    val hmpgAddr: String,
    val rprsTelno: String,
    val dscsnTelno: String,
    val fxno: String,
    val emlAddr: String,
    val operHrCn: String,
    val empCnt: Int,
    val pvsnLngNm: String,
    val crtrYmd: String,
    val expsrYn: String,
    val rmrkCn: String?
)