package uk.ac.aber.dcs.cs31620.faa.datasource

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.ac.aber.dcs.cs31620.faa.datasource.util.GenderConverter
import uk.ac.aber.dcs.cs31620.faa.datasource.util.LocalDateTimeConverter
import uk.ac.aber.dcs.cs31620.faa.model.Cat
import uk.ac.aber.dcs.cs31620.faa.model.CatDao
import uk.ac.aber.dcs.cs31620.faa.model.Fosterer
import uk.ac.aber.dcs.cs31620.faa.model.FostererDao
import uk.ac.aber.dcs.cs31620.faa.model.Gender
import java.time.LocalDateTime

@Database(entities = [Cat::class, Fosterer::class], version = 1)
@TypeConverters(LocalDateTimeConverter::class, GenderConverter::class)
abstract class FaaPersistentRoomDatabase : RoomDatabase(), RoomDatabaseI {
    abstract override fun catDao(): CatDao
    abstract override fun fostererDao(): FostererDao

    override fun closeDb() {
        instance?.close()
        instance = null
    }

    companion object {
        private var instance: FaaPersistentRoomDatabase? = null
        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        val MIGRATION_1_2 = object : Migration(1,2){
            override fun migrate(database: SupportSQLiteDatabase) {
                Log.d("migrate", "Doing a migrate from version 1 to 2")
                // This is where we make relevant database data changes,
                // or copy data from old table to a new table.
                // Deals with the migration from version 1 to version 2
            }
        }

        @Synchronized
        fun getDatabase(context: Context): FaaPersistentRoomDatabase? {
            if (instance == null) {
                instance =
                    Room.databaseBuilder<FaaPersistentRoomDatabase>(
                        context.applicationContext,
                        FaaPersistentRoomDatabase::class.java,
                        "faa_database"
                    )
                        //.allowMainThreadQueries()
                        .addCallback(roomDatabaseCallback(context))
                        //.addMigrations(MIGRATION_1_2)
                        .build()
            } // if
            return instance
        }

        private fun roomDatabaseCallback(context: Context): Callback {
            return object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    coroutineScope.launch {
                        populateDatabase(context, getDatabase(context)!!)
                    }
                }
            }
        }

        private suspend fun populateDatabase(context: Context, instance: FaaPersistentRoomDatabase) {

            val imagePath = "file:///android_asset/images/"

            // Populate some Fosterers
            val fosterer1 = Fosterer(
                1,
                "Mike",
                52.6638,
                -8.6267
            )

            val fosterer2 = Fosterer(
                2,
                "Sarah",
                52.6636,
                -8.6223
            )

            val fosterer3 = Fosterer(
                3,
                "Pete",
                52.6755,
                -8.6475
            )
            val fosterer4 = Fosterer(
                4,
                "Edel",
                52.6634,
                -8.6226
            )
            val fosterer5 = Fosterer(
                5,
                "June",
                52.6613,
                -8.6204
            )
            val fosterer6 = Fosterer(
                6,
                "Jon",
                52.6620,
                -8.6258
            )
            val fosterer7 = Fosterer(
                7,
                "Jon",
                52.66555,
                -8.6337
            )
            val fosterer8 = Fosterer(
                8,
                "Jon",
                52.6546,
                -8.6295
            )

            val fostererList = listOf(
                fosterer1,
                fosterer2,
                fosterer3,
                fosterer4,
                fosterer5,
                fosterer6,
                fosterer7,
                fosterer8
            )

            val fostererDao = instance.fostererDao()
            fostererDao.insertMultipleFosterers(fostererList)

            // Now create and insert the cats
            val upToOneYear = LocalDateTime.now().minusDays(365 / 2)
            val from1to2Years = LocalDateTime.now().minusDays(365 + (36 / 2))
            val from2to5Years = LocalDateTime.now().minusDays(365 * 3)
            val over5Years = LocalDateTime.now().minusDays(365 * 10)
            val admissionsDate = LocalDateTime.now().minusDays(60)
            val veryRecentAdmission = LocalDateTime.now()

            val upToOneYearCat1 = Cat(
                0, "Tibs (< 1)", Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor...",
                upToOneYear,
                veryRecentAdmission,
                "${imagePath}cat1.png",
                fosterer1.id
            )

            val upToOneYearCat2 = Cat(
                0, "Tibs (< 1)", Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor...",
                upToOneYear,
                veryRecentAdmission,
                "${imagePath}cat1.png",
                fosterer2.id
            )

            val from1to2YearsCat1 = Cat(
                0,
                "Tibs (1 - 2)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from1to2Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer3.id
            )

            val from1to2YearsCat2 = Cat(
                0,
                "Tibs (1 - 2)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from1to2Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer4.id
            )

            val from2to5YearsCat1 = Cat(
                0,
                "Tibs (2 - 5)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from2to5Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer5.id
            )

            val from2to5YearsCat2 = Cat(
                0,
                "Tibs (2 - 5)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                from2to5Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer6.id
            )

            val over5YearsCat1 = Cat(
                0,
                "Tibs (> 5)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                over5Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer7.id
            )

            val over5YearsCat2 = Cat(
                0,
                "Tibs (> 5)",
                Gender.MALE,
                "Moggie",
                "Lorem ipsum dolor sit amet, consectetur...",
                over5Years,
                admissionsDate,
                "${imagePath}cat1.png",
                fosterer8.id
            )

            val catList = mutableListOf(
                upToOneYearCat1,
                upToOneYearCat2,
                from1to2YearsCat1,
                from1to2YearsCat2,
                from2to5YearsCat1,
                from2to5YearsCat2,
                over5YearsCat1,
                over5YearsCat2
            )

            val catDao = instance.catDao()
            catDao.insertMultipleCats(catList)
        }
    }
}