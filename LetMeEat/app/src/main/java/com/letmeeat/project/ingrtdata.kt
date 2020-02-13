package com.letmeeat.project

import java.io.Serializable

data class ingrtdata(var name:String="",var category1:String="",var category2:String=""
                     , var isProduct : Boolean=false, var isLack  : Boolean=false, var isDisgust:Boolean=false) : Serializable
