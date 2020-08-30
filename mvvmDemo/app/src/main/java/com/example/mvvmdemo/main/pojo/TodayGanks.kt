
package com.example.mvvmdemo.main.pojo


data class TodayGanks(
    val fuli: List<Gank> = emptyList(), // 福利
    val android: List<Gank> = emptyList(), // Android
    val frondEnd: List<Gank> = emptyList(), // 前端
    val video:List<Gank> = emptyList() // 视频
) {

    fun size() : Int {
        return fuli.size + android.size + frondEnd.size + video.size
    }

    fun isEmpty() : Boolean {
        return fuli.isEmpty() && android.isEmpty() && frondEnd.isEmpty() && video.isEmpty()
    }

    fun toList() : List<Gank> {
        return mutableListOf<Gank>().apply {
            add(GANK_GROUP_FULI)
            addAll(fuli)
            add(GANK_GROUP_ANDROID)
            addAll(android)
            add(GANK_GROUP_FRONT_END)
            addAll(frondEnd)
            add(GANK_GROUP_VIDEO)
            addAll(video)
        }
    }

    // 根据id查找
    // 这里只是演示用, 不要吐槽性能
    fun findGankById(gankId: String): Gank? {
        return fuli.find { it.gankId == gankId }
            ?: android.find { it.gankId == gankId }
            ?: frondEnd.find { it.gankId == gankId }
            ?: video.find { it.gankId == gankId }
    }

    companion object {
        const val GROUP_ID_FULI      = -1L
        const val GROUP_ID_ANDROID   = -2L
        const val GROUP_ID_FRONT_END = -3L
        const val GROUP_ID_VIDEO     = -4L

        // 内置分组
        val GANK_GROUP_FULI      =
            Gank(typeId = Gank.GROUP_FULI/*, gankIdLong = GROUP_ID_FULI*/)
        val GANK_GROUP_ANDROID   =
            Gank(typeId = Gank.GROUP_ANDROID/*, gankIdLong = GROUP_ID_ANDROID*/)
        val GANK_GROUP_FRONT_END =
            Gank(typeId = Gank.GROUP_FRONT_END/*, gankIdLong = GROUP_ID_FRONT_END*/)
        val GANK_GROUP_VIDEO     =
            Gank(typeId = Gank.GROUP_VIDEO/*, gankIdLong = GROUP_ID_VIDEO*/)
    }
}