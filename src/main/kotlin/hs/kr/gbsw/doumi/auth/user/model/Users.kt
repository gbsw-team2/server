package hs.kr.gbsw.doumi.auth.user.model

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
class Users(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = true)
    var password: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    var country: Country?,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var provider: String?,

    @Column(nullable = true)
    var providerId: String?

)