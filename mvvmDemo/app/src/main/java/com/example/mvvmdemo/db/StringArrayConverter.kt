
package com.example.mvvmdemo.db

import androidx.room.TypeConverter
import java.lang.StringBuilder


class StringArrayConverter {
    @TypeConverter
    fun stringToStringArray(s:String):Array<String>{
        return if (s.isEmpty()) emptyArray() else s.split(";").toTypedArray()
    }

    @TypeConverter
    fun stringArrayToString(array: Array<String>):String{
        val sb=StringBuilder()
        array.forEach {
            sb.append(it).append(";")
        }
        if(sb.endsWith(";")){
            sb.deleteCharAt(sb.length-1);
        }
        return sb.toString()
    }
}