/*
 * Copyright (c) 2012-2018 Frederic Julian
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http:></http:>//www.gnu.org/licenses/>.
 */

package wtf.moonlight.flym

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import net.fred.feedex.BuildConfig
import net.frju.flym.data.AppDatabase
import net.frju.flym.data.utils.PrefConstants
import net.frju.flym.utils.putPrefBoolean
import timber.log.Timber
import wtf.moonlight.flym.worker.FetchFeedsWorker
import javax.inject.Inject

@HiltAndroidApp
class FlymApplication : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    companion object {
        @SuppressLint("StaticFieldLeak")
        @JvmStatic
        @Deprecated("Use dependency injection instead")
        lateinit var context: Context
            private set

        @JvmStatic
        @Deprecated("Use dependency injection instead")
        lateinit var db: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        WorkManager.getInstance(this).enqueue(OneTimeWorkRequestBuilder<FetchFeedsWorker>().build())

        context = applicationContext
        db = AppDatabase.createDatabase(context)

        context.putPrefBoolean(PrefConstants.IS_REFRESHING, false) // init
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}