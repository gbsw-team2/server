package hs.kr.gbsw.doumi.map.controller

import hs.kr.gbsw.doumi.map.dto.MapRequestDto
import hs.kr.gbsw.doumi.map.dto.MapResponseDto
import hs.kr.gbsw.doumi.map.service.MapService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/api/map")
@RestController
class MapController(
    val mapService: MapService,
) {

    @GetMapping
    fun map(
        @RequestParam ctpvNm: String,
        @RequestParam sggNm: String,
    ): ResponseEntity<MapResponseDto> {
        val dto = MapRequestDto(ctpvNm, sggNm)
        val response = mapService.getCenterList(dto)
        return ResponseEntity(response, HttpStatus.OK)
    }

}