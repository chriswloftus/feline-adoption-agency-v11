package uk.ac.aber.dcs.cs31620.faa.datasource

import uk.ac.aber.dcs.cs31620.faa.model.CatDao
import uk.ac.aber.dcs.cs31620.faa.model.FostererDao

interface RoomDatabaseI {
    fun catDao(): CatDao
    fun fostererDao(): FostererDao
    fun closeDb()
}