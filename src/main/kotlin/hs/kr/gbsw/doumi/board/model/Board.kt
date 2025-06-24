package hs.kr.gbsw.doumi.board.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import hs.kr.gbsw.doumi.auth.user.model.Country
import hs.kr.gbsw.doumi.auth.user.model.Users
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
class Post(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(length = 200)
    var title: String,

    var body: String,

    var view: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
    val user: Users,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "country_id")
    @JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
    var country: Country,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime,
    var isWritten: Boolean = false,
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt =LocalDateTime.now()
        isWritten = true
    }
}