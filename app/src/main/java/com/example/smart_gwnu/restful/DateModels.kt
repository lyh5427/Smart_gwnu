package com.example.smart_gwnu.restful

import java.time.LocalDateTime
import kotlin.collections.ArrayList


data class HTTP_GET_Model(
    var something : String? =null,
    var users : ArrayList<UserModel>? =null
)

data class UserJoin(
    var username : String,
    var large : String,
    var medium : String,
    var small : String
)

data class R_UserJoin(
    var answer : String
)

data class UserModel(
    var idx : Int? =null ,
    var id : String?=null,
    var nick : String? =null
)


data class UserLogin(
    var username : String,
    var password : String
)

data class R_UserLogin(
    var token : String
)

data class Saveinfo( /*방문기록 저장*/
    var uuid : String,
    var major : String,
    var minor : String,
    var createdAt : String,
    var isEnter : Boolean
)

data class R_SvaeInfo( //방문기록 응답
    var buildingid : Long?,
    var lastNoticeld : Long?
)

data class R_UserVisit( //사용자 방문기록 응답
    var building : String,
    var time : LocalDateTime
)

data class Group(
    var large : String,
    var medium : String,
    var small : String
)

data class R_userSet(//요청은 헤더만 mypage정보 응답
    var username : String,
    var role : String,
    var group : Group
)

data class A( //요청은 헤더만(관리자)
    var id : Long,
    var name : String
)

data class R_ManagerVisit(//관리자 방문기록 응답
    var username : String,
    var buildingName : String,
    var createdAt : String,
    var isEnter: Boolean
)

data class R_noticeInfo(
    var id : Long,
    var title : String,
    var content : String,
    var isPublic : Boolean
)

data class largeList(
    var largeName : String
)

data class mediumList(
    var mediumName: String
)

data class smallList(
    var id : Long,
    var smallName : String
)

data class creatBoard(
    var title : String,
    var content : String,
    var isPublic : Boolean,
    var groupIds : ArrayList<Long>
)

data class notice(
    var id : Long,
    var title : String,
    var content : String,
    var isPublic : Boolean,
    var createdAt: String,
    var updatedAt : String,
    var userId : Long
)

data class graph(
    var startDate : String,
    var endDate : String,
    var total : Int,
    var containTotal : Int,
    var notContatinTotla : Int,
    var dayList : Array<daily>,
)

data class daily(
    var day : String,
    var daytotal : Int,
    var dayContainTotal : Int,
    var dayNotContainTotal : Int,
    var dayGroupList : Array<dailyGroupList>
)

data class dailyGroupList(
    var group : G,
    var dayGroupTotal : Int
)

data class G(
    var id : Long,
    var largName : String,
    var mediumName : String,
    var smallName : String
)

data class post_Graph(
    var buildingId : Long,
    var startDate : String,
    var endDate : String
)