package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.board.dto.CreatePostDto
import hs.kr.gbsw.doumi.board.dto.EventResponseDto
import hs.kr.gbsw.doumi.board.dto.ResponsePostDto
import hs.kr.gbsw.doumi.board.model.Like
import hs.kr.gbsw.doumi.board.model.Post
import hs.kr.gbsw.doumi.board.service.BoardService
import hs.kr.gbsw.doumi.board.service.EventService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate

@RequestMapping("/api/board")
@RestController
class BoardController(
    val boardService: BoardService,
    private val eventService: EventService,
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity.status(404).body("${ex.message}")
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(ex: IllegalAccessException): ResponseEntity<String> {
        return ResponseEntity.status(401).body("${ex.message}")
    }

    @PostMapping("/post")
    fun createPost(
        principal: Principal,
        @RequestBody dto: CreatePostDto
    ): ResponseEntity<ResponsePostDto> {
        val result = boardService.createPost(dto, principal.name)
        val response = ResponsePostDto(
            result.id!!,
            result.title,
            result.body,
            0,
            false,
            0,
            result.createdAt,
            result.updatedAt,
            result.isWritten
        )
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping("/posts")
    fun countryList(
    ): ResponseEntity<List<Country>> {
        val result = boardService.getCountries()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/posts/{countryId}")
    fun getListByCountry(
        @PathVariable(required = true) countryId: Int,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "keyword", defaultValue = "") keyword: String,
    ): ResponseEntity<Page<Post>> {
        val result = boardService.getListByCountryId(countryId, page, keyword)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/post/{postId}")
    fun getPost(
        principal: Principal,
        @PathVariable(required = true) postId: Long
    ): ResponseEntity<ResponsePostDto> {
        val result = boardService.getPost(postId)
        val like = boardService.getLike(postId)
        val isLike = boardService.getIsLike(principal.name, postId)
        val view = boardService.addView(postId)
        val response = ResponsePostDto(
            result.id!!,
            result.title,
            result.body,
            like,
            isLike,
            view,
            result.createdAt,
            result.updatedAt,
            result.isWritten,
        )
        return ResponseEntity(response, HttpStatus.OK)
     }

    @PutMapping("/post/{postId}")
    fun modifyPost(
        principal: Principal,
        @PathVariable(required = true) postId: Long,
        @RequestBody dto: CreatePostDto
    ): ResponseEntity<ResponsePostDto> {
        val result = boardService.modifyPost(principal.name, postId, dto)
        val response = ResponsePostDto(
            result.first.id!!,
            result.first.title,
            result.first.body,
            boardService.getLike(postId),

            boardService.getIsLike(principal.name, postId),
            result.first.view,
            result.first.createdAt,
            result.first.updatedAt,
            result.first.isWritten
        )
        return if (result.second) ResponseEntity(response, HttpStatus.OK) else ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/post/{postId}")
    fun deletePost(
        principal: Principal,
        @PathVariable postId: Long
    ): ResponseEntity<Void> {
        boardService.deletePost(principal.name, postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/post/{postId}/like")
    fun postLike(
        principal: Principal,
        @PathVariable(required = true) postId: Long
    ): ResponseEntity<Like> {
        val result = boardService.postLike(principal.name, postId)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @DeleteMapping("/post/{postId}/like")
    fun deleteLike(
        principal: Principal,
        @PathVariable(required = true) postId: Long
    ): ResponseEntity<Void> {
        boardService.deleteLike(email = principal.name, postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/event")
    fun event(): ResponseEntity<EventResponseDto> {
        val response = eventService.eventList(LocalDate.now().toString())
        return ResponseEntity(response, HttpStatus.OK)
    }

}