package dz.mydz



fun getValues(data: Array<Float>) : Array<Float> {
    val maxIdx = data.indexOf(data.maxOrNull())

    var data2 = emptyArray<Int>()
    var data3 = emptyArray<Float>()

    for (i in data.indices) {
        if (i == maxIdx) {
            data2 += 100
        } else {
            data2 += calcPercents(data[i], data[maxIdx])
        }
    }

    for (i in data2.indices) {
        data3 += calcProportion(data2[i])
    }
    return  data3
}


fun calcPercents(inn : Float, max : Float): Int {
    val a  = inn / max * 100
    return a.toInt()
}

fun calcProportion(p : Int): Float {
    val a  = p *  25 * 0.01
    return (a * 0.01).toFloat()
}