package com.example.smart_gwnu.restful

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface APIS {

    @POST("login")
    @Headers("accept: application/json",
            "content-type: application/json")
    fun login(
            @Body jsonparams: UserLogin
    ): Call<R_UserLogin>

    //비콘 수신시 사용자 정보 저장 포스트 요청
    @POST("/record")
    @Headers("accept: application/json",
        "content-type: application/json",
       )
    fun userVisit(
        @Header("Authorization") authorization : String,
        @Body jsonparams: Saveinfo
    ):Call<R_SvaeInfo>

    //회원가입
    @POST("/signup")
    @Headers("accept: application/json",
        "content-type: application/json",
        )
    fun userJoin(
        @Body jsonparams: UserJoin
    ):Call<R_UserJoin>

    @POST("/notice")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun creat_notice(
        @Header("Authorization") authorization : String,
        @Body jsonparams: creatBoard
    ):Call<Any>

    @POST("/home/manager")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun postGraph(
        @Header("Authorization") authorization : String,
        @Body jsomparams: post_Graph
    ):Call<graph>





    @GET("/login")
    @Headers("accept: application/json",
            "content-type: application/json"
    )
    fun get_users(
    ): Call<HTTP_GET_Model>

    //사용자 방문기록 가져오기
    @GET("record/user")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun get_record():Call<Array<R_UserVisit>>

    //마이페이지 사용자/관리자 동일
    @GET("/mypage")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun get_mypage(
        @Header("Authorization") authorization : String
    ):Call<R_userSet>

    //관리자 방문기록 건물 ID받아오기
    @GET("/record/building")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun get_buildingID(
        @Header("Authorization") authorization : String,
    ):Call<Array<A>>

    //관리자 받아건 건물중 select된 건물 방문기록 가져오기
    @GET("record/building/{id}")
    @Headers("accept: application/json",
        "content-type: application/json"
    )
    fun get_buidingVisit(
        @Header("Authorization") authorization : String,
        @Path("id") id : String
    ):Call<Array<R_ManagerVisit>>

    @GET("/notice/building/{buildingid}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_noticeInfo(
        @Header("Authorization") authorization : String,
        @Path("buildingid") buildingid : String
    ):Call<Array<R_noticeInfo>>

    @GET("/group/manager")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_managerGroup(
        @Header("Authorization") authorization : String
    ):Call<largeList>

    @GET("/group/large")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_largeList():Call<Array<largeList>>


    @GET("/group/medium/{largeName}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_mediumList(
        @Path("largeName") largeName : String
    ):Call<Array<mediumList>>


    @GET("/group/small/{largeName}/{medium}")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_smallLiset(
        @Path("largeName") largeName : String,
        @Path("medium") medium : String,
    ):Call<Array<smallList>>

    @GET("/notice/group")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_notice(
        @Header("Authorization") authorization : String
    ):Call<Array<notice>>

    @GET("/record/user")
    @Headers(
        "accept: application/json",
        "content-type: application/json"
    )
    fun get_userRecord(
        @Header("Authorization") authorization : String
    ):Call<Array<R_ManagerVisit>>

    companion object { // static 처럼 공유객체로 사용가능함. 모든 인스턴스가 공유하는 객체로서 동작함.
        private const val BASE_URL = "http://211.104.70.143:8080"
        fun create(): APIS {

            val gson : Gson = GsonBuilder().setLenient().create()

            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(APIS::class.java)
        }
    }
}