package hs.kr.gbsw.doumi.board.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hs.kr.gbsw.doumi.board.dto.EventItem
import hs.kr.gbsw.doumi.board.dto.EventResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class EventService(
) {

    @Value("\${board.event.url}")
    lateinit var eventInfoApi: String

    @Value("\${board.event.encode}")
    lateinit var key: String

    fun eventList(date: String): EventResponseDto {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val now = LocalDate.parse(date, formatter).minusDays(1)
//        val startRange = now.minusDays(30)
        val endRange = now.plusDays(15)

        val urlStr = "$eventInfoApi?serviceKey=$key&type=json"
        println(urlStr)
        val objectMapper = jacksonObjectMapper()
        val allEvents = mutableListOf<EventItem>()

        var pageNo = 1
        var keepFetching = true

        while (keepFetching) {
            val pageUrl = URL("$urlStr&pageNo=$pageNo&numOfRows=100")
            val conn = pageUrl.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            val responseCode = conn.responseCode
            val response = if (responseCode == 200) {
                conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                conn.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            val jsonNode: JsonNode = objectMapper.readTree(response)
            val itemsNode: JsonNode = jsonNode.path("response").path("body").path("items")

            println("jsonNode: $jsonNode")
            println("itemsNode: $itemsNode")

            val events: List<EventItem> = when {
                itemsNode.isArray -> objectMapper.convertValue(itemsNode, object : TypeReference<List<EventItem>>() {})
                itemsNode.isObject -> listOf(objectMapper.convertValue(itemsNode, EventItem::class.java))
                else -> emptyList()
            }

            val filteredEvents = events.filter { event ->
//                val eventStart = LocalDate.parse(event.fstvlStartDate, formatter)
                val eventEnd = LocalDate.parse(event.fstvlEndDate, formatter)
//                !(eventEnd.isBefore(startRange) || eventStart.isAfter(endRange))
                (eventEnd.isBefore(endRange) && eventEnd.isAfter(now))
            }

            allEvents.addAll(filteredEvents)

            if (events.size < 100) {
                keepFetching = false
            } else {
                pageNo++
            }

            if (allEvents.size >= 1000) {
                keepFetching = false
            }
        }

        return EventResponseDto(allEvents)
    }

}