package id.neotica.neotica.utils

import kotlin.js.Date

fun Date.toEpoch(): Long {
    return Date.UTC(this.getFullYear(), this.getMonth(), this.getDate()) .toLong()
//    this.getTime() .toLong()
}

fun Long.toDate(): Date {
    return Date(this )
}