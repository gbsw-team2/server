package hs.kr.gbsw.doumi.board.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class EventResponseDto(
    val items: List<EventItem>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class EventItem (
val eventNm: String,
val opar: String,
val eventStartDate: String,
val eventEndDate: String,
val homepageUrl: String,
)