package com.letmeeat.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Build
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.google.gson.JsonParser
import com.kakao.auth.Session
import com.kakao.usermgmt.UserManagement
import com.kakao.usermgmt.callback.LogoutResponseCallback
import com.koushikdutta.ion.Ion
import com.letmeeat.fridgeproj.MyShopOffAdapter
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.LocationOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.shoppingtab.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.collections.ArrayList
import kotlin.math.max


class MainActivity : AppCompatActivity(), TabHost.OnTabChangeListener,OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener{

    //place 데이터 변수
    val OverlayImage = MarkerIcons.PINK
    var x_room: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()//위도 모음
    var y_room: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()//경도 모음
    var name_room: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    var phone_room: CopyOnWriteArrayList<String> = CopyOnWriteArrayList() //전화번호 모음
    var distance_room: CopyOnWriteArrayList<String> = CopyOnWriteArrayList()
    private lateinit var locationSource: FusedLocationSource
    lateinit var loctionOverlay: LocationOverlay
    var longtitude: String = ""
    var latitude: String = ""

    /////////////데이터 변수

    var onFridge = false // 초기값 : 전체 추천 요리
    var firebase_rdata: ArrayList<String> = ArrayList()
    var recommend_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
    var search_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()

    // 냉장고 재료 + 필터링 된 recipe
    var cuisine_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
    var which_rdata : CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
    var recommend_all_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
    // 일반 필터링 된 recipe
    var show_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()

    lateinit var sort_radapter: RecipeAdapter
    lateinit var radapter: RecommendRecipeAdapter
    lateinit var fadapter: foodAdapter

    var search_pdata: CopyOnWriteArrayList<productData> = CopyOnWriteArrayList()
    lateinit var sort_padapter: productAdapter

    var date = Date(System.currentTimeMillis())
    var current_month = SimpleDateFormat("MM").format(date).toInt()
    var all_product : CopyOnWriteArrayList<productData> = CopyOnWriteArrayList()
    var season_product : CopyOnWriteArrayList<productData> = CopyOnWriteArrayList()// 제철 재료 저장

    ////////////파이어베이스 변수
    lateinit var mRootRef: DatabaseReference //파이어베이스 루트 경로
    lateinit var conditionRef1: DatabaseReference //냉장고 접근
    lateinit var conditionRef4:DatabaseReference// 못먹는 재료 접근
    lateinit var mContext: Context //현재 activity 콘텍스트 정보

    var isMinus = true  //쓰레기통의 체크 유무 판별 (boolean)


    //유저 아이디
    lateinit var user_id:String
    var nickname = ""
    var imageURL = ""
    var email = ""

    //탭 초기화 관련 변수
    lateinit var tabHost1 : TabHost
    lateinit var tabHost2 : TabHost

    //파싱 완료 판별 변수
    var isParsingComplete:Boolean = false

    val thread_recipe = Thread(Runnable {
        getxmlData_Recipe()
        initstarquery()
    }) //요리 파싱 쓰레드

    val thread_product = Thread(Runnable {
        getJsonData_Product()
    })

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        var slotNum_Bas = 0 //장바구니 슬롯 넘버()
        var slotNum_Ref = 0 //냉장고 슬롯 넘버
        var item_list = ArrayList<String>() //item들
        var rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
        var m_fdata: ArrayList<foodData> = ArrayList()
        var dueCountIndex: ArrayList<Int> = ArrayList() //index가 들어갈꺼임.
        var ingredient: ArrayList<foodData> = ArrayList()   //장바구니에 들어간 "재료"들
        var likeCountData : ArrayList<Pair<Int,Int>> = ArrayList()//레시피 당 좋아요 갯수
        var myFavoriteData:CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
        lateinit var shopOffadapter: MyShopOffAdapter
        lateinit var conditionRef2: DatabaseReference //추천요리 접근
        lateinit var conditionRef3: DatabaseReference //좋아요 요리 인덱스 접근
        lateinit var conditionRef5: DatabaseReference //좋아요 갯수 접근
        var ingrtArray = ArrayList<ingrtdata>()
        var ingredient_set = HashMap<String,Pair<String,String>>()
        var pdata : CopyOnWriteArrayList<productData> = CopyOnWriteArrayList()
        var disgust_food_list = ArrayList<ingrtdata>()
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        //Toast.makeText(this,"NavigationItemSelected confirmed",Toast.LENGTH_SHORT).show()
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when(p0!!.itemId){ // 네비게이션 메뉴가 클릭되면 스낵바가 나타난다.
            R.id.account->
            {
                startMyPopupActivity8(nickname)
            }
            R.id.star->
            {
                if(isParsingComplete)
                    startMyPopupActivity10()
                else
                    Toast.makeText(this, "로딩중 .잠시만 기다려주세요",Toast.LENGTH_SHORT).show()
            }
            R.id.log_out->
            {
                UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                    override fun onCompleteLogout() {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        intent.putExtra("logout", "logout")
                        startActivity(intent)
                        Session.getCurrentSession().close()
                        Session.getCurrentSession().clearCallbacks()
                        Log.v("opensession", "logout")
                        finish()
                    }
                })
            }
        }
        main_drawerLayout.closeDrawers() // 기능을 수행하고 네비게이션을 닫아준다.
        return false

    }

    override fun onMapReady(naverMap: NaverMap) {
        var min = 99999999.1
        var min2 = 99999999.1
        var short_count = 0
        var short_count2 = 0


        val infoWindow = InfoWindow()
        naverMap.setOnMapClickListener {
                coord, point -> infoWindow.close()
        }

        val marker = Marker()
        val marker2 = Marker()

        marker.setOnClickListener {
            // 마커를 클릭할 때 정보창을 엶
            infoWindow.open(marker)
            true
        }

        marker2.setOnClickListener {
            // 마커를 클릭할 때 정보창을 엶
            Log.v("checkclick" ,"Marker눌림")

            infoWindow.open(marker2)
            true
        }


        val uiSettings = naverMap.uiSettings
        uiSettings.isCompassEnabled = false
        uiSettings.isLocationButtonEnabled = true
        loctionOverlay = naverMap.locationOverlay
        longtitude = String.format("%.6f",loctionOverlay.position.longitude)
        latitude = String.format("%.6f",loctionOverlay.position.latitude)
        Log.v("safas",longtitude+latitude)
        Log.v("locationOVerlay", loctionOverlay.position.latitude.toString() + loctionOverlay.position.longitude.toString())
        val temp_thread = Thread(object:Runnable{
            override fun run() {
                place_parsing()}
        })
        temp_thread.start()
        temp_thread.join()
        //파싱 된 다음에 수행되어야 함.

        for(i in 0 until distance_room.size){
            if(min > distance_room[i].toDouble()){
                //거리가 더 작다면, min으로 대체
                min = distance_room[i].toDouble()
                short_count = i;

            }
        }
        for(i in 0 until distance_room.size){
            if(min2 > distance_room[i].toDouble() && i != short_count){
                //거리가 더 작다면, min으로 대체
                min2 = distance_room[i].toDouble()
                short_count2 = i;
            }
        }

        Log.v("min1",min.toString())
        Log.v("min2",min2.toString())

        //Log.v("x_room", x_room[0])
        marker.position = LatLng(y_room[short_count].toDouble(),x_room[short_count].toDouble())
        marker.icon = OverlayImage
        marker.map = naverMap
        marker.captionText = name_room[short_count]
        marker.tag = phone_room[short_count]


        marker2.position = LatLng(y_room[short_count2].toDouble(),x_room[short_count2].toDouble())
        marker2.icon = OverlayImage
        marker2.map = naverMap
        marker2.captionText = name_room[short_count2]
        Log.v("tagCheck" ,phone_room[short_count2])
        marker2.tag = phone_room[short_count2]

        naverMap.locationSource = locationSource

        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
            override fun getText(infoWindow: InfoWindow): CharSequence {
                return infoWindow.marker?.tag as CharSequence? ?: ""
            }
        }

        naverMap.addOnLocationChangeListener {

            //val infoWindow = InfoWindow()
            naverMap.setOnMapClickListener {
                    coord, point -> infoWindow.close()
            }

            val marker = Marker()
            val marker2 = Marker()

            marker.setOnClickListener {
                // 마커를 클릭할 때 정보창을 엶
                if (marker.infoWindow == null) {
                    infoWindow.open(marker)
                } else {
                    infoWindow.close()
                }
                true
            }

            marker2.setOnClickListener {
                // 마커를 클릭할 때 정보창을 엶
                if (marker2.infoWindow == null) {
                    infoWindow.open(marker2)
                } else {
                    infoWindow.close()
                }
                true
            }
            x_room.clear()
            y_room.clear()
            name_room.clear()
            phone_room.clear()
            distance_room.clear()

            loctionOverlay = naverMap.locationOverlay
            longtitude = String.format("%.6f",loctionOverlay.position.longitude)
            latitude = String.format("%.6f",loctionOverlay.position.latitude)
            Log.v("safas",longtitude+latitude)
            Log.v("locationOVerlay", loctionOverlay.position.latitude.toString() + loctionOverlay.position.longitude.toString())
            val temp_thread = Thread(object:Runnable{
                override fun run() {
                    place_parsing()}
            })
            temp_thread.start()
            temp_thread.join()
            //위치 변경에 대한 리스너, 여기서 파싱이 일어나야함.
            for(i in 0 until distance_room.size){
                if(min > distance_room[i].toDouble()){
                    //거리가 더 작다면, min으로 대체
                    min = distance_room[i].toDouble()
                    short_count = i;

                }
            }
            for(i in 0 until distance_room.size){
                if(min2 > distance_room[i].toDouble() && i != short_count){
                    //거리가 더 작다면, min으로 대체
                    min2 = distance_room[i].toDouble()
                    short_count2 = i;
                }
            }

            Log.v("min1",min.toString())
            Log.v("min2",min2.toString())

            if(x_room.isNotEmpty() && y_room.isNotEmpty()){
                Log.v("x_room", x_room[short_count])
                marker.position = LatLng(y_room[short_count].toDouble(),x_room[short_count].toDouble())
                marker.icon = OverlayImage
                marker.map = naverMap
                marker.captionText = name_room[short_count]
                marker.tag = phone_room[short_count]


                marker2.position = LatLng(y_room[short_count2].toDouble(),x_room[short_count2].toDouble())
                marker2.icon = OverlayImage
                marker2.map = naverMap
                marker2.captionText = name_room[short_count2]
                marker2.tag = phone_room[short_count2]
            }



            naverMap.locationSource = locationSource

            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(this) {
                override fun getText(infoWindow: InfoWindow): CharSequence {
                    return infoWindow.marker?.tag as CharSequence? ?: ""
                }
            }

        }

    }
    fun place_parsing(){
        var response = ""
        var answer=""
        var id="i90f8q7ubl"
        var secret = "5dtdTCAzmnlHEpNzkNcTOq7ASMYWIEKVgAJV6DSt"
        Log.v("id",id)
        try {
            val text = URLEncoder.encode("대형마트", "UTF-8")

            Log.v("long format",text)

            Log.v("long format","ㄻㄴㄹㄴㅁ")
            val apiURL ="https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query="+text+"&coordinate="+longtitude+","+latitude// json 결과
            Log.v("좌표",apiURL.toString())
            val url = URL(apiURL)
            val con= url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.setRequestProperty("X-NCP-APIGW-API-KEY-ID",id)
            con.setRequestProperty("X-NCP-APIGW-API-KEY",secret)
            val responseCode = con.responseCode
            val br: BufferedReader
            br = if (responseCode == 200) { // 정상 호출
                BufferedReader(InputStreamReader(con.inputStream))
            } else {  // 에러 발생
                BufferedReader(InputStreamReader(con.errorStream))
            }
            var inputLine: String
            var s = br.readLine()
            Log.v("string",s)
            while (s != null) {
                response+=s
                s= br.readLine()
            }
            var json = JSONObject(response)
            var jarr = json.getJSONArray("places")
            var name="";var x="";var y=""; var phone = ""; var distance = "";
            for(i in 0 until jarr.length()){
                json = jarr.getJSONObject(i)
                name = json.getString("name")
                x = json.getString("x")
                y = json.getString("y")
                phone = json.getString("phone_number")
                distance = json.getString("distance")
                Log.v("phoneNum" , phone)
                Log.v("checkx" , x)
                x_room.add(x)
                y_room.add(y)
                name_room.add(name) //파싱한 정보 저장
                phone_room.add(phone)
                distance_room.add(distance)

                Log.v("name",name+"~"+ x+"~"+y+": "+distance)
                answer += name + "\t" + x+"\t" + y +"\n"
            }
            Log.v("answer",answer)
            br.close()
        } catch (e: Exception) {
            Log.v("exception",e.toString())
            println(e)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        clearAll()
        init()
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
    }
    fun clearAll(){
        m_fdata.clear()
        dueCountIndex.clear()
        rdata.clear()
        ingredient.clear()
        firebase_rdata.clear()
        recommend_rdata.clear()
        search_rdata.clear()
        cuisine_rdata.clear()
        which_rdata.clear()
        recommend_all_rdata.clear()
        show_rdata.clear()
        item_list.clear()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==1){ //재료 추가 할때..
            if(resultCode== Activity.RESULT_OK){
                val popup_food = data!!.getSerializableExtra("Ingredient") as foodData
                m_fdata.add(popup_food)
                Log.v("m_fdata.size.toString",m_fdata.size.toString())
                slotNum_Ref++
                conditionRef1.child(slotNum_Ref.toString()).setValue(popup_food)

                fadapter.notifyDataSetChanged()
                (rdata.filter { it.rlackmain.filter { it.name == popup_food.fkind }.isNotEmpty() }).all {
                    it.rlackmain.remove(ingrtdata(popup_food.fkind!!,ingredient_set.get(popup_food.fkind!!)!!.first,ingredient_set.get(popup_food.fkind!!)!!.second, false, false))
                }
                (rdata.filter { it.rlacksub.filter { it.name == popup_food.fkind }.isNotEmpty() }).all {
                    it.rlacksub.remove(ingrtdata(popup_food.fkind!!,ingredient_set.get(popup_food.fkind!!)!!.first,ingredient_set.get(popup_food.fkind!!)!!.second, false, false))
                }
                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                Log.v("recommend_rdata_2",recommend_rdata.size.toString())

                show_rdata = recommend_rdata
                initadapter()
                Toast.makeText(this,"등록되었습니다.",Toast.LENGTH_SHORT).show()
            }
        }
        else if (requestCode==2){
            if(resultCode== Activity.RESULT_OK){
                Toast.makeText(this,"등록되었습니다.",Toast.LENGTH_SHORT).show()
                val popup_food = data!!.getStringExtra("basket")
                slotNum_Bas++
                ingredient.add(foodData(popup_food,null,slotNum_Bas))

                conditionRef2.child(slotNum_Bas.toString()).setValue(popup_food)

                shopOffadapter.notifyDataSetChanged()
            }
        }
        else if (requestCode==3){
            if(resultCode== Activity.RESULT_OK){
//                val foodList = arrayListOf<foodData>()
                val popup_food = data!!.getSerializableExtra("ingData") as foodData
                if (shopOffadapter.ingList.isNotEmpty()) {

//
                    shopOffadapter.deleteList.clear()
                    shopOffadapter = MyShopOffAdapter(ingredient)
                    shopListOff.adapter = shopOffadapter
//
                    shopOffadapter.ingList.clear()
                }
                else {
                    Toast.makeText(this, "선택한 재료가 없습니다. ", Toast.LENGTH_LONG).show()
                }

                Log.v("m_fdata.size.toString",m_fdata.size.toString())
                slotNum_Ref++
                popup_food.fslotNum = slotNum_Ref
                m_fdata.add(popup_food)
                conditionRef1.child(slotNum_Ref.toString()).setValue(popup_food)

                fadapter.notifyDataSetChanged()
                (rdata.filter { it.rlackmain.filter { it.name == popup_food.fkind }.isNotEmpty() }).all {
                    it.rlackmain.remove(ingrtdata(popup_food.fkind!!,ingredient_set.get(popup_food.fkind!!)!!.first,ingredient_set.get(popup_food.fkind!!)!!.second, false, false))
                }
                (rdata.filter { it.rlacksub.filter { it.name == popup_food.fkind }.isNotEmpty() }).all {
                    it.rlacksub.remove(ingrtdata(popup_food.fkind!!,ingredient_set.get(popup_food.fkind!!)!!.first,ingredient_set.get(popup_food.fkind!!)!!.second, false, false))
                }
                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                show_rdata = recommend_rdata
                initadapter()
                Toast.makeText(this,"등록되었습니다.",Toast.LENGTH_SHORT).show()

            }
        }
        else if (requestCode==4){ // 사용자 정보 등록 완료
            if(resultCode== Activity.RESULT_OK){
                Toast.makeText(this,"등록되었습니다.",Toast.LENGTH_SHORT).show()
                disgust_food_list = data!!.getSerializableExtra("disgust_food") as ArrayList<ingrtdata>
                for(i in 0..disgust_food_list.size-1){
                    Log.v("disgustfood",disgust_food_list[i].name)
                }
                conditionRef4.removeValue()
                for(i in 0..disgust_food_list.size-1)//파이어베이스에 저장.
                {
                    conditionRef4.child(i.toString()).setValue(disgust_food_list[i])
                }



                var popup_no_category1 = data!!.getIntArrayExtra("no_CATEGORY1")
                // 먹을수 없는 음식 종류
                var popup_no_food = data!!.getStringArrayExtra("no_INDRT_SET")
                // 먹을수 없는 음식

//                (rdata.filter { it.rlackmain.filter { it == popup_food.fkind }.isNotEmpty() }).all {
//                    it.rlackmain.remove(popup_food.fkind)
//                }
//                (rdata.filter { it.rlacksub.filter { it == popup_food.fkind }.isNotEmpty() }).all {
//                    it.rlacksub.remove(popup_food.fkind)
//                }

                // 1. recommend_rdata 에서 category1 제외
                // 2. recommend_rdata 에서 no_food 제외


                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })


                recommend_all_rdata = CopyOnWriteArrayList(rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                show_rdata = recommend_rdata
                initadapter()
                Toast.makeText(this,"등록되었습니다.",Toast.LENGTH_SHORT).show()

            }
        }
    }

    fun init(){

        FirebaseApp.initializeApp(this)
        var type = intent.getStringExtra("type")
        if(type.equals("normal")) {
            user_id=intent.getStringExtra("ID")
            nickname = user_id
        }
        else if (type.equals("kakao")) {
            user_id=intent.getStringExtra("id")
            nickname=intent.getStringExtra("nickname")
            email=intent.getStringExtra("email")
            imageURL=intent.getStringExtra("imageURL")
        }
        mRootRef = FirebaseDatabase.getInstance().reference
        conditionRef1 = mRootRef.child(user_id).child("refrigerator")
        conditionRef2 = mRootRef.child(user_id).child("basket")
        conditionRef3 = mRootRef.child(user_id).child("recommend_recipe")
        conditionRef4 = mRootRef.child(user_id).child("disgust_food")
        conditionRef5 = mRootRef.child("likeCount")
        //여기에 FireBase에서 disgust_food 받아오는 구문
        //
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                supportFragmentManager.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)

        who.text = nickname+" 님의 냉장고"

        initadapter()
        initlistener()
        initRefrigeratorData()
        inittab()
        init_RECIPE()
        init_IRDNT()
        init_PRODUCT()
        inituserprofile()
    }
    fun inituserprofile(){
        navigationView.setNavigationItemSelectedListener(this)
        Log.v("1main : id",user_id)
        Log.v("1main : nickname",nickname)
        Log.v("1main : imageURL",imageURL)
        Log.v("1main : email",email)
        val header = findViewById<NavigationView>(R.id.navigationView).getHeaderView(0)
        val nameView = header.findViewById<TextView>(R.id.user_name_textview)
        val emailView = header.findViewById<TextView>(R.id.user_email_textview)
        val profile_imageView = header.findViewById<ImageView>(R.id.user_profile_image)
        nameView.setText(nickname)
        if(email.isNotEmpty())
            emailView.setText(email)
        else
            emailView.setText("")
        profile_imageView.setBackground(ShapeDrawable(OvalShape()))
        profile_imageView.setClipToOutline(true)



        Ion.with(profile_imageView).load(imageURL)


    }
    fun init_notificationDue(){ //유통기한이 지난 물품을 팝업으로 띄워줄 함수.

        var now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //api 제한이 있음.
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        } // now == 2019-06-02 형식임

        var strNow = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"))
        } else {
            TODO("VERSION.SDK_INT < O")
        } //2019/06/02 이런식으로 변경, 0이 저장이 안됨.

        var nowYear = strNow!!.substring(0, 4)
        //Log.d("fdata2", nowYear)
        var nowMonth = strNow!!.substring(5, 7)
        // Log.d("fdata3", nowMonth)
        var nowDay = strNow!!.substring(8)
        //Log.d("fdata3", nowDay)


        var dueCount = 0 //유통기한 지난 개수 체크
        //var dueCountIndex:ArrayList<Int> = ArrayList() //index가 들어갈꺼임.

        //Log.d("fdata", Companion.m_fdata.size.toString())
        if(Companion.m_fdata.size != 0){ //사이즈가 0이 아닐때
            for(i in 0..Companion.m_fdata.size-1) {
                if (!Companion.m_fdata[i].fdue.equals("유통기한 없음")) {
                    var fdataDue = Companion.m_fdata[i].fdue //fdata에 있는 due값 가져옴(yyyy/MM/dd) 형식
                    // Log.d("fdata1", fdataDue)

                    var fdataYear = fdataDue!!.substring(0, 4)
                    // Log.d("fdata2", fdataYear)
                    lateinit var fdataMonth:String
                    lateinit var fdataDay :String

                    if(fdataDue!!.substring(7,8) == "/"){ //즉, 달이 10, 11, 12라면
                        fdataMonth = fdataDue!!.substring(5, 7)
                        fdataDay = fdataDue!!.substring(8)
                    }
                    else{
                        fdataMonth = fdataDue!!.substring(5, 6)
                        //   Log.d("fdata3", fdataMonth)
                        fdataDay = fdataDue!!.substring(7)
                        //  Log.d("fdata4", fdataDay)
                    }


                    if(nowYear.toInt() - fdataYear.toInt() < 0) { //0보다 작으면 더 크다는 뜻이므로 체크할 필요 없음

                    }
                    else if(nowYear.toInt() - fdataYear.toInt() > 0){
                        dueCount++
                        dueCountIndex.add(i) //Index넣어줌.


                    }
                    else { //현재년도가 같을때
                        if(nowMonth.toInt() - fdataMonth.toInt() < 0){

                        }
                        else if(nowMonth.toInt() - fdataMonth.toInt() > 0){
                            dueCount++
                            dueCountIndex.add(i) //Index넣어줌.

                        }
                        else{ //현재월이 같을때
                            if(nowDay.toInt()+1 - fdataDay.toInt() <= 1){

                            }
                            else if(nowDay.toInt()+1 - fdataDay.toInt() > 1){ //유통기한 지난거임
                                dueCount++
                                dueCountIndex.add(i) //Index넣어줌.

                            }
                        }
                    }
                }
            }
        }


        //Log.d("dueCountIndexCheck", dueCountIndex[0].toString())
        // dueCount에 유통기한 지난 개수가 저장.
        // dueCountIndex에 유통기한 지난 물품의 Index가 저장.


        val ft = supportFragmentManager.beginTransaction()
        val newFragment = CustomDialogFragment.newInstance("냉장고의 제품중 유통기한 지난 물품이 " + dueCount.toString() + "개 있습니다.")
        if(dueCount != 0){
            newFragment.show(ft, "dialog")
        }
    }

    fun init_RECIPE(){
        thread_recipe.isDaemon = true
        thread_recipe.start()
    }

    fun init_IRDNT() //Thread를 통해 getXmlDAta를 돌리는 함수
    {
        //getxmlData_INDRT()
    }

    fun init_PRODUCT() {
        thread_product.isDaemon = true
        thread_product.start()
    }


    fun getxmlData_Recipe() //xml 파싱. xml에서 재료 종류만 뽑아옴.
    {
        try {
            val scan = Scanner(resources.openRawResource(R.raw.cooking_array))
            var page = ""
            while (scan.hasNextLine()) {
                val line = scan.nextLine()
                page += line
            }
            Log.v("page",page)
            val parser = JsonParser()
            val json = parser.parse(page).asJsonObject
            val json_part = json.getAsJsonObject("Grid_20150827000000000226_1")
            val jarr = json_part.getAsJsonArray("row").asJsonArray
            var ID:Int = 0;var name:String = "";var sumry:String="";var nation:String="";
            var time:String="";var cal:String="";var level:String="";var img:String="";var cook:String="";
            for(i in 0 until jarr.size()) {
                var main_arr = ArrayList<ingrtdata>();var sub_arr = ArrayList<ingrtdata>()
                var main_lack_arr = ArrayList<ingrtdata>();var sub_lack_arr = ArrayList<ingrtdata>()
                //json = jarr.getJSONObject(i)
                ID = jarr[i].asJsonObject.get("RECIPE_ID").asString.toInt()
                name = jarr[i].asJsonObject.get("RECIPE_NM_KO").asString
                sumry = jarr[i].asJsonObject.get("SUMRY").asString
                nation = jarr[i].asJsonObject.get("NATION_NM").asString
                time = jarr[i].asJsonObject.get("COOKING_TIME").asString
                cal = jarr[i].asJsonObject.get("CALORIE").asString
                level = jarr[i].asJsonObject.get("LEVEL_NM").asString
                img = jarr[i].asJsonObject.get("IMG_URL").asString
                cook = jarr[i].asJsonObject.get("TY_NM").asString
                for(i in 1..7){
                    var url: URL
                    var first_page = ((i-1)*(1000)).toString()
                    var last_page = (((i*1000))-1).toString()
                    try {
                        url = URL(
                            "http://211.237.50.150:7080/openapi/" +
                                    "d54b81c630464f93c2eda9be92fccccc6eda4f4b65c7d23360ce262ef7170be0/" +
                                    "json/" + "Grid_20150827000000000227_1/" + first_page + "/" + last_page+"/"+"?RECIPE_ID="+ID.toString()
                        )
                        var Is: InputStream = url.openStream()
                        var rd: BufferedReader = BufferedReader(InputStreamReader(Is, "UTF-8"))
                        var line: String? = null
                        var page = ""
                        line = rd.readLine()
                        if(!line.contains("ROW_NUM"))
                            continue
                        Log.v("line", line)
                        while (line != null) {
                            page += line
                            Log.v("line", line)
                            line = rd.readLine()
                        }
                        Log.v("page", page)
                        val parser = JsonParser()
                        val json = parser.parse(page).asJsonObject
                        val json_part = json.getAsJsonObject("Grid_20150827000000000227_1")
                        val jarr = json_part.getAsJsonArray("row").asJsonArray
                        var name: String = "";
                        var main: String = "";
                        for (i in 0 until jarr.size()) {
                            name = jarr[i].asJsonObject.get("IRDNT_NM").asString
                            main = jarr[i].asJsonObject.get("IRDNT_TY_NM").asString
                            if (main == "주재료"){
                                main_arr.add(ingrtdata(name,ingredient_set.get(name)!!.first,ingredient_set.get(name)!!.second, false, false))
                                if(m_fdata.filter{it.fkind == name}.size != 0)
                                else{
                                    main_lack_arr.add(ingrtdata(name,ingredient_set.get(name)!!.first,ingredient_set.get(name)!!.second, false, false))
                                }
                            }
                            else {
                                sub_arr.add(ingrtdata(name,ingredient_set.get(name)!!.first,ingredient_set.get(name)!!.second, false, false))
                                if(m_fdata.filter{it.fkind == name}.size != 0)
                                else{
                                    sub_lack_arr.add(ingrtdata(name,ingredient_set.get(name)!!.first,ingredient_set.get(name)!!.second, false, false))
                                }
                            }
                        }
                        break;
                    }
                    finally{

                    }
                }

                rdata.add(RecipeData(img,name,sumry,nation,time,cal,level, ID,main_arr,sub_arr,main_lack_arr ,sub_lack_arr,false, 0, cook))
                // !! likecount db에서 불러와서 수정해야함
                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
            }
            recommend_all_rdata = recommend_rdata
            show_rdata = recommend_rdata
        } finally {
            var e: Exception? = null
            e?.printStackTrace()
        }
        for(i in 0..likeCountData.size-1){
            var ID = likeCountData[i].first
            var likecount = likeCountData[i].second
            Log.v("ID+likecount",ID.toString()+" "+likecount.toString())
            rdata.find {
                it.recipe_num == ID
            }!!.likeCount = likecount
        }

        isParsingComplete = true
    }

    override fun onDestroy() {
        clearAll()

        finishAffinity()
        System.runFinalization()
        System.exit(0)

        //Thread.sleep(11)
        //thread_recipe.interrupt()
        //thread_product.interrupt()
       // thread_recipe.suspend()
       // thread_recipe.interrupt()
       // thread_product.suspend()
        //thread_product.interrupt()
        super.onDestroy()
    }

    fun getJsonData_Product() // 농산물 json parsing
    {
        var count = 0
        try {
            val scan = Scanner(resources.openRawResource(R.raw.product_set2))
            var page = ""
            while (scan.hasNextLine()) {
                val line = scan.nextLine()
                page += line
            }
            var json = JSONObject(page)
            val json_part = JSONObject(json.getJSONObject("Grid_20171128000000000572_1").toString())
            val jarr = json_part.getJSONArray("row")
            var ID:Int = 0;var name:String = "";var kind:String="";var effect:String="";
            var store_mth:String="";var pEra:String="";var cook_mth:String="";
            var pick_mth:String="";var pmonth_str:String="";var img:String="";


            for(i in 0 until jarr.length()) {
                json = jarr.getJSONObject(i)
                ID = json.getString("IDNTFC_NO").toInt()
                name = json.getString("PRDLST_NM")
                kind = json.getString("PRDLST_CL")
                effect = json.getString("EFFECT")
                store_mth = json.getString("TRT_MTH")
                pmonth_str = json.getString("M_DISTCTNS_ITM")
                pick_mth = json.getString("PURCHASE_MTH")
                cook_mth = json.getString("COOK_MTH")
                pEra = json.getString("PRDCTN__ERA")
                img = json.getString("IMG_URL")

                kind = kind.replace("농산물>", "").trim()

                var pMonth : ArrayList<Int> = arrayListOf()
                var st = StringTokenizer(pmonth_str,",")
                while(st.hasMoreTokens()) {
                    var month = st.nextToken().trim().toInt()
                    pMonth.add(month)
                    if(current_month==month) {
                        season_product.add(productData(ID, img, name, kind, pEra,
                            pMonth, effect, pick_mth,
                            store_mth, cook_mth)) // 제철 재료 추가
                    }
                }

                pdata.add(productData(ID, img, name, kind, pEra,
                    pMonth, effect, pick_mth,
                    store_mth, cook_mth))

                Log.v("pname", name)
                count++
            }
            all_product = pdata

        } finally {
            var e: Exception? = null
            e?.printStackTrace()
        }
        Log.v("pcount", count.toString())
    }


    fun initlistener(){
        user_info_button.setOnClickListener{
            main_drawerLayout.openDrawer(GravityCompat.START)
        }
        search_button.setOnClickListener {
            spinner.setSelection(0)
            rdata = CopyOnWriteArrayList(rdata.sortedBy {
                    it->it.recipe_num
            })
            if(rdata.size == 0){
                Toast.makeText(this,"로딩 중입니다. 잠시만 기다려주세요.",Toast.LENGTH_SHORT).show()
            }
            initadapter()
        }
        plusButton.setOnClickListener {
            startMyPopupActivity()
        }
        val add = findViewById<ImageButton>(R.id.addButton)
        add.setOnClickListener {
            startMyPopupActivity3()
        }
        subButton.setOnClickListener {
            // 체크박스 눌린 카드 체크해서 지움
            if (shopOffadapter.ingList.isNotEmpty()) {
                shopOffadapter.notifyItemRangeChanged(0, shopOffadapter.itemCount, "delete")
                for (i in 0..shopOffadapter.deleteList.size-1) {
                    var p1 = shopOffadapter.deleteList.get(i)
                    shopOffadapter.ingList.remove(p1.fkind)
                    shopOffadapter.delete(p1)
                    // db 삭제하기
                    conditionRef2.child(shopOffadapter.deleteindex.get(i).toString()).removeValue()
                    Log.v("장바구니 삭제 : ",shopOffadapter.deleteindex.get(i).toString()+"를 삭제하였습니다.")
                    ingredient.remove(p1)
                }
                shopOffadapter.deleteList.clear()
                shopOffadapter.deleteindex.clear()
                shopOffadapter.notifyDataSetChanged()
            }
            else {
                Toast.makeText(this, "선택한 재료가 없습니다. ", Toast.LENGTH_LONG).show()
            }
        }
        buyBtn.setOnClickListener {
            // 내 냉장고로 물품 이동

                startMyPopupActivity7(shopOffadapter.ingList)
        }

        minusButton.setOnClickListener {
            if (isMinus) {
                fadapter.notifyItemRangeChanged(0, fadapter.itemCount, "clickOn")
                minusButton.setImageResource(R.drawable.ic_delete_black_24dp)
            }
            else {
                fadapter.notifyItemRangeChanged(0, fadapter.itemCount, null)
                minusButton.setImageResource(R.drawable.ic_remove_circle_outline_black_24dp)

                for (i in 0..fadapter.deleteList.size-1) {
                    var p1 = fadapter.deleteList.get(i)
                    fadapter.delete(p1)
                    conditionRef1.child(fadapter.deleteindex.get(i).toString()).removeValue()

                    m_fdata.remove(p1)
                    (rdata.filter { it.rmain.filter { it.name == p1.fkind }.isNotEmpty() }).all {
                        it.rlackmain.add(ingrtdata(p1.fkind!!,ingredient_set.get(p1.fkind!!)!!.first,ingredient_set.get(p1.fkind!!)!!.second, false, false)) }
                    (rdata.filter { it.rsub.filter { it.name == p1.fkind }.isNotEmpty() }).all {
                        it.rlacksub.add(ingrtdata(p1.fkind!!,ingredient_set.get(p1.fkind!!)!!.first,ingredient_set.get(p1.fkind!!)!!.second, false, false)) }

                    recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                        it.rlackmain.size ==0
                    })
                    Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                    recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                        it.rmain == it.rmain.filterNot {
                            disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                        }
                    })

                }
                fadapter.deleteList.clear()
                fadapter.deleteindex.clear()

//                editt
                fadapter.notifyDataSetChanged()

                show_rdata = recommend_rdata
                initadapter()
            }
            isMinus = !isMinus
        }
        search_recipe.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                search_rdata = CopyOnWriteArrayList<RecipeData>(rdata.filter { it.rname.contains(str) })
                initadapter()
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
        search_products.addTextChangedListener(object:TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                val str = s.toString()
                search_pdata.clear()
                search_pdata = CopyOnWriteArrayList<productData>(pdata.filter { it.pName.contains(str) })
                initadapter()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })

        var onSeason = true
        search_pro_button.setOnClickListener {
            spinner_pro.setSelection(0)
            if(onSeason) {
                search_pro_button.text = "제철 농산물"
                search_products.text.clear()
                pdata = season_product
            }
            else {
                search_pro_button.text = "전체 농산물"
                search_products.text.clear()
                pdata = all_product
            }
            initadapter()
            onSeason = !onSeason
        }

        var rectext = findViewById<TextView>(R.id.rec_text)
        onFridge = false

        fridgeButton.setOnClickListener {
            spinnernation.setSelection(0)
            spinnercook.setSelection(0)
            if(onFridge) { // 전체 요리 추천
                recommend_all_rdata = CopyOnWriteArrayList(rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                rectext.text = "전체 추천 요리"
                fridgeButton.setImageResource(R.drawable.refrige2)
                show_rdata = recommend_all_rdata
            }
            else { // 냉장고 기반 추천
                rectext.text = "냉장고 재료로 만들 수 있는 요리"
                fridgeButton.setImageResource(R.drawable.refrige)
                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                Log.v("recommend_rdata_1",recommend_rdata.size.toString())
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                show_rdata = recommend_rdata
            }
            onFridge = !onFridge

            initadapter()
            which_rdata = show_rdata
            cuisine_rdata = show_rdata
        }
        renew_button.setOnClickListener {
            spinnernation.setSelection(0)
            spinnercook.setSelection(0)
            if(rdata.size == 0){
                Toast.makeText(this,"로딩 중입니다. 잠시만 기다려주세요.",Toast.LENGTH_SHORT).show()
            }
            if(onFridge) {
                recommend_rdata = CopyOnWriteArrayList(rdata.filter {
                    it.rlackmain.size ==0
                })
                recommend_rdata = CopyOnWriteArrayList(recommend_rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })
                show_rdata = recommend_rdata
            }
            else { // 전체 요리 추천에서 갱신
                recommend_all_rdata = CopyOnWriteArrayList(rdata.filter{
                    it.rmain == it.rmain.filterNot {
                        disgust_food_list.contains(ingrtdata(it.name,it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,it.category2,false,false))|| disgust_food_list.contains(ingrtdata("전체",it.category1,"0",false,false))
                    }
                })

                show_rdata = recommend_all_rdata
            }
            initadapter()
            which_rdata = show_rdata
            cuisine_rdata = show_rdata
        }
    }
    fun inittab(){
        tabHost1 = findViewById<View>(R.id.tabhost1) as TabHost
        tabHost1.setup()
        tabHost1.setOnTabChangedListener {
            Log.v("tabsetonclick","sssss")
            initadapter()
            if(tabcontent== tabHost1.content4) {
                setContentView(R.layout.activity_main)
            }
        }

        tabHost1.setOnTabChangedListener(this)

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        val ts1 = tabHost1.newTabSpec("Tab Spec 1")
        ts1.setContent(R.id.content1)
        ts1.setIndicator("MY\n냉장고")
        tabHost1.addTab(ts1)


        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        val ts2 = tabHost1.newTabSpec("Tab Spec 2")
        ts2.setContent(R.id.content2)
        ts2.setIndicator("추천\n요리")
        tabHost1.addTab(ts2)

        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        val ts3 = tabHost1.newTabSpec("Tab Spec 3")
        ts3.setContent(R.id.content3)
        ts3.setIndicator("요리"+"\n"+"백과")
        tabHost1.addTab(ts3)

        // 네 번째 Tab. (탭 표시 텍스트:"TAB 4"), (페이지 뷰:"content4")
        val ts4 = tabHost1.newTabSpec("Tab Spec 4")
        ts4.setContent(R.id.content4)
        ts4.setIndicator("농산물"+"\n"+"백과")
        tabHost1.addTab(ts4)

        val ts5 = tabHost1.newTabSpec("Tab Spec 5")
        ts5.setContent(R.id.content5)
        ts5.setIndicator("장"+"\n"+"바구니")
        tabHost1.addTab(ts5)

        tabHost1.tabWidget.setCurrentTab(0)
        var tv = tabHost1.tabWidget.getChildAt(tabHost1.currentTab).findViewById(android.R.id.title) as TextView
        tv.setTextColor(Color.parseColor("#FFFFFFFF"))

    }

    override fun onTabChanged(tabId: String?) {
        initadapter()
        for (i in 0..tabHost1.tabWidget.childCount-1) {
            var tv = tabHost1.tabWidget.getChildAt(i).findViewById(android.R.id.title) as TextView
            tv.setTextColor(Color.parseColor("#FFFFFFFF"))
            tv.gravity = Gravity.CENTER
        }
        var tv = tabHost1.tabWidget.getChildAt(tabHost1.currentTab).findViewById(android.R.id.title) as TextView
        tv.setTextColor(Color.parseColor("#000000"))
        tv.gravity = Gravity.CENTER

    }

    fun initstarquery(){
        val myStarPostQuery = conditionRef3.orderByKey()
        myStarPostQuery.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    Log.v("현재 키 : ",p0.key)

                    for (postSnapshot in p0.getChildren()) {
                        Log.v("다음 키 : ",postSnapshot.key)
                        // TODO: handle the post
                        val value =postSnapshot.getValue(String::class.java)

                        //☆
                        var key = postSnapshot.key!!.toInt()
                        firebase_rdata.add(postSnapshot.key!!)
                        for(i in 0..rdata.size-1){
                            if(rdata.get(i).recipe_num == postSnapshot.key!!.toInt()){
                                Log.v("rdata[i].recipe_nm",rdata[i].rname)
                                rdata.get(i).rStar = true
                                break
                            }
                        }
                        //★

                    }
                }
            })
    }


    fun initRefrigeratorData()//불러오기
    {

        val myRefrigeratorPostsQuery = conditionRef1.orderByKey() //파이어베이스(from)에서 안드로이드(to)로
        myRefrigeratorPostsQuery.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.v("현재 키 : ",p0.key)

                    //  val text = p0.getValue(String::class.java)
                    for (postSnapshot in p0.getChildren()) {
                        //Log.v("다음 키 : ",postSnapshot.key)
                        // TODO: handle the post

                        slotNum_Ref = max(slotNum_Ref,postSnapshot.key!!.toInt())
                        // Log.v("맥스: ",slotNum_Ref.toString())
                        val value =postSnapshot.getValue(foodData::class.java)
                        val t2=value!!.fkind
                        val t3=value!!.fdue
                        val t4=value!!.fslotNum

                        m_fdata.add(foodData(t2,t3,t4))
                    }
                    initadapter()
                    init_notificationDue()
                    // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }


            })
        val myBasketPostQuery = conditionRef2.orderByKey()
        myBasketPostQuery.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.v("현재 키 : ",p0.key)

                    //  val text = p0.getValue(String::class.java)
                    for (postSnapshot in p0.getChildren()) {
                        slotNum_Bas = max(slotNum_Bas,postSnapshot.key!!.toInt())
                        Log.v("다음 키 : ",postSnapshot.key)
                        val value =postSnapshot.getValue(String::class.java)


                        ingredient.add(foodData(value!!,null,postSnapshot.key!!.toInt()))
                    }
                }

            })
        val myDisgustfoodPostsQuery = conditionRef4.orderByKey() //파이어베이스(from)에서 안드로이드(to)로
        myDisgustfoodPostsQuery.addListenerForSingleValueEvent(
            object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    Log.v("현재 키 : ",p0.key)
                    disgust_food_list.clear()
                    for (postSnapshot in p0.getChildren()) {
                        slotNum_Bas = max(slotNum_Bas,postSnapshot.key!!.toInt())
                        Log.v("다음 키 : ",postSnapshot.key)
                        val value =postSnapshot.getValue(ingrtdata::class.java)
                        disgust_food_list.add(value!!)
                    }
                }

            })
        val likeCountPostsQuery = conditionRef5.orderByKey()
        likeCountPostsQuery.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {
                for (postSnapshot in p0.getChildren()) {
                    Log.v("좋아요 데이터 ",postSnapshot.key+"번 째 요리 :" + postSnapshot.value +"개의 좋아요")
                    likeCountData.add(Pair(postSnapshot.key!!.toInt(),postSnapshot.value!!.toString().toInt()))
                }
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }
    fun set_text(){
        if(m_fdata.isNotEmpty()){
            ref_empty_text.visibility = View.INVISIBLE
        }
        else{
            ref_empty_text.visibility = View.VISIBLE
            ref_empty_text.text = "냉장고에 재료를 추가해주세요!"
        }
        if(show_rdata.isNotEmpty()){
            empty_text.visibility = View.INVISIBLE
        }
        else{
            empty_text.visibility = View.VISIBLE
             if( onFridge == true){
                empty_text.text = "냉장고 재료로 만들 수 있는 요리가 없습니다!"
            }
            else if(onFridge == false){
                empty_text.text = "갱신버튼을 눌러서 추천메뉴를 볼 수 있습니다!"
            }
        }
        if(rdata.size != 0){
            empty2_text.visibility = View.INVISIBLE
        }
        else{
            empty2_text.visibility = View.VISIBLE
            empty_text.visibility = View.VISIBLE
            empty2_text.text = "아직 로딩이 되지 않았습니다. 잠시만 기다려주세요!"
            empty_text.text = "아직 로딩이 되지 않았습니다. 잠시만 기다려주세요!"
        }
        if(pdata.size != 0){
            empty3_text.visibility = View.INVISIBLE
        }
        else{
            empty3_text.visibility = View.VISIBLE
            empty3_text.text = "아직 로딩이 되지 않았습니다. 잠시만 기다려주세요!"
        }
    }
    fun initadapter(){
        Log.v("어댑터 변경","성공")


        //탭 1(재료) 리사이클러뷰
       set_text()
        val flayoutManager = GridLayoutManager(this, 2)
        fadapter= foodAdapter(m_fdata,ingredient_set)
        listV.layoutManager=flayoutManager
        listV.adapter = fadapter

        //탭 2(요리) 리사이클러뷰

        val rlayoutManager = GridLayoutManager(this,2)
        //if(onFridge) show_rdata = recommend_rdata
        //else show_rdata = recommend_all_rdata
        radapter = RecommendRecipeAdapter(show_rdata) //두번째 요리목록
        reList.layoutManager = rlayoutManager
        reList.adapter = radapter
        spinnernation.onItemSelectedListener = SpinnerSelectedListener3()
        spinnercook.onItemSelectedListener = SpinnerSelectedListener4()


        //탭 3(요리) 리사이클러뷰

        val sort_rlayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        if(search_recipe.text.isEmpty()) sort_radapter =RecipeAdapter(rdata,conditionRef3,firebase_rdata)
        else sort_radapter = RecipeAdapter(search_rdata,conditionRef3,firebase_rdata)
        recipe_view.layoutManager = sort_rlayoutManager //세번쨰 요리백과
        recipe_view.adapter = sort_radapter


        //요리 정렬 스피너의 아이템 클릭 시 정렬방식 변경.
        spinner.onItemSelectedListener = SpinnerSelectedListener()

        //
        sort_radapter.ItemClickListener = object:RecipeAdapter.OnItemClickListner{
            override fun OnItemClick(holder: RecipeAdapter.ViewHolder, view: View, data: RecipeData, position: Int) {
                Log.v("선택","onItemClick")
                Log.v("레시피 data 전송 : ",data.toString())
                startMyPopupActivity4(data)
            }
        }

        //
        radapter.ItemClickListener = object:RecommendRecipeAdapter.OnItemClickListner{
            override fun OnItemClick(holder: RecommendRecipeAdapter.ViewHolder, view: View, data: RecipeData, position: Int
            ) {
                // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                Log.v("레시피 data 전송 : ",data.toString())
                startMyPopupActivity4(data)

            }
        }

        //탭 4(농산물) 리사이클러뷰

        val sort_playoutManager = GridLayoutManager(this, 2)
        if(search_products.text.isEmpty()) sort_padapter =productAdapter(pdata)
        else sort_padapter = productAdapter(search_pdata)
//        sort_padapter = productAdapter(pdata)
        products_view.layoutManager = sort_playoutManager //세번쨰 요리백과
        products_view.adapter = sort_padapter


        //요리 정렬 스피너의 아이템 클릭 시 정렬방식 변경.
        spinner_pro.onItemSelectedListener = SpinnerSelectedListener2()

        sort_padapter.ItemClickListener = object:productAdapter.OnItemClickListner{
            override fun OnItemClick(holder: productAdapter.ViewHolder, view: View, data: productData, position: Int) {
                Log.v("선택","onItemClick")
                startMyPopupActivity9(data.pName, data.pImg, data.pKind, data.pEra,
                    data.pEffect, data.ppick_Mth, data.pstore_Mth, data.pcook_Mth)
            }
        }

        //탭5(오프라인, 온라인) 리사이클러뷰
        val layoutManager2 = GridLayoutManager(this, 2)
        val layoutManager3 = GridLayoutManager(this, 2)
        shopListOff.layoutManager = layoutManager2
        shopOffadapter = MyShopOffAdapter(ingredient)
        shopListOff.adapter = shopOffadapter

        shopOffadapter.listBtnClickListener = object : MyShopOffAdapter.ListBtnClickListener{
            override fun onListBtnClick(holder: MyShopOffAdapter.ViewHolder, view: View, data: foodData, position: Int) {
                startMyPopupActivity5(data.fkind!!)
            }
        }
    }
    fun startMyPopupActivity(){
        Log.v("value",ingredient_set.size.toString())
        if(ingredient_set.size >= 728){
            val intent = Intent(this, registerActivity::class.java)
            intent.putExtra("data", "Test Popup")
            intent.putExtra("slotNum", slotNum_Ref)
            intent.putExtra("INDRT_SET",ingredient_set) //요리 종류 넘겨줌
            startActivityForResult(intent, 1)
        }
        else
        {
            Toast.makeText(this,"로딩 중입니다! .잠시만 기다려주세요!",Toast.LENGTH_SHORT).show()
        }
    }

    fun startMyPopupActivity3(){
        if(ingredient_set.size >= 728){
            val intent = Intent(this, AddBasketActivity::class.java)
            intent.putExtra("data", "Test Popup")
            intent.putExtra("slotNum", slotNum_Bas)
            intent.putExtra("INDRT_SET",ingredient_set) //요리 종류 넘겨줌
            startActivityForResult(intent, 2)
        }
        else
        {
            Toast.makeText(this,"로딩 중입니다! .잠시만 기다려주세요!",Toast.LENGTH_SHORT).show()
        }
    }
    fun startMyPopupActivity4(rdata : RecipeData) //Recycler view 클릭 시
    {
        val intent = Intent(this, ShowRecipeActivity::class.java)
        intent.putExtra("recipe", rdata)
        intent.putExtra("product", pdata)
        intent.putExtra("disgust", disgust_food_list)

        //파이어베이스에 저장된 원소들 중 내가 고른 아이템의 순서가 존재한다면,
        /*for(i in 0..firebase_rdata.size-1){
            if(firebase_rdata[i]==rdata.recipe_num.toString()){
                intent.putExtra("likeData",true)
                startActivityForResult(intent, 1)
                return
            }

        }
        intent.putExtra("likeData",false)*/
        startActivityForResult(intent, 1)


    }
    fun startMyPopupActivity5(ingName : String){ // 온라인쇼핑몰 선택
        val intent = Intent(this, SelectShopActivity::class.java)
        intent.putExtra("ingName", ingName)
        startActivity(intent)
    }

    fun startMyPopupActivity7(ingList: ArrayList<String>){ // 장바구니에서 냉장고로 재료 이동
        for (i in 0..ingList.size-1) {
            val intent = Intent(this, BuyIngreActivity::class.java)
            intent.putExtra("ingData", ingList.get(i))
            intent.putExtra("slotNum", slotNum_Ref)
            startActivityForResult(intent, 3)
        }
    }
    fun startMyPopupActivity8(username : String){ // 사용자 정보 입력
        if(ingredient_set.size >= 728){
            val intent = Intent(this, UserinfoActivity::class.java)
            intent.putExtra("data", "Test Popup")
            intent.putExtra("username", username)
            intent.putExtra("slotNum", slotNum_Bas)
            intent.putExtra("INDRT_SET",ingrtArray) //요리 종류 넘겨줌
            intent.putExtra("DISGUST_FOOD",disgust_food_list)
            startActivityForResult(intent, 4)
        }
        else
        {
            Toast.makeText(this,"로딩 중입니다! .잠시만 기다려주세요!",Toast.LENGTH_SHORT).show()
        }
    }
    fun startMyPopupActivity9(name: String, img: String, kind : String, era : String,
                              effect : String, pick_mth : String, store_mth : String, cook_mth:String)
    //Recycler view 클릭 시
    // 농산물 상세정보 출력
    {
        val intent = Intent(this, ShowProductActivity::class.java)
        intent.putExtra("IMG",img)
        intent.putExtra("NAME",name)
        intent.putExtra("KIND",kind)
        intent.putExtra("ERA",era)
        intent.putExtra("EFFECT",effect)
        intent.putExtra("PICK_MTH",pick_mth)
        intent.putExtra("STORE_MTH",store_mth)
        intent.putExtra("COOK_MTH",cook_mth)

        startActivity(intent)
    }
    fun startMyPopupActivity10(){

        myFavoriteData = CopyOnWriteArrayList(rdata.filter{
            it.rStar == true
        })
        Log.v("즐겨찾기 요리 추가됨? in 메인",myFavoriteData.toString())
        val intent = Intent(this, MyFavoriteActivity::class.java)


        startActivity(intent)
    }
    inner class SpinnerSelectedListener :
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//            Toast.makeText(parent?.context, parent?.getItemAtPosition(position).toString(),
//                Toast.LENGTH_LONG).show()
            var str = parent?.getItemAtPosition(position).toString()
            if(str.equals("칼로리 순")) {
                if(search_recipe.text.isNotEmpty()){
                    search_rdata = CopyOnWriteArrayList(search_rdata.sortedWith(kotlin.Comparator{o1,o2->
                        if(o1.rcal != "" && o2.rcal != ""){
                            return@Comparator o1.rcal.replace("Kcal","1").toInt() - o2.rcal.replace("Kcal","1").toInt()
                        }
                        else{
                            return@Comparator -1
                        }
                    }))
                }
                else{
                    rdata = CopyOnWriteArrayList(rdata.sortedWith(kotlin.Comparator{o1,o2->
                        if(o1.rcal != "" && o2.rcal != ""){
                            return@Comparator o1.rcal.replace("Kcal","1").toInt() - o2.rcal.replace("Kcal","1").toInt()
                        }
                        else{
                            return@Comparator -1
                        }
                    }))
                }
                initadapter()
            } else if(str.equals("시간 순")) {
                if(search_recipe.text.isNotEmpty()){
                    search_rdata =CopyOnWriteArrayList(search_rdata.sortedBy { it ->
                        it.rtime.replace("분", "").toInt()
                    })
                }
                else {
                    rdata = CopyOnWriteArrayList(rdata.sortedBy { it ->
                        it.rtime.replace("분", "").toInt()
                    })
                }
                initadapter()

            } else if(str.equals("난이도 순")) {
                if(search_recipe.text.isNotEmpty()) {
                    search_rdata = CopyOnWriteArrayList(search_rdata.sortedWith(kotlin.Comparator { o1, o2 ->
                        var temp_o1 = 0 ;var temp_o2 = 0
                        when (o1.rlevel) {
                            "초보환영" -> {
                                temp_o1 = 1
                            }
                            "보통" -> {
                                temp_o1 = 2
                            }
                            else -> {
                                temp_o1 = 3
                            }
                        }
                        when (o2.rlevel) {
                            "초보환영" -> {
                                temp_o2 = 1
                            }
                            "보통" -> {
                                temp_o2 = 2
                            }

                            else -> {
                                temp_o2 = 3
                            }
                        }
                        return@Comparator temp_o1 - temp_o2
                    }))}
                else{
                    rdata = CopyOnWriteArrayList(rdata.sortedWith(kotlin.Comparator { o1, o2 ->
                        var temp_o1 = 0
                        var temp_o2 = 0
                        when (o1.rlevel) {
                            "초보환영" -> {
                                temp_o1 = 1
                            }
                            "보통" -> {
                                temp_o1 = 2
                            }
                            else -> {
                                temp_o1 = 3
                            }
                        }
                        when (o2.rlevel) {
                            "초보환영" -> {
                                temp_o2 = 1
                            }
                            "보통" -> {
                                temp_o2 = 2
                            }

                            else -> {
                                temp_o2 = 3
                            }
                        }
                        return@Comparator temp_o1 - temp_o2
                    }))
                }
                initadapter()
            }
            else if(str.equals("인기순")) {
                if(search_recipe.text.isNotEmpty()){
                    search_rdata = CopyOnWriteArrayList(search_rdata.sortedWith(kotlin.Comparator { o1, o2 ->
                        return@Comparator o2.likeCount - o1.likeCount
                    }))
                }
                else {
                    rdata = CopyOnWriteArrayList(rdata.sortedWith(kotlin.Comparator { o1, o2 ->
                        return@Comparator o2.likeCount - o1.likeCount
                    }))
                }
                initadapter()
            }
            else if(str.equals("이름 순")){
                if(search_recipe.text.isNotEmpty()){
                    search_rdata = CopyOnWriteArrayList(search_rdata.sortedBy {
                            it->it.rname.toString()
                    })
                }
                else{
                    rdata = CopyOnWriteArrayList(rdata.sortedBy {
                            it->it.rname.toString()
                    })
                }
                initadapter()
            }
            else if(str.equals("전체 요리")){
                search_button.performClick()
            }
        }
    }

    inner class SpinnerSelectedListener2 : // 농산물 종류 spinner
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val product_array = resources.getStringArray(R.array.product_array)
            search_pdata.clear()
            var str = parent?.getItemAtPosition(position).toString()
            if(!str.equals("전체")) {
                for(i in 0 until product_array.size) {
                    if(str.equals(product_array[i])) {
                        for(j in 0 until pdata.size) {
                            if(pdata[j].pKind.contains(str)) {
                                search_pdata.add(pdata[j])
                            }
                        }
                    }
                }
                sort_padapter = productAdapter(search_pdata)
                sort_padapter.notifyDataSetChanged()
                products_view.adapter = sort_padapter
                sort_padapter.ItemClickListener = object:productAdapter.OnItemClickListner{
                    override fun OnItemClick(holder: productAdapter.ViewHolder, view: View, data: productData, position: Int) {
                        Log.v("선택","onItemClick")
                        startMyPopupActivity9(data.pName, data.pImg, data.pKind, data.pEra,
                            data.pEffect, data.ppick_Mth, data.pstore_Mth, data.pcook_Mth)
                    }
                }
            }
            else {
//                search_pdata = pdata
                initadapter()

            }

        }
    }

    //전체추천요리인지 냉장고추천요리인지
    inner class SpinnerSelectedListener3 : // 요리 cuisine spinner
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            cuisine_rdata = show_rdata
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var str = parent?.getItemAtPosition(position).toString()
            var cook_array = arrayOf<String>()
            when (str) {
                "나라 전체" -> {
                    cook_array = resources.getStringArray(R.array.allcook_array)
                }
                "한식" -> {
                    cook_array = resources.getStringArray(R.array.korean_array)
                }
                "일본" -> {
                    cook_array = resources.getStringArray(R.array.japanese_array)
                }
                "중국" -> {
                    cook_array = resources.getStringArray(R.array.chinese_array)
                }
                "서양" -> {
                    cook_array = resources.getStringArray(R.array.western_array)
                }
                "이탈리아" -> {
                    cook_array = resources.getStringArray(R.array.italian_array)
                }
                "동남아시아" -> {
                    cook_array = resources.getStringArray(R.array.southeastasian_array)
                }
                "퓨전" -> {
                    cook_array = resources.getStringArray(R.array.fusion_array)
                }
            }
            var temp_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
            if(!str.equals("나라 전체")) {
                for (i in 0 until which_rdata.size) {
                    if(str.equals(which_rdata[i].rnation)) {
                        temp_rdata.add(which_rdata[i])
                    }
                }
                show_rdata = temp_rdata

            }
            else {
                show_rdata = which_rdata

            }
            cuisine_rdata = show_rdata
            initadapter()

            var adapter_cook = ArrayAdapter<String>(
                this@MainActivity.mContext,
                android.R.layout.simple_spinner_dropdown_item, cook_array)
            spinnercook.adapter = adapter_cook

        }
    }
    inner class SpinnerSelectedListener4 : // 요리 cuisine spinner
        AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            var str = parent?.getItemAtPosition(position).toString()

            var temp_rdata: CopyOnWriteArrayList<RecipeData> = CopyOnWriteArrayList()
            if(!str.equals("요리법 전체")) {
                for (i in 0 until cuisine_rdata.size) {
                    if(str.equals(cuisine_rdata[i].rcook)) {
                        temp_rdata.add(cuisine_rdata[i])
                    }
                }
                show_rdata = temp_rdata
            }
            else {
                show_rdata = cuisine_rdata
            }
            initadapter()

        }
    }

    var pressTime: Long = 0
    override fun onBackPressed() {
        var currentTime = System.currentTimeMillis()
        var intervalTime = currentTime - pressTime

        if(intervalTime <2000){
            super.onBackPressed()
            finishAffinity()
        }else{
            pressTime = currentTime;
            Toast.makeText(this,"한 번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show()
        }
    }

}