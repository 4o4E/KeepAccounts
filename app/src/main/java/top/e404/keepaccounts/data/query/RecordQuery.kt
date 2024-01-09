package top.e404.keepaccounts.data.query

data class RecordQuery(
    var timeStart: Long? = null,
    var timeEnd: Long? = null,
    var valueStart: Long? = null,
    var valueEnd: Long? = null,
    var desc: String? = null,
    var order: RecordOrder = RecordOrder.TIME,
    var asc: Boolean = false,
) {
    enum class RecordOrder(val value: String) {
        VALUE("value"),
        TIME("time")
    }

    fun edit(
        timeStart: Long? = this.timeStart,
        timeEnd: Long? = this.timeEnd,
        valueStart: Long? = this.valueStart,
        valueEnd: Long? = this.valueEnd,
        desc: String? = this.desc,
        order: RecordOrder = this.order,
        asc: Boolean = this.asc,
    ) = RecordQuery(timeStart, timeEnd, valueStart, valueEnd, desc, order, asc)
}