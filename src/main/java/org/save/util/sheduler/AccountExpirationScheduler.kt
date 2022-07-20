package org.save.util.sheduler

import org.save.model.entity.common.User
import org.save.model.enums.AccountStatusEnum
import org.save.repo.UserRepository
import org.save.repo.WatchlistRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import javax.transaction.Transactional

@Component
open class AccountExpirationScheduler(private val userRepository: UserRepository,
                                      private val watchlistRepository: WatchlistRepository) {

    private val USER_TIME_IS_EXPIRED = 0

    @Transactional
    @Scheduled(cron = "0 0 6 * * *")
    open fun countExpirationDays() {
        userRepository.findAllByAccountStatus(AccountStatusEnum.DELETED)
            .forEach { user -> updateOrDeleteUser(user) }

    }

    private fun updateOrDeleteUser(user: User) {
        user.expirationDays = user.expirationDays.dec()
        if (user.expirationDays == USER_TIME_IS_EXPIRED) {
            val watchlist = watchlistRepository.findWatchlistByUserId(user.id)
            if (watchlist.isPresent) {
                watchlistRepository.delete(watchlist.get())
            }
            userRepository.delete(user)
        }
    }

}
