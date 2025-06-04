package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.board.dto.CreatePostDto
import hs.kr.gbsw.doumi.board.dto.EventResponseDto
import hs.kr.gbsw.doumi.board.dto.ResponsePostDto
import hs.kr.gbsw.doumi.board.model.Like
import hs.kr.gbsw.doumi.board.model.Post
import hs.kr.gbsw.doumi.board.service.BoardService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal
import java.time.LocalDate

@RequestMapping("/board")
@RestController
class BoardController(
    val boardService: BoardService,
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity("${ex.message}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(ex: IllegalAccessException): ResponseEntity<String> {
        return ResponseEntity("${ex.message}", HttpStatus.UNAUTHORIZED)
    }

    @PostMapping
    fun createPost(@RequestBody dto: CreatePostDto, principal: Principal): ResponseEntity<Post> {
        val result = boardService.createPost(dto, principal.name)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @GetMapping
    fun countryList(): ResponseEntity<List<Country>> {
        val result = boardService.getCountries()
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{countryId}")
    fun getListByCountry(
        @PathVariable(required = true) countryId: Int,
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "keyword", defaultValue = "") keyword: String,
    ): ResponseEntity<Page<Post>> {
        val result = boardService.getListByCountryId(countryId, page, keyword)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @GetMapping("/{postId}")
    fun getPost(@PathVariable(required = true) postId: Long, principal: Principal): ResponseEntity<ResponsePostDto> {
        val result = boardService.getPost(postId)
        val like = boardService.getLike(postId)
        val isLike = boardService.getIsLike(principal.name, postId)
        val response = ResponsePostDto(
            result.title,
            result.body,
            like,
            isLike,
            result.view,
            result.createdAt,
            result.updatedAt,
            result.isWritten,
        )
        return ResponseEntity(response, HttpStatus.OK)
     }

    @PutMapping("/{postId}")
    fun modifyPost(@PathVariable(required = true) postId: Long, @RequestBody dto: CreatePostDto, principal: Principal): ResponseEntity<Post> {
        val result = boardService.modifyPost(principal.name, postId, dto)
        return if (result.second) ResponseEntity(result.first, HttpStatus.OK) else ResponseEntity(result.first, HttpStatus.NOT_MODIFIED)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(@PathVariable postId: Long, principal: Principal): ResponseEntity<Void> {
        boardService.deletePost(principal.name, postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @PostMapping("/{postId}/like")
    fun postLike(@PathVariable(required = true) postId: Long, principal: Principal): ResponseEntity<Like> {
        val result = boardService.postLike(principal.name, postId)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @DeleteMapping("/{postId}/like")
    fun deleteLike(@PathVariable(required = true) postId: Long, principal: Principal): ResponseEntity<Void> {
        boardService.deleteLike(email = principal.name, postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/event")
    fun event(): ResponseEntity<EventResponseDto> {
        val response = boardService.eventList(LocalDate.now().toString())
        return ResponseEntity(response, HttpStatus.OK)
    }

}