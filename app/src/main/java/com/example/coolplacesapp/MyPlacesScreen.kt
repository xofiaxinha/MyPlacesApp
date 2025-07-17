package com.example.coolplacesapp


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlacesMapScreen(viewModel: MyPlacesViewmodel) {
    val context = LocalContext.current
    var location by remember { mutableStateOf<LatLng?>(null) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Permissão de localização
    val locationPermissionGranted = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        locationPermissionGranted.value = granted
        if (granted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                loc?.let { location = LatLng(it.latitude, it.longitude) }
            }
        }
    }

    // Solicita permissão na primeira execução
    LaunchedEffect(Unit) {
        if (!locationPermissionGranted.value) {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { loc: Location? ->
                loc?.let { location = LatLng(it.latitude, it.longitude) }
            }
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            location ?: LatLng(-3.7445, -38.4957), 12f
        )
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("MyPlacesApp") }) }
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = locationPermissionGranted.value),
                uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                onMapLongClick = { latLng ->
                    // Exemplo: Adicione um lugar sem dialog (direto)
                    viewModel.addPlace(MyPlaces("Lugar ${viewModel.myPlaces.size + 1}", latLng))
                }
            ) {
                // (Opcional) Marcador para o local atual
                location?.let {
                    Marker(
                        state = MarkerState(position = it),
                        title = "Você está aqui"
                    )
                }
                // Marcadores adicionados pelo usuário
                for (place in viewModel.myPlaces) {
                    Marker(
                        state = MarkerState(position = place.latLong),
                        title = place.name,
                        snippet = "Marcado por você",
                        onClick = {
                            viewModel.toggleSelected(place)
                            true
                        }

                    )
                }
                // Desenhar Polyline entre os dois selecionados
                if (viewModel.selectedPlaces.size == 2) {
                    Polyline(
                        points = listOf(
                            viewModel.selectedPlaces[0].latLong,
                            viewModel.selectedPlaces[1].latLong
                        ),
                        width = 8f
                    )
                }
            }
        }
    }
}