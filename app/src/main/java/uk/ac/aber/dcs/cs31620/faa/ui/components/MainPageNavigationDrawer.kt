package uk.ac.aber.dcs.cs31620.faa.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import uk.ac.aber.dcs.cs31620.faa.R
import uk.ac.aber.dcs.cs31620.faa.ui.navigation.Screen
import uk.ac.aber.dcs.cs31620.faa.ui.theme.FAATheme
/**
 * Creates the navigation drawer. Uses Material 3 ModalNavigationDrawer.
 * Current implementation has an image at the top and then three items.
 * @param navController To pass through the NavHostController since navigation is required
 * @param drawerState The state of the drawer, i.e. whether open or closed
 * @param closeDrawer To pass in the close navigation drawer behaviour as a lambda.
 * By default has an empty lambda.
 * @param content To pass in the page content for the page when the navigation drawer is closed
 * @author Chris Loftus
 */
@Composable
fun MainPageNavigationDrawer(
    navController: NavHostController,
    drawerState: DrawerState,
    closeDrawer: () -> Unit = {},
    content: @Composable () -> Unit = {}
) {

    val items: List<Pair<ImageVector, String>> = listOf(
        Pair(
            Icons.AutoMirrored.Filled.Login,
            stringResource(R.string.login)
        ),
        Pair(
            Icons.AutoMirrored.Filled.Help,
            stringResource(R.string.help)
        ),
        Pair(
            Icons.Default.Feedback,
            stringResource(R.string.feedback)
        )
    )
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Drawer content goes here
            val selectedItem = rememberSaveable { mutableIntStateOf(0) }

            ModalDrawerSheet {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        modifier = Modifier
                            .size(200.dp)
                            .padding(bottom = 16.dp, top = 16.dp),
                        painter = painterResource(id = R.drawable.kitten_small),
                        contentDescription =
                        stringResource(R.string.small_kitten),
                        contentScale = ContentScale.Crop
                    )
                    items.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = item.first,
                                    contentDescription = item.second
                                )
                            },
                            label = { Text(item.second) },
                            selected = index == selectedItem.intValue,
                            onClick = {
                                selectedItem.intValue = index
                                // Exercise solution. If the first item (login button) then
                                // just close the drawer and navigate
                                if (index == 0) {
                                    // If we don't do this the drawer will be hidden
                                    // when we navigate to the login screen, however,
                                    // the back button etc will take us back to the
                                    // open drawer, which is not usual behaviour
                                    closeDrawer()
                                    navController.navigate(route = Screen.Login.route)
                                }
                            }
                        )
                    }
                }
            }
        },
        // Page content here
        content = content
    )
}

@Preview
@Composable
private fun MainPageNavigationDrawerPreview() {
    FAATheme(dynamicColor = false) {
        val navController = rememberNavController()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Open)
        MainPageNavigationDrawer(navController, drawerState)
    }
}