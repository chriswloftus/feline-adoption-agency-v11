package uk.ac.aber.dcs.cs31620.faa.model

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FostererDao {
    // Foster operations
    @Insert
    suspend fun insertMultipleFosterers(catsList: List<Fosterer>)

    @Query("SELECT * FROM fosterers")
    fun getAllFosterers(): LiveData<List<Fosterer>>

    @Query(
        """SELECT fosterers.name AS fosterersName, 
                  fosterers.latitude AS latitude,
                  fosterers.longitude AS longitude,
                  cats.name AS catName,
                  cats.main_image_path AS image,
                  cats.description AS catDescription
           FROM fosterers, cats
           WHERE fosterers.id = cats.fostererId"""
    )
    fun getFosterersAndTheirCats(): LiveData<List<FostererCat>>
}