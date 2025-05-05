package com.project.banking.accounts

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(path = ["/api/v1/accounts"])
class AccountsApiController {

    @GetMapping
    fun getAccounts(): String {
        return "accounts available"
    }
}