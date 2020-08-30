
package com.example.mvvmdemo.struct


annotation class ConvertFactory (vararg val value:DataType){
    enum class DataType{
        String, Gson, ProtoBuf
    }
}