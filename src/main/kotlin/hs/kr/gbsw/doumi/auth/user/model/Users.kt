package hs.kr.gbsw.doumi.auth.user.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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

    @Column(nullable = false)
    var country: Int?,

    @CreationTimestamp
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = true)
    var provider: String?,

    @Column(nullable = true)
    var providerId: String?

)