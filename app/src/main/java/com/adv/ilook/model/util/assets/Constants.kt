package com.adv.ilook.model.util.assets

/**SharedPref */
object SharedPrefKey{
   const val TOKEN_KEY: String ="TOKEN_KEY"
    const val APP_LANGUAGE:String = "APP_LANGUAGE"
    const val APP_WEBLINK:String = "APP_WEBLINK"
    const val APP_USERID:String = "APP_USERID"
    const val APP_USERMAIL:String = "APP_USERMAIL"
    const val APP_USERLOGIN:String = "APP_USERLOGIN"
    const val APP_USERNAME:String = "APP_USERNAME"
    const val APP_USERPHONE:String = "APP_USERPHONE"
    const val APP_USERSTATUS:String = "APP_USERSTATUS"
    const val APP_SELECTED_DEVICES:String = "APP_SELECTED_DEVICES"
    const val APP_AVAILABLE_ROOMS:String = "APP_AVAILABLE_ROOMS"
    const val APP_SELECTED_ROOM:String = "APP_SELECTED_ROOM"
    const val APP_AVAILABLE_DEVICES:String = "APP_AVAILABLE_DEVICES"
    const val APP_ADV_WORKFLOW:String = "APP_ADV_WORKFLOW"
    const val APP_CONFIGURE_DEVICES:String = "APP_CONFIGURE_DEVICES"
    const val APP_SWITCH_STATUS:String = "APP_SWITCH_STATUS"
    const val APP_AVAILABLE_ROOMS_DOWNLOAD:String = "APP_AVAILABLE_ROOMS_DOWNLOAD"
}

object IntentKeys {}
object BundleKeys {

    const val USER_NAME_KEY:String = "USER_NAME_KEY"
    const val USER_PHONE_KEY:String = "USER_PHONE_KEY"
    const val LOGIN_OTP_KEY:String = "LOGIN_OTP_KEY"
    const val LOGIN_USER_TYPE_KEY:String = "LOGIN_USER_TYPE"
    const val LOGIN_USER_TYPE_VALUE:String = "LOGIN_USER_TYPE_VALUE"
}

object FirebaseKeys {
    const val user_name:String = "user_name"
    const val status:String = "status"
    const val call_event:String = "call_event"
    const val contacts:String = "contacts"
    const val latest_event:String = "latest_event"
    const val login:String = "login"
    const val workflow:String = "workflow"
    const val token:String = "token"
    const val missed_call:String = "missed_call"
    const val outgoing_call:String = "outgoing_call"
    const val call_history:String = "call_history"
}

object BReceiverKeys{
    const val CONNECTED_KEY:String = "CONNECTED_KEY"
}
object FileKeys{
   const val workflow_file_name="workflow.json"
}
object WorkManagerKeys {
    const val FILE_NAME_KEY="FILE_NAME_KEY"
    const val SHARED_PREF_KEY="SHARED_PREF_KEY"
    const val REMOTE_CONFIG_KEY="REMOTE_CONFIG_KEY"
    const val CALL_ACTIVITY_KEY="CALL_ACTIVITY_KEY"

}

object Debug{
    const val DEBUG_MODE:Boolean = true
}
object RemoteConfigMode{
    const val REMOTE_ENABLE:Boolean = true
}
enum class UserStatus {
 ONLINE,OFFLINE,UNREGISTER,IN_CALL
}
enum class LoginUserType {
    VISUALLY_IMPAIRED,ASSISTIVE
}

enum class WORK_TYPES {
    NOTIFY,WORKFLOW_LOCAL,WORKFLOW_REMOTE,ERROR
}