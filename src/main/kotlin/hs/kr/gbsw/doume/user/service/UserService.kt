package hs.kr.gbsw.doume.user.service

import hs.kr.gbsw.doume.common.dto.BaseResponse
import hs.kr.gbsw.doume.common.exception.InvalidInputException
import hs.kr.gbsw.doume.common.status.ResponseCode
import hs.kr.gbsw.doume.user.dto.UserSignupDto
import hs.kr.gbsw.doume.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {

    fun signup(dto: UserSignupDto): BaseResponse<Unit> {

        var user = userRepository.findByEmail(dto.email)
        if (user != null) {
            return BaseResponse(ResponseCode.ERROR.name, null, "이미 존재하는 이메일입니다.")
        }

        user = dto.toEntity()
        userRepository.save(user)

        return BaseResponse(ResponseCode.SUCCESS.name, null, "회원가입이 완료 되었습니다.")
    }

}