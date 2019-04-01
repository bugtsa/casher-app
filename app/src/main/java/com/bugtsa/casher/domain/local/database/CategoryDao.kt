package com.bugtsa.casher.domain.local.database

import android.arch.persistence.room.*
import com.bugtsa.casher.model.CategoryEntity
import io.reactivex.Flowable

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(category: CategoryEntity): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(category: CategoryEntity)

//    @Delete
//    fun delete(categoryText: String)
//
    @Query("SELECT * FROM category")
    fun getCategories(): Flowable<List<CategoryEntity>>
}