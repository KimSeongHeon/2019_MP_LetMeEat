package com.letmeeat.project

import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.kakao.auth.AuthType
import com.kakao.auth.Session
import kotlinx.android.synthetic.main.activity_login.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : AppCompatActivity(){
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    lateinit var input_password:String
    lateinit var input_id:String
    lateinit var imageURL: String
    lateinit var email:String
    lateinit var mRootRef:DatabaseReference
    lateinit var MyQuery:DatabaseReference
    lateinit var callback: SessionCallback

    var ID_PW_List:MutableList<MutableMap<String,String>> = mutableListOf()
    var u_IDList:ArrayList<String> = ArrayList()
    // var u_passwordList:ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up the login form.
        ////////////파이어베이스 초기화(id,pw 배열에 저장 시킴,위치 수정 x)//////
        initFirebase()
        /////////////////////////////////////////////////////////

        password.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        login_button.setOnClickListener { attemptLogin() }
        signin_button.setOnClickListener {registerAccount()}
        getHashKey()
        val session = Session.getCurrentSession()
        callback = SessionCallback(this)
        session.addCallback(callback)
        btn_custom_login.setOnClickListener {
            session.open(AuthType.KAKAO_LOGIN_ALL, this@LoginActivity)
        }
        getHashKey()
      /*  val intent = getIntent()
        var logout : String?
        if(intent!=null) {
            logout = intent?.getStringExtra("logout")
            if(logout==null) {
                if (session.isOpenable) {
                    session.checkAndImplicitOpen()
                    Log.v("opensession", "notyet")
                }
                else {
                    session.checkAndImplicitOpen()
                    Log.v("opensession", "opened")
                    btn_custom_login.performClick()
                }
            }
            else {

            }

        }*/


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    fun registerAccount(){
        input_id=userId.text.toString()
        input_password=password.text.toString()
        if(input_id!="" && input_password!=""){
            if(IDcheckVaild())
            {
                Toast.makeText(this,"중복 아이디가 있어 생성이 불가능 합니다.",Toast.LENGTH_SHORT).show()
            }
            else {
                mRootRef.child("USERID").child(input_id).setValue(input_password)
                //LoadMain() // way1
                Toast.makeText(this,"회원가입이 완료되었습니다!",Toast.LENGTH_SHORT).show()
                var tempID_PW:MutableMap<String,String> = mutableMapOf()
                tempID_PW[userId.text.toString()]=password.text.toString()
                ID_PW_List.add(tempID_PW)
                u_IDList.add(userId.text.toString())
                userId.text.clear()
                password.text.clear()
            }
        }
    }
    fun checkAccount(id: Long, nickname: String,p_imageURL:String,p_email:String){

        input_id=id.toString()
        email = p_email
        imageURL = p_imageURL
        Log.v("checkid", input_id +" "+ nickname)
        input_password = nickname
        if(IDcheckVaild()) { // 이미 생성된 계정
            LoadMain(input_id, nickname)
            Log.v("checkidtrue", "true")
        }
        else { // db에 입력
            mRootRef.child("USERID").child(input_id).setValue(input_password)
            //LoadMain() // way1
//            Toast.makeText(this,"회원가입이 완료되었습니다",Toast.LENGTH_SHORT).show()
            var tempID_PW:MutableMap<String,String> = mutableMapOf()
            tempID_PW[id.toString()]=nickname
            ID_PW_List.add(tempID_PW)
            u_IDList.add(id.toString())
            LoadMain(input_id, nickname)
            Log.v("checkidtrue", "false")
        }
    }
    fun initFirebase(){
        FirebaseApp.initializeApp(this)
        mRootRef = FirebaseDatabase.getInstance().reference
        MyQuery=mRootRef.child("USERID")
        MyQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                //  TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot) {

                for (postSnapshot in p0.getChildren()) {
                    // Log.v("아이디: ",postSnapshot.key.toString())
                    // Log.v("비밀번호 : ",postSnapshot.value.toString())
                    var tempID_PW:MutableMap<String,String> = mutableMapOf()
                    tempID_PW[postSnapshot.key.toString()]=postSnapshot.value.toString()
                    ID_PW_List.add(tempID_PW)
                    u_IDList.add(postSnapshot.key.toString())
                    //u_passwordList.add(postSnapshot.value.toString())

                }

            }
        })
    }
    fun attemptLogin(){
        input_id=userId.text.toString()
        input_password=password.text.toString()
        if(input_id!="" && input_password!="")
        { // 공백 검사
            if(IDcheckVaild())
            {// 유효 아이디 검사
                if(PWcheckValid(input_id,input_password)) // 비밀번호 검사
                    LoadMain()
            }
        }
        else{
            Toast.makeText(this,"아이디나 비밀번호칸이 공백입니다.",Toast.LENGTH_SHORT).show()
        }

    }
    fun IDcheckVaild():Boolean{
        for(curID in u_IDList){
            if(curID.equals(input_id))
            {
                Log.v("checkidcur", curID)
                // Toast.makeText(this,"로그인 성공!",Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }
    fun PWcheckValid(ID:String,password:String):Boolean{
        for(curMap in ID_PW_List){
            if(curMap[ID]==password) {

                // Toast.makeText(this, "비밀번호도 성공", Toast.LENGTH_SHORT).show()
                return true
            }
        }
        return false
    }
    fun LoadMain(){ // 일반 로그인
        val intent = Intent(this,MainActivity::class.java)
        intent.putExtra("type", "normal")
        intent.putExtra("ID",input_id)
        intent.putExtra("PW",input_password)
        startActivity(intent)
        finish()
    }
    fun LoadMain(id:String, nickname : String){ // 카카오톡 로그인
        val intent = Intent(this,LoadingActivity::class.java)
        intent.putExtra("type", "kakao")
        intent.putExtra("nickname",nickname)
        intent.putExtra("id",id)
        intent.putExtra("imageURL",imageURL)
        intent.putExtra("email",email)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        Session.getCurrentSession().removeCallback(callback)
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
            Toast.makeText(this,"한 번 더 누르시면 종료됩니다.",Toast.LENGTH_SHORT).show();
        }
    }
    private fun getHashKey() {
        var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        if (packageInfo == null)
            Log.v("KeyHash", "KeyHash:null")

        for (signature in packageInfo!!.signatures) {
            try {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.v("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.v("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }

        }
    }
}