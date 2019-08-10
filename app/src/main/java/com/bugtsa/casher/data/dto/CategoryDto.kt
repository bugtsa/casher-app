package com.bugtsa.casher.data.dto

import com.bugtsa.casher.data.local.database.entity.category.CategoryEntity

class CategoryDto {

    var id: Long
    var name: String

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
    }

    constructor(categoryEntity: CategoryEntity) {
        this.id = categoryEntity.remoteId
        this.name = categoryEntity.name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CategoryDto

        if (id != other.id) return false
        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }


}