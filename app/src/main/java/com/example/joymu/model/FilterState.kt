package com.example.joymu.model

data class FilterState(
    val minPriceKes: Float = 0f,
    val maxPriceKes: Float = 300000f,
    val selectedBedrooms: Int? = null, // null = Any, 0 = Studio, 1, 2, 3, 4
    val selectedBathrooms: Int? = null, // null = Any, 1, 2, 3
    val selectedAmenities: Set<String> = emptySet()
) {
    fun isFiltered(): Boolean {
        return minPriceKes > 0f ||
                maxPriceKes < 300000f ||
                selectedBedrooms != null ||
                selectedBathrooms != null ||
                selectedAmenities.isNotEmpty()
    }
}
