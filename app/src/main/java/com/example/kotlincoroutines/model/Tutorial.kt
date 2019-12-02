package com.example.kotlincoroutines.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by FastShift, Inc., on 12/2/2019.
 *
 * @author Sargis Khlopuzyan (sargis.khlopuzyan@fcc.am)
 */

@Parcelize
data class Tutorial(
    val name: String,
    val url: String,
    val description: String
) : Parcelable