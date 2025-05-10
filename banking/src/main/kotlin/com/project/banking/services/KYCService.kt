package com.project.banking.services

import com.project.banking.entities.KYCEntity
import com.project.banking.kycs.dtos.KYCRequest
import com.project.common.responses.authenthication.UserInfoDto


interface KYCService {
    fun createKYCOrUpdate(kycRequest: KYCRequest, user: UserInfoDto): KYCEntity
    fun findKYCByUserId(userId: Long): KYCEntity?
}