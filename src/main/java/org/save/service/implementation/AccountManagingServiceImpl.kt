package org.save.service.implementation

import org.save.model.dto.user.ChangeRoleDto
import org.save.model.entity.common.RoleEntity
import org.save.model.entity.common.User
import org.save.model.enums.AccountStatusEnum
import org.save.model.enums.RoleEnum
import org.save.exception.NoSuchObjectException
import org.save.repo.RoleRepository
import org.save.repo.UserRepository
import org.save.service.AccountManagingService
import org.save.util.jwt.JwtUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Service
open class AccountManagingServiceImpl(private val userRepository: UserRepository,
                                      private val jwtUtils: JwtUtils,
                                      private val roleRepository: RoleRepository) : AccountManagingService {

    private val EXPIRATION_PERIOD = 90

    @Transactional
    override fun deleteUserAccount(authorizationHeader: String?) {
        val token = jwtUtils.extractJwtToken(authorizationHeader)
        val username = jwtUtils.getUsernameFromToken(token)
        val user = getUserByUsername(username)
        user.accountStatus = AccountStatusEnum.DELETED
        user.roleEntitySet = mutableSetOf(roleRepository.findRoleEntityByName(RoleEnum.ROLE_BANNED))
        user.expirationDays = EXPIRATION_PERIOD
        userRepository.save(user)
    }

    override fun getUserBack(authorizationHeader: String?) {
        val token = jwtUtils.extractJwtToken(authorizationHeader)
        val username = jwtUtils.getUsernameFromToken(token)
        val user = getUserByUsername(username)
        user.accountStatus = AccountStatusEnum.REGULAR
        user.roleEntitySet = mutableSetOf(roleRepository.findRoleEntityByName(RoleEnum.ROLE_USER))
        userRepository.save(user)
    }

    override fun changeUserRole(username: String, roles: Set<ChangeRoleDto>) {
        if (roles.isEmpty()) {
            throw NoSuchObjectException("Set of roles is empty")
        }
        val user = getUserByUsername(username)
        val roleEntities = HashSet<RoleEntity>(roles.size)
        roles.forEach { role ->
            val roleEntity = roleRepository.findRoleEntityByName(role.getName())
            roleEntities.add(roleEntity)
        }
        user.roleEntitySet = roleEntities
        userRepository.save(user)
    }

    override fun getUserRoles(username: String): List<RoleEnum> =
        getUserByUsername(username)
            .roleEntitySet
            .map { roleEntity -> roleEntity.name }

    private fun getUserByUsername(username: String?): User {
        return userRepository
            .findByUsername(username)
            .orElseThrow {
                NoSuchObjectException(
                    "There is no user with such username"
                )
            }
    }

}
