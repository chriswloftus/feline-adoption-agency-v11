package uk.ac.aber.dcs.cs31620.faa.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

@Entity(
    tableName = "fosterers",

)
data class Fosterer(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
)