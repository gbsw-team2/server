package hs.kr.gbsw.doumi.board.service

import hs.kr.gbsw.doumi.auth.user.model.Country

import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.auth.user.repository.CountryRepository
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import hs.kr.gbsw.doumi.board.dto.CreatePostDto
import hs.kr.gbsw.doumi.board.model.Like
import hs.kr.gbsw.doumi.board.model.Post
import hs.kr.gbsw.doumi.board.repository.BoardRepository
import hs.kr.gbsw.doumi.board.repository.LikeRepository

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

@Service
class BoardService(
    val boardRepository: BoardRepository,
    val countryRepository: CountryRepository,
    val userRepository: UserRepository,
    val likeRepository: LikeRepository,
) {

    fun createPost(dto: CreatePostDto, email: String): Post {
        val user = userRepository.findByEmail(email)!!
        val country = countryRepository.findById(dto.country).get()
        val post = dto.toEntity(user, country)

        return boardRepository.save(post)
    }

    fun getCountries(): List<Country> {
        return countryRepository.findAll()
    }

    fun getListByCountryId(
        countryId: Int,
        page: Int,
        keyword: String
    ): Page<Post> {
        val sorts: ArrayList<Sort.Order> = ArrayList()
        sorts.add(Sort.Order.desc("createdDate"))
        val pageable = PageRequest.of(page, 10, Sort.by(sorts))

        val spec = search(keyword)

        return boardRepository.findByCountryId(countryId, spec, pageable)
    }

    fun getPost(id: Long): Post {
        return boardRepository.findById(id).orElseThrow { NoSuchElementException("Post with id $id not found.") }
    }

    fun getLike(id: Long): Int {
        return likeRepository.getLikesByPostId(id).count()
    }

    fun getIsLike(email: String, postId: Long): Boolean {
        return likeRepository.existsByUserEmailAndId(email, postId)
    }

    fun addView(postId: Long): Int {
        val post = boardRepository.findById(postId).get()
        post.view += 1
        boardRepository.save(post)
        return post.view
    }

    fun modifyPost(
        email: String,
        id: Long,
        dto: CreatePostDto
    ): Pair<Post, Boolean> {
        val post = getPost(id)
        if (post.user.email != email) {
            throw IllegalAccessException("Can only modify own post")
        }
        var modified = false
        if (post.title != dto.title) {
            post.title = dto.title
            post.isWritten = true
            modified = true
        }
        if (post.body != dto.body) {
            post.body = dto.body
            post.isWritten = true
            modified = true
        }
        if (post.country.id != dto.country) {
            post.country = countryRepository.findById(dto.country).get()
            post.isWritten = true
            modified = true
        }

        return if(modified) Pair(boardRepository.save(post), true) else Pair(post, false)
    }

    fun deletePost(email: String, id: Long) {
        val post = getPost(id)
        if (post.user.email != email) {
            throw IllegalAccessException("Can only delete own post")
        }
        boardRepository.delete(post)
    }

    private fun search(keyword: String): Specification<Post> {
        return Specification<Post> { post, query, criteriaBuilder ->
            val kw = "%$keyword%"
            query!!.distinct(true)

            val user: Join<Post, Users> = post.join("user", JoinType.LEFT)

            val predicate1 = criteriaBuilder.like(post.get("title"), kw)
            val predicate2 = criteriaBuilder.like(post.get("body"), kw)
            val predicate3 = criteriaBuilder.like(user.get("email"), kw)

            criteriaBuilder.or(
                predicate1,
                criteriaBuilder.or(predicate2, predicate3)
            )
        }
    }

    fun postLike(email: String, postId: Long): Like {
        val user = userRepository.findByEmail(email)!!
        val post = getPost(postId)
        val like = Like(user = user, post = post)
        return likeRepository.save(like)
    }

    fun deleteLike(email: String, postId: Long) {
        val like = likeRepository.getLikeByUserEmailAndPostId(email, postId)
        likeRepository.delete(like)
    }

}