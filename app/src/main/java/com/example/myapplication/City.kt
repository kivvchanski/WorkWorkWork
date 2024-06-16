package com.example.myapplication

class City(

    val name: String,
    val lat: Double,
    val long: Double
) :Comparable<City> {
    private val thisName = name
    private val latitude = lat
    private val longitude = long


    override fun compareTo(other: City): Int {
        if (this.name == other.name) {
            return 0
        }
        val list = arrayOf(this.name, other.name)
        if (this.name == list[0]) {
            return -1
        }
        else {
            return 1
        }
    }


}