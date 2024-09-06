/**
 * Represents a Cat
 * @author Chris Loftus
 */
package uk.ac.aber.dcs.cs31620.faa.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity(
    tableName = "cats",
    foreignKeys = [
        ForeignKey(
            entity = Fosterer::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("fostererId"),
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Cat(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var gender: Gender = Gender.FEMALE,
    var breed: String = "",
    var description: String = "",
    var dob: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "admission_date")
    var admissionDate: LocalDateTime = LocalDateTime.now(),
    @ColumnInfo(name = "main_image_path")
    var imagePath: String = "",
    var fostererId: Int = 0
) {

    fun isKitten(): Boolean {
        val today = LocalDate.now()
        val fromDays = dob.until(today, ChronoUnit.DAYS)

        return (fromDays < 365)
    }
}