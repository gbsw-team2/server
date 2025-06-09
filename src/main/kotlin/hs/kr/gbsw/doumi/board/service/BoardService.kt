package hs.kr.gbsw.doumi.board.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import hs.kr.gbsw.doumi.auth.user.model.Country

import hs.kr.gbsw.doumi.auth.user.model.Users
import hs.kr.gbsw.doumi.auth.user.repository.CountryRepository
import hs.kr.gbsw.doumi.auth.user.repository.UserRepository
import hs.kr.gbsw.doumi.board.dto.CreatePostDto
import hs.kr.gbsw.doumi.board.dto.EventItem
import hs.kr.gbsw.doumi.board.dto.EventResponseDto
import hs.kr.gbsw.doumi.board.dto.ResponsePostDto
import hs.kr.gbsw.doumi.board.model.Like
import hs.kr.gbsw.doumi.board.model.Post
import hs.kr.gbsw.doumi.board.repository.BoardRepository
import hs.kr.gbsw.doumi.board.repository.LikeRepository

import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.JoinType

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service

import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class BoardService(
    val boardRepository: BoardRepository,
    val countryRepository: CountryRepository,
    val userRepository: UserRepository,
    val likeRepository: LikeRepository,
) {

//    @Value("\${board.event.url}")
//    lateinit var eventInfoApi: String

//    @Value("\${board.event.encode}")
//    lateinit var key: String

//    fun eventList(date: String): EventResponseDto {
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
//        val now = LocalDate.parse(date, formatter)
//
//        val urlStr = "$eventInfoApi?serviceKey=$key&type=json"
//        val objectMapper = jacksonObjectMapper()
//        val allEvents = mutableListOf<EventItem>()
//
//        val initUrl = URL("$urlStr&pageNo=1&numOfRows=1")
//        val initConn = initUrl.openConnection() as HttpURLConnection
//        initConn.requestMethod = "GET"
//        initConn.setRequestProperty("Accept", "application/json")
//        val initResponse = initConn.inputStream.bufferedReader().use { it.readText() }
//
//        val initJson = objectMapper.readTree(initResponse)
//        val total = initJson.path("response").path("body").path("totalCount").intValue()
//        val totalPages = Math.ceil(total / 100.0).toInt()
//
//        for (i in 1..totalPages) {
//            val pageUrl = URL("$urlStr&pageNo=$i&numOfRows=100")
//            val conn = pageUrl.openConnection() as HttpURLConnection
//            conn.requestMethod = "GET"
//            conn.setRequestProperty("Accept", "application/json")
//            val response = conn.inputStream.bufferedReader().use { it.readText() }
//
//            val jsonNode = objectMapper.readTree(response)
//            val itemsNode = jsonNode.path("response").path("body").path("items").path("item")
//
//            val events = when {
//                itemsNode.isArray -> objectMapper.convertValue(itemsNode, object : TypeReference<List<EventItem>>() {})
//                itemsNode.isObject -> listOf(objectMapper.convertValue(itemsNode, EventItem::class.java))
//                else -> emptyList()
//            }
//
//            events.filterTo(allEvents) { event ->
//                val endDate = LocalDate.parse(event.eventEndDate, formatter)
//                !endDate.isBefore(now)
//            }
//        }
//
//        return EventResponseDto(allEvents)
//    }

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

        return if(modified) Pair(boardRepository.save(post), modified) else Pair(post, modified)
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

            criteriaBuilder.or(
                criteriaBuilder.like(post.get("title"), kw),
                criteriaBuilder.like(post.get("body"), kw),
                criteriaBuilder.like(user.get("email"), kw),
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