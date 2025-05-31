package hs.kr.gbsw.doumi.auth.user.repository

import hs.kr.gbsw.doumi.auth.user.model.Country
import org.springframework.data.jpa.repository.JpaRepository

interface CountryRepository: JpaRepository<Country, Int>