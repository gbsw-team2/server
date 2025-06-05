//package hs.kr.gbsw.doumi.user.model
//
//import jakarta.persistence.Column
//import jakarta.persistence.Entity
//import jakarta.persistence.FetchType
//import jakarta.persistence.GeneratedValue
//import jakarta.persistence.GenerationType
//import jakarta.persistence.Id
//import jakarta.persistence.JoinColumn
//import jakarta.persistence.JoinTable
//import jakarta.persistence.ManyToMany
//import org.hibernate.annotations.CreationTimestamp
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.userdetails.User
//import java.time.LocalDateTime
//
//@Entity
//class Users(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    var id: Long? = null,
//
//    @Column(nullable = false)
//    var email: String,
//
//    @Column(nullable = false)
//    var password: String,
//
//    @Column(nullable = false)
//    var country: Int?,
//
//    @CreationTimestamp
//    var createdAt: LocalDateTime = LocalDateTime.now(),
//
//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//        name = "user_roles",
//        joinColumns = [JoinColumn(name = "user_id")],
//        inverseJoinColumns = [JoinColumn(name = "role_id")]
//    )
//    var roles: Set<Role>
//) {
//    fun getAuthorities(): Collection<GrantedAuthority> {
//        return listOf(SimpleGrantedAuthority("USER"))
//
//    }
//}
//
//class Role(
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    var id: Long? = null,
//
//    @Column(nullable = false, unique = true)
//    var name: String
//) : GrantedAuthority {
//    override fun getAuthority(): String = name
//}