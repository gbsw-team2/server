package hs.kr.gbsw.doumi.auth.user.repository

import hs.kr.gbsw.doumi.auth.user.model.Country
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CountryRepository: JpaRepository<Country, Int>