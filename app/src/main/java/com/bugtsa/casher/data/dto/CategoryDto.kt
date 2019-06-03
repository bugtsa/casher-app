package com.bugtsa.casher.data.dto

import com.bugtsa.casher.domain.local.database.model.CategoryEntity

class CategoryDto {

    var id: Long
    var name: String

    constructor(id: Long, name: String) {
        this.id = id
        this.name = name
    }

    constructor(categoryEntity: CategoryEntity) {
        this.id = categoryEntity.id
        this.name = categoryEntity.name
    }
}