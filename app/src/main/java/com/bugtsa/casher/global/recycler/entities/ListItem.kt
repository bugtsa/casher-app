package com.bugtsa.casher.global.recycler.entities

import java.util.UUID

abstract class ListItem {

    var id: Int = 0
    val uuid: UUID = UUID.randomUUID()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ListItem

        if (id != other.id) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + uuid.hashCode()
        return result
    }

}