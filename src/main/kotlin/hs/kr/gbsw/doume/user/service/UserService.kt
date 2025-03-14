package hs.kr.gbsw.doume.user.service

import hs.kr.gbsw.doume.common.dto.BaseResponse
import hs.kr.gbsw.doume.common.status.ResponseCode
import hs.kr.gbsw.doume.user.dto.UserSignupRequest
import hs.kr.gbsw.doume.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun signup(dto: UserSignupRequest): BaseResponse<Unit> {

        var user = userRepository.findByEmail(dto.email!!)
        if (user != null) {
            return BaseResponse(ResponseCode.ERROR.name, null, "이미 존재하는 이메일입니다.")
        }

        user = dto.toEntity(passwordEncoder.encode(dto.password))
        userRepository.save(user)

        return BaseResponse(ResponseCode.SUCCESS.name, null, "회원가입이 완료 되었습니다.")
    }

}