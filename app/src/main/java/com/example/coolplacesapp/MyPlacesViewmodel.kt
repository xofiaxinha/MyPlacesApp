package com.example.coolplacesapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class MyPlacesViewmodel: ViewModel(){
    val myPlaces = mutableStateListOf<MyPlaces>()
        private set
    val selectedPlaces = mutableStateListOf<MyPlaces>()
        private set
    fun addPlace(place: MyPlaces){
        myPlaces.add(place)
    }

    fun toggleSelected(place: MyPlaces){
        if (selectedPlaces.contains(place)){
            selectedPlaces.remove(place)
        }else{
            if(selectedPlaces.size < 2){
                selectedPlaces.add(place)
            }else{
                selectedPlaces.clear()
                selectedPlaces.add(place)
            }
        }
    }
}