package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.service.UserService
import hs.kr.gbsw.doumi.board.dto.CreatePostDto
import hs.kr.gbsw.doumi.board.dto.EventResponseDto
import hs.kr.gbsw.doumi.board.dto.ResponsePostDto
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
    private val userService: UserService,
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity("${ex.message}", HttpStatus.NOT_FOUND)
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
    fun getPost(@PathVariable(required = true) postId: Long): ResponseEntity<ResponsePostDto> {
        val result = boardService.getPost(postId)
        val response = ResponsePostDto(
            result.title,
            result.body,
            result.like,
            result.createdAt,
            result.updatedAt,
            result.isWritten,
        )
        return ResponseEntity(response, HttpStatus.OK)
     }

    @PutMapping("/{postId}")
    fun modifyPost(@PathVariable(required = true) postId: Long, @RequestBody dto: CreatePostDto): ResponseEntity<Post> {
        val result = boardService.modifyPost(postId, dto)
        return if (result.second) ResponseEntity(result.first, HttpStatus.OK) else ResponseEntity(result.first, HttpStatus.NOT_MODIFIED)
    }

    @DeleteMapping("/{postId}")
    fun deletePost(@PathVariable postId: Long): ResponseEntity<Void> {
        boardService.deletePost(postId)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/event")
    fun event(): ResponseEntity<EventResponseDto> {
        val response = boardService.eventList(LocalDate.now().toString())
        return ResponseEntity(response, HttpStatus.OK)
    }

}