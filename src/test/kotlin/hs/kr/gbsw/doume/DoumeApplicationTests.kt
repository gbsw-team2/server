package hs.kr.gbsw.doume

import hs.kr.gbsw.doume.user.model.Users
import hs.kr.gbsw.doume.user.repository.UserRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class DoumeApplicationTests {

	@Autowired
	private lateinit var userRepository: UserRepository

	@Test
	fun signup() {
		val user: Users = Users(null, "test@test.com", "test", null)
		userRepository.save(user)
	}

}
