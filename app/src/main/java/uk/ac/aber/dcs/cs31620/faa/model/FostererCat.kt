package uk.ac.aber.dcs.cs31620.faa.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class FostererCat(
    var catName: String = "",
    var catDescription: String = "",
    var image: String = "",
    var fosterersName: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : ClusterItem { // Mostly needed for the Clustering feature
    override fun getPosition(): LatLng = LatLng(latitude, longitude)

    override fun getTitle(): String = catName

    override fun getSnippet(): String = catDescription

    override fun getZIndex(): Float = 1f
}