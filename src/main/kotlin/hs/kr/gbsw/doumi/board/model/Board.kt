package hs.kr.gbsw.doumi.board.model

import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.model.Users
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 200)
    var title: String,

    var body: String,

    var like: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: Users,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id", nullable = false)
    var country: Country,

    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var isWritten: Boolean = false,
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt =LocalDateTime.now()
        isWritten = true
    }
}