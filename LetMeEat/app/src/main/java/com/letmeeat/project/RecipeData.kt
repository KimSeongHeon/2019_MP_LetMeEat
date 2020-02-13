package com.letmeeat.project

import java.io.Serializable

data class RecipeData(var rimg:String, var rname:String, var rsumry:String,
                      var rnation:String,var rtime:String,var rcal:String,var rlevel:String,
                      var recipe_num:Int,var rmain:ArrayList<ingrtdata>,var rsub:ArrayList<ingrtdata>,
                      var rlackmain:ArrayList<ingrtdata>,var rlacksub:ArrayList<ingrtdata>,
                      var rStar:Boolean,var likeCount:Int=0, var rcook:String) : Serializable