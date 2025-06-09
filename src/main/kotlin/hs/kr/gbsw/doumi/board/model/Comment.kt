package hs.kr.gbsw.doumi.board.model

import hs.kr.gbsw.doumi.auth.user.model.Users
import jakarta.persistence.*
import net.minidev.json.annotate.JsonIgnore
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
class Comment(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var body: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    val user: Users,

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonIgnore
    val post: Post,

    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now(),

    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var isWritten: Boolean = false,
) {
    @PreUpdate
    fun onUpdate() {
        updatedAt = LocalDateTime.now()
        isWritten = true
    }
}