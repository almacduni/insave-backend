package org.save.model.dto.user

import org.save.model.enums.RoleEnum
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

class ChangeRoleDto {

    private var name: @Size(min = 2, max = 15) @NotNull RoleEnum? = null

    fun getName(): RoleEnum? {
        return name
    }

    fun setName(name: RoleEnum?) {
        this.name = name
    }
}
