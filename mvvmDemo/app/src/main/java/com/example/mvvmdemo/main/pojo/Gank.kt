
package com.example.mvvmdemo.main.pojo

import androidx.room.*
import com.example.mvvmdemo.db.StringArrayConverter


@Entity
@TypeConverters(StringArrayConverter::class)
data class Gank(
    @PrimaryKey(autoGenerate = true)
    val id:Int=0,
    val typeId:Int=FULI,
    val gankId: String = "0",
    val createdAt: String = "",
    val desc: String = "",
    val images: Array<String> = emptyArray(),
    val publishedAt: String = "",
    val source: String = "",
    val type: String = "",
    val url: String = "",
    val used: Boolean = false,
    val who: String = ""
) {
    @Ignore
    var viewed = false

    /**
     * 返回对应的图
     */
    fun getImageUrl(index: Int): String {
        return if (images.size > index) images[index] else ""
    }

    companion object {
        const val FULI      = 0
        const val ANDROID   = 1000
        const val FRONT_END = 2000
        const val VIDEO     = 3000

        const val GROUP_FIRST     = -1
        const val GROUP_FULI      = -1
        const val GROUP_ANDROID   = -2
        const val GROUP_FRONT_END = -3
        const val GROUP_VIDEO     = -4
        const val GROUP_LAST      = -4
    }


}