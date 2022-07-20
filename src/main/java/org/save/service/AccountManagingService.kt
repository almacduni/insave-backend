package org.save.service

import org.save.model.dto.user.ChangeRoleDto
import org.save.model.enums.RoleEnum

interface AccountManagingService {

    fun deleteUserAccount(authorizationHeader: String?)

    fun getUserBack(authorizationHeader: String?)

    fun changeUserRole(username: String, roles: Set<ChangeRoleDto>)

    fun getUserRoles(username: String): List<RoleEnum>

}
