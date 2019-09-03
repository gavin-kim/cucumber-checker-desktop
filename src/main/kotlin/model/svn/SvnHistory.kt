package model.svn

data class SvnHistory(
    val url: String,
    val baseRevision: Long,
    val headRevision: Long,
    val logs: List<SvnLog>
)