package org.openhdp.hdt.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import org.openhdp.hdt.data.relations.CategoryWithStopwatches

@Dao
interface CategoryWithStopwatchesDAO {

    @Transaction
    @Query("SELECT * FROM categories")
    fun getCategoryWithStopwatches(): LiveData<List<CategoryWithStopwatches>>
}