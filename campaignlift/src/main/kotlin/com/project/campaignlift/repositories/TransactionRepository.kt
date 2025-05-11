package com.project.campaignlift.repositories

import com.project.banking.entities.TransactionEntity
import com.project.common.enums.TransactionType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
interface TransactionRepository: JpaRepository<TransactionEntity, Long> {
    @Query("SELECT SUM(t.amount) FROM TransactionEntity t WHERE t.id IN :ids AND t.type = :type")
    fun sumByIdInAndType(@Param("ids") ids: List<Long>, @Param("type") type: TransactionType): BigDecimal?

}