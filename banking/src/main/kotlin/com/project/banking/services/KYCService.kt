package com.project.banking.services

import com.project.banking.entities.KYCEntity
import com.project.banking.kycs.dtos.KYCRequest


interface KYCService {
    fun createKYCOrUpdate(kycRequest: KYCRequest, userId: Long): KYCEntity
    fun findKYCByUserId(userId: Long): KYCEntity?
}