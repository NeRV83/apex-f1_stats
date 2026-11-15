package ru.nerv.apex.ui.races

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import ru.nerv.apex.R
import java.text.SimpleDateFormat
import java.util.TimeZone

@Composable
fun formatRaceDate(isoDate: String?): String {
    if (isoDate == null) return stringResource(R.string.date_tbd)
    val currentLocale = Locale.current.platformLocale
    return try {
        val cleanDate = isoDate.replace(" ", "T")
            .substringBefore("+")
            .substringBefore("Z")
            .substringBefore(".")

        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", currentLocale)
        parser.timeZone = TimeZone.getTimeZone("UTC")
        val date = parser.parse(cleanDate)

        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", currentLocale)
        date?.let { formatter.format(it) } ?: isoDate
    } catch (e: Exception) {
        isoDate
    }
}