package com.adv.ilook.model.data.workflow

import com.google.gson.annotations.SerializedName

data class Workflow(

	@field:SerializedName("test")
	val test: Test? = null,

	@field:SerializedName("screens")
	val screens: Screens? = null,

	@field:SerializedName("mqtt")
	val mqtt: Mqtt? = null
)

data class ToastView(

	@field:SerializedName("login_success")
	val loginSuccess: LoginSuccess? = null,

	@field:SerializedName("login_failure")
	val loginFailure: LoginFailure? = null,

	@field:SerializedName("loading")
	val loading: Loading? = null
)

data class Test(

	@field:SerializedName("phone")
	val phone: List<PhoneItem?>? = null
)

data class Loading(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class CallHistoryScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Validation(

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class DisagreeBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class OutputScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class VideoCallScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Image(

	@field:SerializedName("background_image")
	val backgroundImage: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class ViBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class Screens(

	@field:SerializedName("commodity_classification_screen")
	val commodityClassificationScreen: CommodityClassificationScreen? = null,

	@field:SerializedName("video_call_screen")
	val videoCallScreen: VideoCallScreen? = null,

	@field:SerializedName("contacts_screen")
	val contactsScreen: ContactsScreen? = null,

	@field:SerializedName("ocr_screen")
	val ocrScreen: OcrScreen? = null,

	@field:SerializedName("select_screen_type")
	val selectScreenType: SelectScreenType? = null,

	@field:SerializedName("voice_translator_screen")
	val voiceTranslatorScreen: VoiceTranslatorScreen? = null,

	@field:SerializedName("see_for_me_screen")
	val seeForMeScreen: SeeForMeScreen? = null,

	@field:SerializedName("ai_navigator_screen")
	val aiNavigatorScreen: AiNavigatorScreen? = null,

	@field:SerializedName("output_screen")
	val outputScreen: OutputScreen? = null,

	@field:SerializedName("logout_screen")
	val logoutScreen: LogoutScreen? = null,

	@field:SerializedName("login_screen")
	val loginScreen: LoginScreen? = null,

	@field:SerializedName("call_logs_screen")
	val callLogsScreen: CallLogsScreen? = null,

	@field:SerializedName("otp_screen")
	val otpScreen: OtpScreen? = null,

	@field:SerializedName("text_to_speech_screen")
	val textToSpeechScreen: TextToSpeechScreen? = null,

	@field:SerializedName("splash_screen")
	val splashScreen: SplashScreen? = null,

	@field:SerializedName("device_control_screen")
	val deviceControlScreen: DeviceControlScreen? = null,

	@field:SerializedName("object_detection_screen")
	val objectDetectionScreen: ObjectDetectionScreen? = null,

	@field:SerializedName("home_screen")
	val homeScreen: HomeScreen? = null,

	@field:SerializedName("call_history_screen")
	val callHistoryScreen: CallHistoryScreen? = null,

	@field:SerializedName("instruction_screen")
	val instructionScreen: InstructionScreen? = null
)

data class CallLogsScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class UserName(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class OtpCode(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null,

	@field:SerializedName("helper_text_color")
	val helperTextColor: String? = null
)

data class CallStatus(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class ImageView(

	@field:SerializedName("icon")
	val icon: Icon? = null
)

data class ObjectDetectionScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class LeftIcon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class SelectScreenItem(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class TextView(

	@field:SerializedName("check_box")
	val checkBox: CheckBox? = null,

	@field:SerializedName("terms_of_use_text")
	val termsOfUseText: TermsOfUseText? = null,

	@field:SerializedName("header")
	val header: Header? = null,

	@field:SerializedName("call_status")
	val callStatus: CallStatus? = null,

	@field:SerializedName("temperature")
	val temperature: Temperature? = null,

	@field:SerializedName("user_name")
	val userName: UserName? = null,

	@field:SerializedName("mobile_number")
	val mobileNumber: MobileNumber? = null,

	@field:SerializedName("vi_mode_text")
	val viModeText: ViModeText? = null,

	@field:SerializedName("guide_mode_text")
	val guideModeText: GuideModeText? = null,

	@field:SerializedName("otp_code")
	val otpCode: OtpCode? = null
)

data class ViModeText(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class InputTextLength(

	@field:SerializedName("max_characters")
	val maxCharacters: Int? = null,

	@field:SerializedName("min_characters")
	val minCharacters: Int? = null
)

data class CallButton(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null
)

data class HomeScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Properties(

	@field:SerializedName("image")
	val image: Image? = null,

	@field:SerializedName("color")
	val color: Color? = null
)

data class Views(

	@field:SerializedName("button_view")
	val buttonView: ButtonView? = null,

	@field:SerializedName("image_view")
	val imageView: ImageView? = null,

	@field:SerializedName("toast_view")
	val toastView: ToastView? = null,

	@field:SerializedName("text_view")
	val textView: TextView? = null,

	@field:SerializedName("menu_view")
	val menuView: List<MenuViewItem?>? = null
)

data class GenerateOtp(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class LoginScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Header(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("helper_text_color")
	val helperTextColor: String? = null
)

data class LoginFailure(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class ButtonView(

	@field:SerializedName("disagree_btn")
	val disagreeBtn: DisagreeBtn? = null,

	@field:SerializedName("agree_btn")
	val agreeBtn: AgreeBtn? = null,

	@field:SerializedName("call_button")
	val callButton: CallButton? = null,

	@field:SerializedName("login")
	val login: Login? = null,

	@field:SerializedName("vi_btn")
	val viBtn: ViBtn? = null,

	@field:SerializedName("guide_btn")
	val guideBtn: GuideBtn? = null,

	@field:SerializedName("generate_otp")
	val generateOtp: GenerateOtp? = null,

	@field:SerializedName("verify_otp")
	val verifyOtp: VerifyOtp? = null
)

data class MenuViewItem(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null,

	@field:SerializedName("menu_name")
	val menuName: String? = null
)

data class CheckBox(
	val any: Any? = null
)

data class GuideModeText(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class CommodityClassificationScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class AiNavigatorScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class DeviceControlScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class Color(

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("secondary_color")
	val secondaryColor: String? = null,

	@field:SerializedName("primary_color")
	val primaryColor: String? = null
)

data class GuideBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class LoginSuccess(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class TermsOfUseText(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class Icon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class OcrScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class AgreeBtn(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class ContactsScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class VoiceTranslatorScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class RightIcon(

	@field:SerializedName("color")
	val color: String? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("width")
	val width: Int? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("height")
	val height: Int? = null
)

data class SeeForMeScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Temperature(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class TextToSpeechScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null
)

data class SplashScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<Any?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class LogoutScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class Mqtt(

	@field:SerializedName("enable")
	val enable: Boolean? = null
)

data class MobileNumber(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("helper_text")
	val helperText: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class PhoneItem(

	@field:SerializedName("number")
	val number: String? = null,

	@field:SerializedName("name")
	val name: String? = null
)

data class Login(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class InstructionScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class OtpScreen(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("properties")
	val properties: Properties? = null,

	@field:SerializedName("views")
	val views: Views? = null
)

data class VerifyOtp(

	@field:SerializedName("text_size")
	val textSize: Int? = null,

	@field:SerializedName("input_text_length")
	val inputTextLength: InputTextLength? = null,

	@field:SerializedName("enable")
	val enable: Boolean? = null,

	@field:SerializedName("right_icon")
	val rightIcon: RightIcon? = null,

	@field:SerializedName("text")
	val text: String? = null,

	@field:SerializedName("text_color")
	val textColor: String? = null,

	@field:SerializedName("left_icon")
	val leftIcon: LeftIcon? = null,

	@field:SerializedName("validation")
	val validation: Validation? = null
)

data class SelectScreenType(

	@field:SerializedName("select_screen")
	val selectScreen: List<SelectScreenItem?>? = null,

	@field:SerializedName("next_screen")
	val nextScreen: String? = null,

	@field:SerializedName("current_screen")
	val currentScreen: String? = null,

	@field:SerializedName("previous_screen")
	val previousScreen: String? = null,

	@field:SerializedName("views")
	val views: Views? = null
)
