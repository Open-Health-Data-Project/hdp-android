package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.openhdp.hdt.data.Category
import org.openhdp.hdt.data.IndependentCategories

@Dao
interface IndependentCategoriesDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createIndependentCategories(independentCategories: IndependentCategories)

    @Update
    suspend fun updateIndependentCategories(independentCategories: IndependentCategories)

    @Delete
    suspend fun deleteIndependentCategories(independentCategories: IndependentCategories)

    @Query("SELECT EXISTS(SELECT * FROM independent_categories WHERE category1 IN (:cat1, :cat2) AND category2 IN (:cat1, :cat2) AND category1 != category2)")
    fun areIndependentCategories(cat1: Category, cat2: Category): LiveData<Boolean>

}