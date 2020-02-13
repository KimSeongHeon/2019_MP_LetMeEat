package com.letmeeat.project

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.letmeeat.project.MainActivity.Companion.dueCountIndex
import com.letmeeat.project.MainActivity.Companion.m_fdata
import kotlinx.android.synthetic.main.layout_dialog.view.*


class CustomDialogFragment : DialogFragment() {
    lateinit var dadapter:dueAdapter
    var ddata:ArrayList<dueData> = ArrayList()

    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        content = arguments!!.getString("content")

        // Pick a style based on the num.
        val style = DialogFragment.STYLE_NO_FRAME
        val theme = R.style.DialogTheme
        setStyle(style, theme)
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.layout_dialog, container, false)

        //val btnCancel = view.findViewById<View>(R.id.buttonCancel) as Button
        val btnAccept = view.findViewById<View>(R.id.buttonAccept) as Button

        val textViewContent = view.findViewById<View>(R.id.textViewContent) as TextView
        textViewContent.text = content

        //FontUtils.setTypeface(getActivity(), textViewQuestion, "fonts/mangal.ttf");
        //FontUtils.setTypeface(getActivity(), textViewAnswer, "fonts/mangal.ttf");
       // btnCancel.setOnClickListener {
       //     Toast.makeText(activity, "action cancelled", Toast.LENGTH_SHORT).show()
       //     dismiss()
       // }

        btnAccept.setOnClickListener {
            Toast.makeText(activity, "확인되었습니다.", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        //20190603 추가 dadapter에 대해
        val context = view.context
        val dlayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false) //유통기한용 매니저

        Log.d("check", "실행확인")
        //ddata에다가 유통기한 지난 물품들을 넣어줘야함.

        //Log.v("duecount", dueCountIndex[0].toString())
        if(dueCountIndex.size != 0){
            for(i in 0..dueCountIndex.size-1){
                ddata.add(dueData(m_fdata[dueCountIndex[i]].fkind, m_fdata[dueCountIndex[i]].fdue))
            }
            Log.d("check",ddata.get(0).fname )
            Log.v("duecount1", ddata[0].fname)
            Log.d("check", "실행확인3")

        }
        else{
            ddata.add(dueData("", ""))
        }
        dadapter = dueAdapter(ddata)
        view.listDue.layoutManager= dlayoutManager
        view.listDue.adapter = dadapter

        //fragment생성을 하는데, 여기서는 코드만 있고, view에 대한 정보가 없음
        //요점은, fragment는 view를 동적으로 만들어서 붙이는데, 기준이 없었음. view에다가 붙여야 하는데. 그래서 특정 뷰에 대한 정의가 필요함


        return view
    }

    companion object {


        /**
         * Create a new instance of CustomDialogFragment, providing "num" as an
         * argument.
         */
        fun newInstance(content: String): CustomDialogFragment {
            val f = CustomDialogFragment()

            // Supply num input as an argument.
            val args = Bundle()
            args.putString("content", content)
            f.arguments = args

            return f
        }
    }

}
