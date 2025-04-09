package hs.kr.gbsw.doumi.map.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hs.kr.gbsw.doumi.map.dto.CenterItem
import hs.kr.gbsw.doumi.map.dto.MapRequestDto
import hs.kr.gbsw.doumi.map.dto.MapResponseDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

@Service
class MapService {

    @Value("\${map.url}")
    lateinit var mtpcltFamSpcn: String

    @Value("\${map.encode}")
    lateinit var key: String

    fun getCenterList(dto: MapRequestDto): MapResponseDto {

        val ctpvNm = dto.ctpvNm?.let {
            "&ctpvNm=" + URLEncoder.encode(it, "UTF-8")
        } ?: ""
        val sggNm = dto.sggNm?.let {
            "&sggNm=" + URLEncoder.encode(it, "UTF-8")
        } ?: ""

        val urlStr = "$mtpcltFamSpcn?serviceKey=$key&pageNo=1&numOfRows=100&type=json$sggNm$ctpvNm"
        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection

        conn.requestMethod = "GET"
        conn.setRequestProperty("Accept", "application/json")
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        val responseCode = conn.responseCode
        val response = if (responseCode == 200) {
            conn.inputStream.bufferedReader().use(BufferedReader::readText)
        } else {
            conn.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
        }

        val objectMapper = jacksonObjectMapper()
        val root = objectMapper.readTree(response)

        val itemsNode = root.path("response").path("body").path("items").path("item")
        val centerList = when {
            itemsNode.isArray -> objectMapper.convertValue(itemsNode, object : TypeReference<List<CenterItem>>() {})
            itemsNode.isObject -> listOf(objectMapper.convertValue(itemsNode, CenterItem::class.java))
            else -> emptyList()
        }

        return MapResponseDto(centerList)

    }
}
