package com.ns.transferex.domain.business

import com.ns.transferex.application.exceptions.BusinessException
import com.ns.transferex.application.exceptions.DomainNotFoundException
import com.ns.transferex.application.service.TransferService
import com.ns.transferex.domain.AccountRepository
import com.ns.transferex.domain.Transaction
import com.ns.transferex.domain.TransactionRepository
import io.micronaut.spring.tx.annotation.Transactional
import javax.inject.Singleton

@Singleton
open class TransferServiceImp(private val accountRepository: AccountRepository,
                              private val transactionRepository: TransactionRepository) : TransferService {

    @Transactional
    override fun applyTransfer(transaction: Transaction) {

        val fromAccount = accountRepository.findById(transaction.fromAccount)
                .orElseThrow { throw DomainNotFoundException("Account not found") }
        val toAccount = accountRepository.findById(transaction.toAccount)
                .orElseThrow { throw DomainNotFoundException("Account not found") }

        if (fromAccount == toAccount) {
            throw BusinessException("Transfer must be between different accounts")
        }

        fromAccount.withdraw(transaction.amount)
        toAccount.add(transaction.amount)
        accountRepository.update(fromAccount)
        accountRepository.update(toAccount)
        transactionRepository.insert(transaction)
    }
}