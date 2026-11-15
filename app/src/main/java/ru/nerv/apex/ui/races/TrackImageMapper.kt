package ru.nerv.apex.ui.races

import androidx.annotation.DrawableRes
import ru.nerv.apex.R

@DrawableRes
fun getTrackImage(circuitName: String?): Int? {
    if (circuitName == null) return null
    return when {
        circuitName.contains("Abu Dhabi", ignoreCase = true) -> R.drawable.track_abu_dhabi
        circuitName.contains("Austria", ignoreCase = true) -> R.drawable.track_austria
        circuitName.contains("Bahrain", ignoreCase = true) -> R.drawable.track_bahrain
        circuitName.contains("Baku", ignoreCase = true) -> R.drawable.track_baku
        circuitName.contains("Brazil", ignoreCase = true) -> R.drawable.track_brazil
        circuitName.contains("Catalunya", ignoreCase = true) -> R.drawable.track_catalunya
        circuitName.contains("Hungaroring", ignoreCase = true) -> R.drawable.track_hungaroring
        circuitName.contains("Imola", ignoreCase = true) -> R.drawable.track_imola
        circuitName.contains("Jeddah", ignoreCase = true) -> R.drawable.track_jeddah
        circuitName.contains("Las Vegas", ignoreCase = true) -> R.drawable.track_las_vegas
        circuitName.contains("Losail", ignoreCase = true) -> R.drawable.track_losail
        circuitName.contains("Melbourne", ignoreCase = true) -> R.drawable.track_melbourne
        circuitName.contains("Mexico", ignoreCase = true) -> R.drawable.track_mexico
        circuitName.contains("Miami", ignoreCase = true) -> R.drawable.track_miami
        circuitName.contains("Monaco", ignoreCase = true) -> R.drawable.track_monaco
        circuitName.contains("Montreal", ignoreCase = true) -> R.drawable.track_montreal
        circuitName.contains("Monza", ignoreCase = true) -> R.drawable.track_monza
        circuitName.contains("Paul Ricard", ignoreCase = true) -> R.drawable.track_paul_ricard
        circuitName.contains("Portimao", ignoreCase = true) -> R.drawable.track_portimao
        circuitName.contains("Shanghai", ignoreCase = true) -> R.drawable.track_shanghai
        circuitName.contains("Silverstone", ignoreCase = true) -> R.drawable.track_silverstone
        circuitName.contains("Singapore", ignoreCase = true) -> R.drawable.track_singapore
        circuitName.contains("Spa", ignoreCase = true) -> R.drawable.track_spa
        circuitName.contains("Suzuka", ignoreCase = true) -> R.drawable.track_suzuka
        circuitName.contains("Texas", ignoreCase = true) -> R.drawable.track_texas
        circuitName.contains("Zandvoort", ignoreCase = true) -> R.drawable.track_zandvoort

        else -> R.drawable.track_unknown
    }
}