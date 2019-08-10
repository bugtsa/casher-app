package com.bugtsa.casher.data.local.database.entity.category

import androidx.room.*
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