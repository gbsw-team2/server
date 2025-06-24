package hs.kr.gbsw.doumi.board.model

import hs.kr.gbsw.doumi.auth.user.model.Users
import jakarta.persistence.*

@Entity
@Table(
    name = "post_like",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id", "post_id"])
    ]
)
data class Like(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: Users,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    var post: Post,
)
