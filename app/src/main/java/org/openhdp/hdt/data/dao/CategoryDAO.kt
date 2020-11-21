package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.entities.Category

@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun createCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE id=:id")
    suspend fun findCategory(id: String): Category?

    @Query("SELECT * FROM categories ORDER BY customOrder")
    suspend fun getAllCategoriesOrdered(): List<Category>

}