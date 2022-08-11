package com.ntu.hero.sql.joiner

//data class ChatTabList(
//        @Embedded
//        var chatList: ChatList,
//        @Embedded
//        var contactRoster: ContactRoster,
//        @Embedded
//        var message: Message
//
//) {
//    constructor() : this(ChatList(), ContactRoster(), Message())
//
//    // get today's time or date
//    fun getMsgTimeDate(): String {
//
//        return UIHelper().dateLongToStrToday(chatList.chatDate!!)
//    }
//}
//
//@BindingAdapter("android:textStyle")
//fun setTypeface(v: TextView, needBold: Boolean) {
//    if (needBold) {
//        v.setTypeface(null, Typeface.BOLD)
//        v.setTextColor(Color.BLACK)
//    } else {
//        v.setTypeface(null, Typeface.NORMAL)
//    }
//}