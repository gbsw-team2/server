package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.board.dto.CreateCommentDto
import hs.kr.gbsw.doumi.board.dto.ResponseCommentDto
import hs.kr.gbsw.doumi.board.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RequestMapping("/api/comment")
@RestController
class CommentController(
    val commentService: CommentService,
) {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<String> {
        return ResponseEntity("${ex.message}", HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(IllegalAccessException::class)
    fun handleIllegalAccess(ex: IllegalAccessException): ResponseEntity<String> {
        return ResponseEntity("${ex.message}", HttpStatus.UNAUTHORIZED)
    }

    @PostMapping("/{postId}")
    fun createComment(
        principal: Principal,
        @PathVariable postId: Long,
        @RequestBody dto: CreateCommentDto
    ): ResponseEntity<ResponseCommentDto> {
        val result = commentService.create(dto, principal.name, postId)
        val response = ResponseCommentDto(
            id = result.id!!,
            body = result.body,
            createdAt = result.createdAt,
            updatedAt = result.createdAt,
            isWritten = result.isWritten,
        )
        return ResponseEntity(response, HttpStatus.CREATED)
    }

    @GetMapping("/{postId}")
    fun getListByComment(
        @PathVariable postId: Long
    ): ResponseEntity<List<ResponseCommentDto>> {
        val result = commentService.getCommentByPost(postId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PutMapping("/{postId}/{commentId}")
    fun modifyComment(
        principal: Principal,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestBody dto: CreateCommentDto
    ): ResponseEntity<ResponseCommentDto> {
        val result = commentService.updateComment(commentId, dto, principal.name)
        val response = ResponseCommentDto(
            id = result.first.id!!,
            body = result.first.body,
            createdAt = result.first.createdAt,
            updatedAt = result.first.createdAt,
            isWritten = result.first.isWritten,
        )
        return if (result.second) ResponseEntity(response, HttpStatus.OK) else ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @DeleteMapping("/{postId}/{commentId}")
    fun deleteComment(
        principal: Principal,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        ): ResponseEntity<Void> {
        commentService.deleteComment(commentId, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}