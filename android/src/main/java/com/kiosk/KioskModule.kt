package com.kiosk

import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import com.facebook.react.bridge.*
import com.facebook.react.bridge.UiThreadUtil.runOnUiThread

class KioskModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

  private val mDevicePolicyManager = reactContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

  private val mAdminComponentName = MyDeviceAdminReceiver.getComponentName(reactContext)

  private val isAdmin by lazy { mDevicePolicyManager.isDeviceOwnerApp(reactApplicationContext.packageName) }

  init {
    reactContext.addLifecycleEventListener(this)
  }

  override fun getName(): String {
    return NAME
  }

  @ReactMethod
  fun enable(promise: Promise) {
    setKioskPolicies(true)
    promise.resolve(true)
  }

  @ReactMethod
  fun disable(promise: Promise) {
    setKioskPolicies(false)
    promise.resolve(true)
  }

  private fun setUserRestriction(restriction: String, enable: Boolean) {
    if (enable) {
      mDevicePolicyManager.addUserRestriction(mAdminComponentName, restriction)
    } else {
      mDevicePolicyManager.clearUserRestriction(mAdminComponentName, restriction)
    }
  }

  private fun setKioskPolicies(enable: Boolean) {
    if (isAdmin) {
      setRestrictions(enable)
      enableStayOnWhilePluggedIn(enable)
      setUpdatePolicy(enable)
      setAsHomeApp(enable)
      setKeyGuardEnabled(enable)
    }
    setLockTask(enable)
    setImmersiveMode(enable)
  }

  private fun setRestrictions(enable: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, enable)
      // setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, false)
    }
    // setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, false)
    setUserRestriction(UserManager.DISALLOW_ADD_USER, enable)
    setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, enable)
    setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, enable)
  }

  private fun enableStayOnWhilePluggedIn(enable: Boolean) {
    if (enable) {
      mDevicePolicyManager.setGlobalSetting(
        mAdminComponentName,
        Settings.Global.STAY_ON_WHILE_PLUGGED_IN,
        (BatteryManager.BATTERY_PLUGGED_AC
          or BatteryManager.BATTERY_PLUGGED_USB
          or BatteryManager.BATTERY_PLUGGED_WIRELESS).toString()
      )
    } else {
      mDevicePolicyManager.setGlobalSetting(mAdminComponentName,
        Settings.Global.STAY_ON_WHILE_PLUGGED_IN, "0")
    }
  }

  private fun setUpdatePolicy(enable: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    if (enable) {
      mDevicePolicyManager.setSystemUpdatePolicy(
        mAdminComponentName,
        SystemUpdatePolicy.createWindowedInstallPolicy(60, 120)
      )
    } else {
      mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName, null)
    }
  }

  private fun setAsHomeApp(enable: Boolean) {
    val packageName = reactApplicationContext.packageName

    if (enable) {
      val intentFilter = IntentFilter(Intent.ACTION_MAIN)
      intentFilter.addCategory(Intent.CATEGORY_HOME)
      intentFilter.addCategory(Intent.CATEGORY_DEFAULT)

      val launchIntent = reactApplicationContext.packageManager.getLaunchIntentForPackage(packageName)
      val className = launchIntent?.component!!.className

      mDevicePolicyManager.addPersistentPreferredActivity(
        mAdminComponentName, intentFilter, ComponentName(packageName, className)
      )
    } else {
      mDevicePolicyManager.clearPackagePersistentPreferredActivities(
        mAdminComponentName, packageName
      )
    }
  }

  private fun setKeyGuardEnabled(enable: Boolean) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return
    mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, !enable)
  }

  private fun setLockTask(enable: Boolean) {
    val packageName = reactApplicationContext.packageName

    if (isAdmin) {
      mDevicePolicyManager.setLockTaskPackages(
        mAdminComponentName, if (enable) arrayOf(packageName) else arrayOfNulls(0)
      )
    }
    if (enable) {
      reactApplicationContext.currentActivity?.startLockTask()
    } else {
      reactApplicationContext.currentActivity?.stopLockTask()
    }
  }

  private fun setImmersiveMode(enable: Boolean) {
    setFullscreen(enable)
  }

  private fun setFullscreen(enable: Boolean) {
    runOnUiThread(Runnable {
      val window = reactApplicationContext.currentActivity?.window
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        if (enable) {
          window?.attributes?.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        } else {
          window?.attributes?.layoutInDisplayCutoutMode =
            WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
        }
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        if (enable) {
          window?.setDecorFitsSystemWindows(false)
          window?.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
          }
        } else {
          window?.setDecorFitsSystemWindows(true)
        }
      } else {
        if (enable) {
          window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_IMMERSIVE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        } else {
          window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
      }
    })
  }

  // region LifecycleEventListener

  override fun onHostResume() {

  }

  override fun onHostPause() {

  }

  override fun onHostDestroy() {
  }

  // endregion

  companion object {
    const val NAME = "Kiosk"
  }
}
