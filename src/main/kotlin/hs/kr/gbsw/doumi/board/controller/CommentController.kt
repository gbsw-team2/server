package hs.kr.gbsw.doumi.board.controller

import hs.kr.gbsw.doumi.board.dto.CreateCommentDto
import hs.kr.gbsw.doumi.board.dto.ResponseCommentDto
import hs.kr.gbsw.doumi.board.dto.UpdateCommentDto
import hs.kr.gbsw.doumi.board.model.Comment
import hs.kr.gbsw.doumi.board.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @PostMapping("/{postId}")
    fun createComment(@PathVariable postId: Long, @RequestBody dto: CreateCommentDto, principal: Principal): ResponseEntity<Comment> {
        val result = commentService.create(dto, principal.name, postId)
        return ResponseEntity(result, HttpStatus.CREATED)
    }

    @GetMapping("/{postId}")
    fun getListByComment(@PathVariable postId: Long): ResponseEntity<List<ResponseCommentDto>> {
        val result = commentService.getCommentByPost(postId)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @PutMapping("/{postId}/{commentId}")
    fun modifyComment(@PathVariable commentId: Long, @RequestBody dto: UpdateCommentDto, principal: Principal): ResponseEntity<Comment> {
        val result = commentService.updateComment(commentId, dto, principal.name)
        return ResponseEntity(result, HttpStatus.OK)
    }

    @DeleteMapping("/{postId}/{commentId}")
    fun deleteComment(@PathVariable commentId: Long, principal: Principal): ResponseEntity<Void> {
        commentService.deleteComment(commentId, principal.name)
        return ResponseEntity(HttpStatus.NO_CONTENT)
    }
}