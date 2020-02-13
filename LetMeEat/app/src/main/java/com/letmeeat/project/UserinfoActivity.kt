package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.letmeeat.project.MainActivity.Companion.item_list
import kotlinx.android.synthetic.main.activity_userinfo.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class UserinfoActivity : AppCompatActivity() {
    lateinit var category_adapter:ArrayAdapter<String>
    lateinit var item_adapter:ArrayAdapter<String>
    var no_CATEGORY1 : ArrayList<Int> = arrayListOf() // 먹을 수 없는 음식 종류
    var no_INDRT_SET : ArrayList<String> = arrayListOf() // 먹을 수 없는 음식 이름
    var category_list = ArrayList<String>() //두번쨰 카테고리

    var disgust_list = ArrayList<ingrtdata>() //못먹는 음식 리스트
    var  INDRT_array = ArrayList<ingrtdata>()
    var button_map = HashMap<Int,ingrtdata>()
    var index = 0;
    var vegOk = false
    var milOk = false
    var eggOk = false
    var fishOk = false
    var chicOk = false
    var meatOk = false
    var guitar0k = false
    var source0k = false
    var meal0k = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userinfo)
        init()
    }
    fun init() {
        val intent = getIntent()
        val username = intent.getStringExtra("username")
        INDRT_array = intent.getSerializableExtra("INDRT_SET") as ArrayList<ingrtdata>
        Log.v("aasfasf",INDRT_array.size.toString())
        disgust_list = intent.getSerializableExtra("DISGUST_FOOD") as ArrayList<ingrtdata>
        createbutton()
        tname.text = username+" 님의 정보 등록"
        category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
        category_listview.adapter = category_adapter
        initlistener()
    }
    fun createbutton(){
        var grid = findViewById<GridLayout>(R.id.disgust_grid)
        for(i in 0..disgust_list.size-1){
            val button = Button(this) //버튼 생성
            button.id = ++index
            Log.v("button id",button.id.toString())
            button_map.put(button.id, disgust_list[i]) //버튼ID -> 이름 category1 category2 맵
            grid.addView(button)
            button.setText(find_name(disgust_list[i]))
            button.setBackgroundResource(R.drawable.button_corner)
            button.setOnClickListener {
                grid.removeView(button)
                disgust_list.remove(
                    ingrtdata(
                        button_map[it.id]!!.name,
                        button_map[it.id]!!.category1,
                        button_map[it.id]!!.category2,
                        false,
                        false
                    )
                )
                Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun find_name(ingrtdata: ingrtdata):String{
        var str = ""
        if(ingrtdata.name == "전체"){
            if(ingrtdata.category2 == "0"){
                when (ingrtdata.category1.toInt()) {
                    1 -> str = "채소과일"; 2 -> str = "유제품"; 3 -> str = "계란"
                    4 -> str = "수산물"; 5 -> str = "닭고기/조류"
                    6 -> str = "소고기/돼지고기";7 -> str = "기타";8 -> str = "양념/소스";9 -> str = "곡물/견과/가루류"
                }
                str = str + " 전체"
            }
            else{
                when (ingrtdata.category1.toInt()) {
                    1 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.vegetable)))[ingrtdata.category2.toInt()]
                    2 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.milk)))[ingrtdata.category2.toInt()]
                    3 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.egg)))[ingrtdata.category2.toInt()]
                    4 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.fish)))[ingrtdata.category2.toInt()]
                    5 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.chicken)))[ingrtdata.category2.toInt()]
                    6 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meat)))[ingrtdata.category2.toInt()]
                    7 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.guitar)))[ingrtdata.category2.toInt()]
                    8 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.source)))[ingrtdata.category2.toInt()]
                    9 -> str =
                        ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meal)))[ingrtdata.category2.toInt()]
                }
            }
        }
        else{
            str = ingrtdata.name
        }
        return str
    }

    fun lightoff(){
        item_list.clear()
        item_adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, item_list)
        item_listview.adapter = item_adapter
        var veg = findViewById<ImageButton>(R.id.veggie)
        var milk = findViewById<ImageButton>(R.id.milk)
        var egg = findViewById<ImageButton>(R.id.eggs)
        var fish = findViewById<ImageButton>(R.id.fish)
        var chicken = findViewById<ImageButton>(R.id.chicken)
        var meat = findViewById<ImageButton>(R.id.meat)
        var guitar = findViewById<ImageButton>(R.id.guitar)
        var source = findViewById<ImageButton>(R.id.source)
        var meal = findViewById<ImageButton>(R.id.meal)
        veg.setImageResource(R.drawable.broccoli2)
        milk.setImageResource(R.drawable.milk2)
        egg.setImageResource(R.drawable.egg2)
        fish.setImageResource(R.drawable.fish2)
        chicken.setImageResource(R.drawable.turkey2)
        meat.setImageResource(R.drawable.steak2)
        guitar.setImageResource(R.drawable.guitar2)
        source.setImageResource(R.drawable.spices2)
        meal.setImageResource(R.drawable.grain2)
        vegOk = false ; milOk = false ; eggOk = false ;fishOk = false ;chicOk = false; meatOk = false;guitar0k=false;source0k =false; meal0k=false
    }
    fun lighton(str:String){
        when(str){
            "veggie"->{
                var veg = findViewById<ImageButton>(R.id.veggie)
                veg.setImageResource(R.drawable.broccoli)
                vegOk = true
            }
            "milk"->{
                var veg = findViewById<ImageButton>(R.id.milk)
                veg.setImageResource(R.drawable.milk)
                milOk = true
            }
            "egg"->{
                var veg = findViewById<ImageButton>(R.id.eggs)
                veg.setImageResource(R.drawable.egg)
                eggOk = true
            }
            "fish"->{
                var veg = findViewById<ImageButton>(R.id.fish)
                veg.setImageResource(R.drawable.fish)
                fishOk = true
            }
            "chicken"->{
                var veg = findViewById<ImageButton>(R.id.chicken)
                veg.setImageResource(R.drawable.turkey)
                chicOk = true
            }
            "meat"->{
                var veg = findViewById<ImageButton>(R.id.meat)
                veg.setImageResource(R.drawable.steak)
                meatOk = true
            }
            "guitar"->{
                var veg = findViewById<ImageButton>(R.id.guitar)
                veg.setImageResource(R.drawable.guitar)
                guitar0k = true
            }
            "source"->{
                var veg = findViewById<ImageButton>(R.id.source)
                veg.setImageResource(R.drawable.spices)
                source0k = true
            }
            "meal"->{
                var veg = findViewById<ImageButton>(R.id.meal)
                veg.setImageResource(R.drawable.grain)
                meal0k = true
            }

        }
    }
    fun onBtnClick(v : View) {
        when(v.id) {
            R.id.veggie-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.vegetable)))
                Log.v("category",category_list.size.toString())
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("veggie")
                Toast.makeText(this, "채소/과일", Toast.LENGTH_SHORT).show()
            }
            R.id.milk-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.milk)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("milk")
                Toast.makeText(this, "유제품", Toast.LENGTH_SHORT).show()
            }
            R.id.eggs-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.egg)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("egg")
                Toast.makeText(this, "달걀", Toast.LENGTH_SHORT).show()
            }
            R.id.fish-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.fish)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("fish")
                Toast.makeText(this, "수산물", Toast.LENGTH_SHORT).show()
            }
            R.id.chicken-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.chicken)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("chicken")
                Toast.makeText(this, "닭고기/조류", Toast.LENGTH_SHORT).show()
            }
            R.id.meat-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meat)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("meat")
                Toast.makeText(this, "소고기/돼지고기/양고기", Toast.LENGTH_SHORT).show()
            }
            R.id.guitar-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.guitar)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("guitar")
                Toast.makeText(this, "기타", Toast.LENGTH_SHORT).show()
            }
            R.id.source-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.source)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("source")
                Toast.makeText(this, "양념/소스", Toast.LENGTH_SHORT).show()
            }
            R.id.meal-> {
                lightoff()
                category_list = ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meal)))
                category_adapter = ArrayAdapter(this,android.R.layout.simple_dropdown_item_1line,category_list)
                category_listview.adapter = category_adapter
                lighton("meal")
                Toast.makeText(this, "곡물/견과류", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun initlistener(){
        var grid = findViewById<GridLayout>(R.id.disgust_grid)
        save_button.setOnClickListener {
            // 정보 저장
            //if(!vegOk) no_CATEGORY1.add(1)
            //if(!milOk) no_CATEGORY1.add(2)
           // if(!eggOk) no_CATEGORY1.add(3)
            //if(!fishOk) no_CATEGORY1.add(4)
            //if(!chicOk) no_CATEGORY1.add(5)
            //if(!meatOk) no_CATEGORY1.add(6)
            // category1에서 먹을 수 없는 음식 종류 추가
            mSuccess()
        }
        //////////////////
        // 클릭리스너 시작//
        ////////////////////
        category_listview.setOnItemClickListener { parent, view, position, id ->
            item_list.clear()
            item_adapter.notifyDataSetChanged()
            val category2 = position
            var category1: Int = 0
            if (vegOk) category1 = 1; if (milOk) category1 = 2; if (eggOk) category1 = 3
            if (fishOk) category1 = 4; if (chicOk) category1 = 5; if (meatOk) category1 = 6
            if (guitar0k) category1 = 7; if (source0k) category1 = 8; if (meal0k) category1 = 9
            //////////////////////
            //'전체'가 아닐때/////
            /////////////////////////
            if (position != 0) {
                var ingrtdata_list = INDRT_array.filter {
                    it.category1 == category1.toString() && it.category2 == category2.toString()
                }
                item_list.add("전체")
                for (i in 0..ingrtdata_list.size - 1) {
                    Log.v("ingrtdata.size",ingrtdata_list[i].name)
                    item_list.add(ingrtdata_list[i].name)
                }
                item_adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, item_list)
                item_listview.adapter = item_adapter
            }
            //////////////////////
            //'전체 일때'/////////
            /////////////////////
            else {
                item_list.clear()
                item_adapter.notifyDataSetChanged()
                var str = ""
                if(!disgust_list.contains(ingrtdata("전체",category1.toString(),category2.toString(),false,false))){ //disgust_list에 없을때만 새로생성
                    disgust_list = ArrayList(disgust_list.filter {
                        it.category1 !=category1.toString()
                    })
                    val remove_id_set = HashMap(button_map.filterValues {
                        it.category1 == category1.toString()
                    }).keys
                    var it = remove_id_set.iterator()
                    while(it.hasNext()){
                        val num = it.next()
                        grid.removeView(findViewById(num))
                        button_map.remove(num)
                        Log.v("remove","delete"+num+"")

                    }

                    //삭제. 겹치는 부분 삭제
                    // 추가하는 부분
                    disgust_list.add(ingrtdata("전체", category1.toString(), category2.toString(), false, false))
                    val button = Button(this) //버튼 생성
                    button.id = ++index
                    Log.v("button id",button.id.toString())
                    button_map.put(button.id, ingrtdata("전체",category1.toString(),category2.toString(),false,false)) //버튼ID -> 이름 category1 category2 맵
                    grid.addView(button)
                    when (category1) {
                        1 -> str = "채소과일"; 2 -> str = "유제품"; 3 -> str = "계란"
                        4 -> str = "수산물"; 5 -> str = "닭고기/조류"
                        6 -> str = "소고기/돼지고기";7 -> str = "기타";8 -> str = "양념/소스";9 -> str = "곡물/견과/가루류"
                    }
                    button.setText(str + " 전체")
                    button.setBackgroundResource(R.drawable.button_corner)
                    button.setOnClickListener {
                        grid.removeView(button)
                        disgust_list.remove(ingrtdata("전체",button_map[it.id]!!.category1,button_map[it.id]!!.category2,false,false))
                        Toast.makeText(this,"삭제되었습니다",Toast.LENGTH_SHORT).show()
                    }

                }
            }
            //////////////////////////
            //item들을 골랐을때
            //////////////////////////////
            item_listview.setOnItemClickListener { parent, view, position, id ->
                val item = position
                val category2= category2
                val category1 = category1
                ////////////////////////////////////////////
                //'전체'를 고른것이 아니라 item을 골랐을때//
                /////////////////////////////////////////////

                if(position != 0){//'전체'가 아닐때
                    var ingrtdata_list = INDRT_array.filter {
                        it.category1 == category1.toString() && it.category2 == category2.toString()
                    }
                    if(!disgust_list.contains(ingrtdata(ingrtdata_list[position - 1].name,category1.toString(),category2.toString(),false,false))) { //disgust_list에 없을때만 새로생성

                        val remove_id_set = HashMap(button_map.filterValues {
                            it.category1 == category1.toString() && it.category2 == category2.toString() && it.name == "전체"
                        }).keys
                        val remove_id_set2 = HashMap(button_map.filterValues {
                            it.category1 == category1.toString() && it.category2 == "0" && it.name == "전체"
                        }).keys
                        disgust_list = ArrayList(disgust_list.filterNot {
                            it.category1 == category1.toString() && it.category2 == category2.toString() && it.name == "전체"
                        })
                        if (disgust_list.contains(ingrtdata("전체", category1.toString(), "0", false, false))) {
                            disgust_list.remove(ingrtdata("전체", category1.toString(), "0", false, false))
                        }
                        var it = remove_id_set.iterator();
                        var it2 = remove_id_set2.iterator()
                        while (it.hasNext()) {
                            val num = it.next()
                            grid.removeView(findViewById(num))
                            button_map.remove(num)
                        }
                        while (it2.hasNext()) {
                            val num = it2.next()
                            grid.removeView(findViewById(num))
                            button_map.remove(num)
                        }
                        //제거 부분 complete
                        // 추가 start

                        disgust_list.add(
                            ingrtdata(
                                ingrtdata_list[position - 1].name,
                                category1.toString(),
                                category2.toString(),
                                false,
                                false
                            )
                        )
                        val button = Button(this)
                        button.id = ++index
                        button_map.put(
                            button.id,
                            ingrtdata(
                                ingrtdata_list[position - 1].name,
                                category1.toString(),
                                category2.toString(),
                                false,
                                false
                            )
                        ) //버튼ID -> 이름 category1 category2 맵
                        grid.addView(button)
                        var str = ingrtdata_list[position - 1].name
                        button.setText(str)
                        button.setBackgroundResource(R.drawable.button_corner)
                        button.setOnClickListener {
                            grid.removeView(button)
                            disgust_list.remove(
                                ingrtdata(
                                    button_map[it.id]!!.name,
                                    button_map[it.id]!!.category1,
                                    button_map[it.id]!!.category2,
                                    false,
                                    false
                                )
                            )
                            Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
                    //////////////////////////////////////////////////
                //'전체'를  골랐을떄
                ///////////////////////////////////////////////////////////
                else{
                    if(!disgust_list.contains(ingrtdata("전체",category1.toString(),category2.toString(),false,false))) { //disgust_list에 없을때만 새로생성
                        var str = ""
                        val remove_id_set = HashMap(button_map.filterValues {
                            it.category1 == category1.toString() && it.category2 == category2.toString() && it.name != "전체"
                        }).keys
                        disgust_list = ArrayList(disgust_list.filterNot {
                            it.category1 == category1.toString() && it.category2 == category2.toString() && it.name != "전체"
                        })
                        val remove_id_set2 = HashMap(button_map.filterValues {
                            it.category1 == category1.toString() && it.category2 == "0" && it.name == "전체"
                        }).keys
                        if (disgust_list.contains(ingrtdata("전체", category1.toString(), "0", false, false))) {
                            disgust_list.remove(ingrtdata("전체", category1.toString(), "0", false, false))
                        }
                        var it = remove_id_set.iterator();
                        var it2 = remove_id_set2.iterator()
                        while (it.hasNext()) {
                            val num = it.next()
                            grid.removeView(findViewById(num))
                            button_map.remove(num)
                        }
                        while (it2.hasNext()) {
                            val num = it2.next()
                            grid.removeView(findViewById(num))
                            button_map.remove(num)
                        }
                        //삭제 complete
                        ////////////////////////////////
                        //추가 start
                        if (!disgust_list.contains(
                                ingrtdata(
                                    "전체",
                                    category1.toString(),
                                    category2.toString(),
                                    false,
                                    false
                                )
                            )
                        ) { //disgust_list에 없을때
                            disgust_list.add(ingrtdata("전체", category1.toString(), category2.toString(), false, false))
                            val button = Button(this)
                            button.id = ++index
                            button_map.put(
                                button.id,
                                ingrtdata("전체", category1.toString(), category2.toString(), false, false)
                            ) //버튼ID -> 이름 category1 category2 맵
                            grid.addView(button)
                            var str = ""
                            when (category1) {
                                1 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.vegetable)))[category2]
                                2 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.milk)))[category2]
                                3 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.egg)))[category2]
                                4 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.fish)))[category2]
                                5 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.chicken)))[category2]
                                6 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meat)))[category2]
                                7 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.guitar)))[category2]
                                8 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.source)))[category2]
                                9 -> str =
                                    ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.meal)))[category2]
                            }
                            button.setText(str + " 전체")
                            button.setBackgroundResource(R.drawable.button_corner)
                            button.setOnClickListener {
                                grid.removeView(button)
                                disgust_list.remove(
                                    ingrtdata(
                                        "전체",
                                        button_map[it.id]!!.category1,
                                        button_map[it.id]!!.category2,
                                        false,
                                        false
                                    )
                                )
                                Toast.makeText(this, "삭제되었습니다", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
    fun mSuccess(){
        val intent = Intent()
        intent.putExtra("result", "Close Popup")
        intent.putExtra("no_CATEGORY1", no_CATEGORY1)
        intent.putExtra("no_INDRT_SET", no_INDRT_SET)
        intent.putExtra("disgust_food",disgust_list)
//        intent.putExtra("basket",autoCompleteTextViewkind.text.toString())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
