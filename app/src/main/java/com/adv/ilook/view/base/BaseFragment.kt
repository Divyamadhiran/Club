package com.adv.ilook.view.base

import android.app.ActivityManager
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.viewbinding.ViewBinding
import com.adv.ilook.R
import com.adv.ilook.model.db.remote.firebase.remoteconfig.RemoteConfig
import com.adv.ilook.model.util.customview.WindowPreferencesManager
import com.adv.ilook.model.util.permissions.Permission
import com.adv.ilook.model.util.permissions.PermissionManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.properties.Delegates


private const val TAG = "==>>BaseFragment"

abstract class BaseFragment<VB : ViewBinding> : Fragment(), TextToSpeech.OnInitListener {


    @Inject
    lateinit var remoteConfig: RemoteConfig
    private lateinit var tts: TextToSpeech

    //View binding
    @Suppress("UNCHECKED_CAST")
    private var _binding: ViewBinding? = null
    protected val binding: VB get():VB = _binding as VB

    //Lifecycle and view model
    lateinit var observer: BaseLifecycleObserver
    private val _sharedModel: BaseViewModel by activityViewModels()
    val sharedModel get() = _sharedModel

    //Navigation and Fragment management
    private lateinit var navController: NavController
    protected open lateinit var navHostFragment: NavHostFragment

    //View binding inflater
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    //Context and permission listener
    lateinit var localeUpdatedContext: ContextWrapper
    lateinit var activityListener: PermissionListener

    //. Navigation IDs
    protected open var nextScreenId_1 by Delegates.notNull<Int>()//Non-primitive type -ensures these properties cannot be null and must be initialized before use.
    protected open var nextScreenId_2 by Delegates.notNull<Int>()
    protected open var previousScreenId by Delegates.notNull<Int>()

    //Abstract Setup Method
    abstract fun setup(savedInstanceState: Bundle?)

    //Edge to Edge preference
    protected fun shouldApplyEdgeToEdgePreference(): Boolean {
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach: ")
        activityListener = context as PermissionListener
    }

    // hide the soft keyboard (on-screen keyboard) on an Android device.
    fun FragmentActivity.hideKeyboard(view: android.view.View): Boolean {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        return imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (shouldApplyEdgeToEdgePreference()) {
            val windowPreferencesManager = WindowPreferencesManager(requireActivity())
            windowPreferencesManager.applyEdgeToEdgePreference(requireActivity().window)
        }
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        //  configureWorkflow()
        observer = BaseLifecycleObserver()
        lifecycle.addObserver(observer)
        lifecycleScope.launch(Dispatchers.Main) { tts = TextToSpeech(context, this@BaseFragment) }

    }

    private fun configureWorkflow() {
        // lifecycleScope.launch { _sharedModel.configureWorkflow(requireContext()) }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        Log.d(TAG, "onCreateView: ")
        createNavControl()
        _binding = bindingInflater.invoke(inflater, container, false)
        return requireNotNull(_binding).root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")
        requireActivity().enableEdgeToEdge()
        setup(savedInstanceState)

        // requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    // Function to hide NavigationBar
    @RequiresApi(Build.VERSION_CODES.R)
    private fun hideSystemUI() {
        val window = requireActivity().window

        // Make the status and navigation bars transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(
            window,
            window.decorView.findViewById(android.R.id.content)
        ).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true

            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    fun getNaviHostFragment(resId: Int = R.id.nav_host_fragment): NavHostFragment {
        navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(resId) as NavHostFragment
        Log.d(TAG, "getNaviHostFragment: navHostFragment =$navHostFragment")
        return navHostFragment
    }

    fun createNavControl(): NavController {
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        return navController
    }

    fun findFragmentById() {
        // Access the NavHostFragment
        val navHostFragment =
            requireActivity().supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // Access the NavController
        val navController = navHostFragment.navController
        val specificDestination = navController.graph.findNode(R.id.seeformeScreenFragment)
        Log.d(TAG, "findFragmentById: specificDestination =$specificDestination")
        // Set a listener to track navigation changes
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Log.d(
                TAG,
                "findFragmentById() called with: controller = $controller, destination = $destination, arguments = $arguments"
            )
            val currentFragment = getCurrentFragment(navHostFragment)
            Log.d(TAG, "findFragmentById: current =$currentFragment")
        }
    }

    private fun getCurrentFragment(navHostFragment: NavHostFragment): Fragment? {
        return navHostFragment.childFragmentManager.fragments.firstOrNull()
    }

    fun getFragmentNameByActionId(actionId: Int, callback: (String?) -> Unit) {
        lifecycleScope.launch {
            val navController = findNavController(requireActivity(), R.id.nav_host_fragment)
            val navGraph = navController.graph
            val action = navGraph.getAction(actionId)
            val destinationId = action?.destinationId
            val destination = destinationId?.let { navGraph.findNode(it) }
            val fragmentName = destination?.label?.toString()

            callback(fragmentName)
        }
    }

    fun nav(id: Int, bundle: Bundle = Bundle.EMPTY) {
        Log.d(TAG, "nav: $id")
        //(requireActivity() as NavigationHost).findNavControl()?.navigate(id, bundle)

        try {
            id.let {
                navController.navigate(it, bundle)
            }
        } catch (e: Exception) {
            Log.d(TAG, "nav: ${e.message}")
        }


    }

    protected fun findNavControl() =
        (requireActivity() as NavigationHost).findNavControl()

    protected fun hideNavigation(animate: Boolean = true) =
        (requireActivity() as NavigationHost).hideNavigation(animate)

    protected fun showNavigation(animate: Boolean = true) =
        (requireActivity() as NavigationHost).showNavigation(animate)

    fun View.showSnackbar(
        view: View,
        msg: String = "",
        length: Int = Snackbar.LENGTH_SHORT,
        actionMessage: CharSequence? = "Ok",
        action: (View) -> Unit,
        action2: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action2(this)
            }.show()
        } else {
            snackbar.show()
        }
        action(this)
    }

    open fun showMessage(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun getPermissionManager(): PermissionManager {
        return PermissionManager.from(this)
    }

    fun clearAllPermissions() {
        val manager = requireContext().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        manager.clearApplicationUserData()
    }

    fun checkHSPermissions() {
        getPermissionManager().request(Permission.HS_Permissions)
            .rationale("We require to demonstrate that we can request two permissions at once")
            .checkPermission { granted ->
                Log.d(TAG, "checkHSPermissions: $granted")
                if (granted) {
                    success("YES! Now I can access Storage and Location!")
                } else {
                    error("Still missing at least one permission :(")
                }
            }
    }


    private fun success(message: String = "") {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
            .withColor(Color.parseColor("#09AF00"))
            .show()
    }

    private fun error(message: String) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
            .withColor(Color.parseColor("#B00020"))
            .show()
    }

    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar {
        this.view.setBackgroundColor(colorInt)
        return this
    }


    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        lifecycle.removeObserver(observer)
        callback.remove()
        _binding = null

    }

    // Create an OnBackPressedCallback to handle the back button event
    private val callback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isEnabled = true
            findNavControl()?.run {

            }
        }
    }

    /**speech from text content*/

    fun sendTextForSpeech(
        text: String,
        volume: Float = 0.9f,
        pan: Float = 0.0f,
        stream: Float = 1.0f,
        pitch: Float = 1.0f,
        rate: Float = 1.1f,
        language: String = "en",
        country: String = "US"
    ) {
        sharedModel.sendTextForSpeech(
            tts,
            text,
            volume,
            pan,
            stream,
            pitch,
            rate,
            language,
            country
        )
    }

    override fun onInit(status: Int) {
        sharedModel.onInitSpeech(status)
    }

    fun getIPAdd(callback: (String, WifiManager) -> Unit) {
        binding.apply {
            val wifiManager: WifiManager =
                context?.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val connectivityManager =
                context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = connectivityManager.activeNetwork
            val linkProperties = connectivityManager.getLinkProperties(activeNetwork)
            val ipV4Address =
                linkProperties?.linkAddresses?.firstOrNull { it.address.isAnyLocalAddress }?.address?.hostAddress
            if (ipV4Address != null && ipV4Address != "0.0.0.0") {
                // Use ipV4Address here
                val text = "ip-" + ipV4Address
                callback(text, wifiManager)
            } else {
                // No IP address found
                val text =
                    "ip-" + android.text.format.Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
                callback(text, wifiManager)
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        if (::tts.isInitialized)
            sharedModel.onDestroySpeech(tts)
    }




}