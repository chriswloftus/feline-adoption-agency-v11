package uk.ac.aber.dcs.cs31620.faa.ui.map

import android.annotation.SuppressLint
import android.location.Location
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import uk.ac.aber.dcs.cs31620.faa.R
import uk.ac.aber.dcs.cs31620.faa.model.CatsViewModel
import uk.ac.aber.dcs.cs31620.faa.model.FostererCat
import uk.ac.aber.dcs.cs31620.faa.model.map.LocationViewModel
import uk.ac.aber.dcs.cs31620.faa.ui.components.TopLevelScaffold
import uk.ac.aber.dcs.cs31620.faa.ui.theme.FAATheme

@Composable
fun FostererMapScreenTopLevel(
    navController: NavHostController,
    catsViewModel: CatsViewModel,
    locationViewModel: LocationViewModel
) {
    val fostererCatList by catsViewModel.fostererCatList.observeAsState(listOf())

    FostererMapScreen(
        navController = navController,
        currentLocation = locationViewModel.lastLocation,
        getLastLocation = { locationViewModel.getLastLocation() },
        fostererCatList = fostererCatList
    )
}

@Composable
fun FostererMapScreen(
    navController: NavHostController,
    currentLocation: Location?,
    getLastLocation: () -> Unit = { },
    fostererCatList: List<FostererCat> = listOf()
) {
    val coroutineScope = rememberCoroutineScope()

    TopLevelScaffold(
        navController = navController,
        coroutineScope = coroutineScope
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            FostererMapScreenContent(
                modifier = Modifier.padding(8.dp),
                currentLocation,
                getLastLocation,
                fostererCatList = fostererCatList
            )
        }
    }
}

//@OptIn(ExperimentalPermissionsApi::class, MapsComposeExperimentalApi::class) // For clustering
@SuppressLint("UnrememberedMutableState")
@Composable
private fun FostererMapScreenContent(
    modifier: Modifier = Modifier,
    currentLocation: Location?,
    getLastLocation: () -> Unit = { },
    fostererCatList: List<FostererCat>
) {
    // Here are some resources:
    // https://github.com/googlemaps/android-maps-compose
    // https://darrylbayliss.net/jetpack-compose-for-maps/
    // https://blog.sanskar10100.dev/integrating-google-maps-places-api-and-reverse-geocoding-with-jetpack-compose
    // https://github.com/googlemaps/android-maps-utils
    // https://googlemaps.github.io/android-maps-utils/
    // https://developers.google.com/codelabs/maps-platform/maps-platform-101-compose#0

    // Make the default Limerick: if running on an emulator
    //val defaultLocation = LatLng(52.6638, -8.6267)
    /*val location = LatLng(
        currentLocation?.latitude ?: defaultLocation.latitude,
        currentLocation?.longitude ?: defaultLocation.longitude
    )*/

    val cameraPositionState = rememberCameraPositionState {}

    val uiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                compassEnabled = true,
                myLocationButtonEnabled = true,
                rotationGesturesEnabled = true,
                scrollGesturesEnabled = true,
                scrollGesturesEnabledDuringRotateOrZoom = true,
                tiltGesturesEnabled = true,
                zoomControlsEnabled = true,
                zoomGesturesEnabled = true
            )
        )
    }

    val properties by remember {
        mutableStateOf(
            MapProperties(
                isBuildingEnabled = false,
                isMyLocationEnabled = true, // Will show the dot current marker. Can take a while to appear
                isIndoorEnabled = false,
                isTrafficEnabled = false,
                mapType = MapType.NORMAL,
                maxZoomPreference = 21f,
                minZoomPreference = 3f
            )
        )
    }
    GoogleMap(
        modifier = modifier,
        properties = properties,
        uiSettings = uiSettings,
        cameraPositionState = cameraPositionState
    ) {
        // Calling this here will mean it will get called
        // through recomposition if the map changes, e.g. when the
        // location marker is added or changed by the Google library. That way
        // we know that the location has been found.
        // It gets called a lot of times but shouldn't be too time
        // consuming.
        getLastLocation()
        val location = currentLocation?.let {
            LatLng(currentLocation.latitude, currentLocation.longitude)
        }

        // The following shows how clustering of markers is achieved in the
        // com.google.maps.android-maps-compose-utils library.
        // Unfortunately, it does not allow for configuration of marker content;
        // see comment below under clusterItemContent.
        // I've therefore commented out clustering so that markers are displayed using
        // MarkerInfoWindowContent so that we can configure the content to show images.
        /* Clustering(
             items = fostererCatList,
             onClusterClick = {

                 // Zoom into a cluster just enough to next level
                 cameraPositionState.move(
                     update = CameraUpdateFactory.zoomIn()
                 )
                 false
             },
             onClusterItemClick = {
                 false
             },
             //clusterContent = {
             // Here we can customise the appearance of the
             // cluster icon.
                 //CustomerClusterContent(cluster = it)
             //},
             //clusterItemContent = {
                 // Again we could customise a specific marker appearance.
                 // Note that we cannot add a Marker
                 // or MarkerInfoWindowContent to customise the
                 // internal content of what is displayed when the marker is tapped.
                 // This is unfortunate and is a known issue.
                 //CustomClusterContentItem(it)
             //}
         )*/
        // If we didn't use Clustering we can use markers or MarkerInfoWindowContent
        for (fostererCat in fostererCatList) {
            DisplayMarker(fostererCat)
            /*Marker(
                state = MarkerState(
                    position = LatLng(fostererCat.latitude, fostererCat.longitude)
                ),
                title = fostererCat.catName
            )*/
        }

        // Move to the current location, if known
        location?.let {
            // Move to actual current location and zoom in
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(it, 12f)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
private fun DisplayMarker(
    fostererCat: FostererCat
) {
    MarkerInfoWindowContent(
        rememberMarkerState(position = fostererCat.position),
        //title = fostererCat.title,
        //snippet = fostererCat.catDescription
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                //.fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = fostererCat.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = fostererCat.catDescription,
                color = Color.Black
            )
            GlideImage(
                model = Uri.parse(fostererCat.image),
                contentDescription = stringResource(R.string.cat_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(100.dp)
                    .clip(RoundedCornerShape(70.dp))
                    .clickable { }
            )
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
private fun CustomMarkerContent(
    fostererCat: FostererCat
) {
    MarkerInfoWindowContent(
        state = MarkerState(
            position = fostererCat.position
        ),
        title = fostererCat.title,
        snippet = fostererCat.snippet
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                //.fillMaxWidth()
                .padding(32.dp)
        ) {
            Text(
                modifier = Modifier.padding(top = 6.dp),
                text = fostererCat.title,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            /*data.imageResourceId?.let {
                Image(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    painter = painterResource(id = it),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = ""
                )
            }*/
        }
    }
}

@Composable
private fun CustomerClusterContent(cluster: Cluster<FostererCat>) {
    val size = cluster.size
    Surface(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp),
        shape = CircleShape,
        color = Color.Blue,
        contentColor = Color.White,
        border = BorderStroke(1.dp, Color.White)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = size.toString(),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CustomClusterContentItem(fostererCat: FostererCat) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = fostererCat.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Customer icon for Cluster",
            tint = Color.Red
        )
    }
}

@Preview
@Composable
private fun FostererMapScreenPreview() {
    val navController = rememberNavController()
    FAATheme(dynamicColor = false) {
        FostererMapScreen(navController, null)
    }
}