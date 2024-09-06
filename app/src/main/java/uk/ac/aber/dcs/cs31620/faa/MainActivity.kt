package uk.ac.aber.dcs.cs31620.faa

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import uk.ac.aber.dcs.cs31620.faa.model.CatsViewModel
import uk.ac.aber.dcs.cs31620.faa.model.map.LocationViewModel
import uk.ac.aber.dcs.cs31620.faa.ui.authentication.LoginScreen
import uk.ac.aber.dcs.cs31620.faa.ui.cats.AddCatScreenTopLevel
import uk.ac.aber.dcs.cs31620.faa.ui.cats.CatsScreenTopLevel
import uk.ac.aber.dcs.cs31620.faa.ui.home.HomeScreenTopLevel
import uk.ac.aber.dcs.cs31620.faa.ui.map.FostererMapScreenTopLevel
import uk.ac.aber.dcs.cs31620.faa.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.faa.ui.theme.FAATheme

/**
 * Starting activity class. Entry point for the app.
 * @author Chris Loftus
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FAATheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapPermissions()
                    BuildNavigationGraph()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun MapPermissions(){
    val locationPermissions = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    // Call when the app gets launched for the first time and not on recompositions
    LaunchedEffect(true) {
        locationPermissions.launchMultiplePermissionRequest()
    }
}

@Composable
fun BuildNavigationGraph(
    locationViewModel: LocationViewModel = viewModel(),
    catsViewModel: CatsViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    var startDestination = remember { Screen.Home.route }

    val ctx = LocalContext.current as Activity
    val viewCatsAction = stringResource(R.string.action_view_cats)
    val catsUri = stringResource(R.string.cats_uri)

    ctx.intent?.let {
        if (it.action != null && it.action == viewCatsAction) {
            if (it.data != null && it.data.toString() == catsUri) {
                startDestination = Screen.Cats.route
            }
        }
    }
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route){ HomeScreenTopLevel(navController, catsViewModel) }
        composable(Screen.Cats.route){ CatsScreenTopLevel(navController, catsViewModel) }
        composable(Screen.Login.route) { LoginScreen(navController) }
        composable(Screen.AddCat.route) { AddCatScreenTopLevel(navController) }
        composable(Screen.Map.route) { FostererMapScreenTopLevel(navController, catsViewModel, locationViewModel) }
    }
}


