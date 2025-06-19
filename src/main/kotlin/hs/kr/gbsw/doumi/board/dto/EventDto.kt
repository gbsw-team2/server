package hs.kr.gbsw.doumi.board.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class EventResponseDto(
    val items: List<EventItem>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventItem (
val fstvlNm: String,
val opar: String,
val fstvlStartDate: String,
val fstvlEndDate: String,
val homepageUrl: String,
)