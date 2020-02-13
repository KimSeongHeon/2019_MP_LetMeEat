package com.letmeeat.project

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.Window
import kotlinx.android.synthetic.main.activity_select_shop.*
import java.net.URLEncoder

class SelectShopActivity : Activity() {
    lateinit var txt : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_select_shop)
        init()
    }
    fun init() {
        val intent = getIntent()
        txt = intent.getStringExtra("ingName")
        oningre.text = txt +" 구매하기"
    }
    fun OnBtnClick(v : View) {
        when(v.id) {
            R.id.gmarketBtn-> {
                goOnline("gmarket","UTF-8")
            }
            R.id.kurlyBtn-> {
                goOnline("Kurly", "EUC-KR")
            }
            R.id.coupBtn-> {
                goOnline("coupang","UTF-8")
            }
            R.id.naveBtn-> {
                goOnline("naver","UTF-8")
            }
            R.id.wemaBtn-> {
                goOnline("wemap","UTF-8")
            }
            R.id.tmonBtn-> {
                goOnline("tmon","UTF-8")
            }
        }
    }
    fun goOnline(shop : String, type : String) {
        var URL = ""
        var subURL = txt
        when(type) {
            "EUC-KR"-> {
                when(shop) {
                    "Kurly"-> {
                        URL = "https://www.kurly.com/m2/goods/list.php?hid_pr_text=4%C1%D6%B3%E2+%B1%E2%C8%B9%C0%FC&hid_link_url=http%3A%2F%2Fwww.kurly.com%2Fshop%2Fgoods%2Fgoods_list.php%3Fcategory%3D188&edit=Y&kw="

                    }
                }
                subURL = URLEncoder.encode(subURL, "EUC-KR")
            }
            "UTF-8"-> {
                when(shop) {
                    "gmarket"-> {
                        URL = "http://mobile.gmarket.co.kr/Search/Search?topKeyword="
                    }
                    "coupang"-> {
                        URL = "http://m.coupang.com/nm/search?q="
                    }
                    "naver"-> {
                        URL = "https://msearch.shopping.naver.com/search/all?query="
                    }
                    "wemap"-> {
                        URL = "https://msearch.wemakeprice.com/search?keyword="
                    }
                    "tmon"-> {
                        URL = "http://m.search.tmon.co.kr/search?useArtistchaiRegion=Y#_=1559300938608&keyword="
                    }
                }
                subURL = URLEncoder.encode(subURL, "UTF-8")
            }
        }
        URL = URL + subURL
        val page = Uri.parse(URL)
        val intent = Intent(Intent.ACTION_VIEW, page)
        startActivity(intent)

    }
}
