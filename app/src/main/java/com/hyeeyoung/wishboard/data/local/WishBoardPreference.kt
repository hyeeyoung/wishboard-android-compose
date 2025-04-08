package com.hyeeyoung.wishboard.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.hyeeyoung.wishboard.BuildConfig
import com.hyeeyoung.wishboard.data.util.getBase64Json
import com.hyeeyoung.wishboard.domain.model.user.UserInfo
import com.hyeeyoung.wishboard.presentation.util.extension.toBase64Json
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WishBoardPreference @Inject constructor(@ApplicationContext context: Context) {
    private val masterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val dataStore: SharedPreferences =
        if (BuildConfig.DEBUG) {
            context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
        } else {
            EncryptedSharedPreferences.create(
                context,
                FILE_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
            )
        }

    var userInfo: UserInfo
        set(value) = dataStore.edit { putString(USER_INFO, value.toBase64Json()) }
        get() = dataStore.getBase64Json<UserInfo?>(USER_INFO) ?: UserInfo()

    var accessToken: String
        set(value) = dataStore.edit { putString(ACCESS_TOKEN, value) }
        get() = dataStore.getString(
            ACCESS_TOKEN,
            null,
        ) ?: ""

    var refreshToken: String
        set(value) = dataStore.edit { putString(REFRESH_TOKEN, value) }
        get() = dataStore.getString(
            REFRESH_TOKEN,
            null,
        ) ?: ""

    var isLogin: Boolean
        set(value) = dataStore.edit { putBoolean(IS_LOGIN, value) }
        get() = dataStore.getBoolean(IS_LOGIN, false)

    var hasShownNotificationAlert: Boolean
        set(value) = dataStore.edit { putBoolean(HAS_SHOWN_NOTIFICATION_ALERT, value) }
        get() = dataStore.getBoolean(HAS_SHOWN_NOTIFICATION_ALERT, false)

    var shouldShowOnboardingModal: Boolean
        set(value) = dataStore.edit { putBoolean(SHOULD_SHOW_ONBOARDING_MODAL, value) }
        get() = dataStore.getBoolean(SHOULD_SHOW_ONBOARDING_MODAL, false)

    fun setUserInfo(email: String, nickname: String?, accessToken: String, refreshToken: String) {
        isLogin = true
        userInfo = UserInfo(
            email = email,
            nickname = nickname ?: "",
        )
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun updateToken(accessToken: String, refreshToken: String) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
    }

    fun clear() {
        val tempNotificationAlertState = hasShownNotificationAlert
        dataStore.edit {
            clear()
        }
        hasShownNotificationAlert = tempNotificationAlertState
    }

    fun clear(key: String) {
        dataStore.edit {
            remove(key).apply()
        }
    }

    companion object {
        const val FILE_NAME = "wishboardPreferences"
        const val ACCESS_TOKEN = "accessToken"
        const val REFRESH_TOKEN = "refreshToken"
        const val IS_LOGIN = "isLogin"
        const val USER_INFO = "userInfo"
        const val HAS_SHOWN_NOTIFICATION_ALERT = "hasShownNotificationAlert"
        const val SHOULD_SHOW_ONBOARDING_MODAL = "shouldShowOnboardingModal"
    }
}

//모달 노출 필요 여부를 로컬 db에 저장해서 앱 재진입 시에도 대응하겠습니다!
//• 회원가입 완료 후 “네, 알겠어요” 버튼 클릭 전까지 노출 필요 여부를 true로 함
//• "네, 알겠어요" 버튼 클릭 시 노출 필요 여부 앱 데이터 제거(일은  데이터라 더이상 필요 없다면 데이터 제거할 것임.)
//• true라면 앱 재진입 시 온보딩 모달 노출함
