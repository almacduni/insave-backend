package org.save.controller

import org.save.model.dto.user.ChangeRoleDto
import org.save.model.enums.RoleEnum
import org.save.service.AccountManagingService
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/accounts")
@Secured("ROLE_ADMIN")
open class AccountManagingController(private val accountManagingService: AccountManagingService) {

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    open fun deleteCurrentUser(@RequestHeader("Authorization") authorizationHeader: String?) {
        accountManagingService.deleteUserAccount(authorizationHeader)
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    open fun getUserBack(@RequestHeader("Authorization") authorizationHeader: String?) {
        accountManagingService.getUserBack(authorizationHeader)
    }

    @PatchMapping("/{username}/roles")
    @ResponseStatus(HttpStatus.OK)
    open fun changeUserRole(@PathVariable username: String, @RequestBody @Valid roles: Set<ChangeRoleDto>) {
        accountManagingService.changeUserRole(username, roles)
    }

    @GetMapping("/{username}/roles")
    @ResponseStatus(HttpStatus.OK)
    open fun getUserRoles(@PathVariable username: String): List<RoleEnum> {
        return accountManagingService.getUserRoles(username)
    }

}
