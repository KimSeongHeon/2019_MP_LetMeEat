package com.letmeeat.project

import java.io.Serializable

data class productData(var pNum : Int, var pImg : String, var pName : String, var pKind : String, var pEra : String,
                       var pMonth : ArrayList<Int>, var pEffect : String, var ppick_Mth : String,
                       var pstore_Mth : String, var pcook_Mth : String): Serializable