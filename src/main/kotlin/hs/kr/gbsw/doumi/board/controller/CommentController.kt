package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.board.dto.CreateCommentDto
import hs.kr.gbsw.doumi.board.dto.ResponseCommentDto
import hs.kr.gbsw.doumi.board.dto.UpdateCommentDto
import hs.kr.gbsw.doumi.board.model.Comment
import hs.kr.gbsw.doumi.board.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RequestMapping("/comment")
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
        @AuthenticationPrincipal principal: Principal,
        @PathVariable postId: Long,
        @RequestBody dto: CreateCommentDto
    ): ResponseEntity<Comment> {
        val result = commentService.create(dto, principal.name, postId)
        return ResponseEntity(result, HttpStatus.CREATED)
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
        @AuthenticationPrincipal principal: Principal,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        @RequestBody dto: UpdateCommentDto
    ): ResponseEntity<Comment> {
        val result = commentService.updateComment(commentId, dto, principal.name)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @DeleteMapping("/{postId}/{commentId}")
    fun deleteComment(
        @AuthenticationPrincipal principal: Principal,
        @PathVariable postId: Long,
        @PathVariable commentId: Long,
        ): ResponseEntity<Void> {
        commentService.deleteComment(commentId, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}