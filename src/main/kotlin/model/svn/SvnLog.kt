package model.svn

import java.util.*

data class SvnLog(
    val revision: Long,
    val author: String,
    val date: Date,
    val message: String
)
