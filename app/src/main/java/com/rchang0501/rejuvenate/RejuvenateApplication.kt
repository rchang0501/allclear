package com.rchang0501.rejuvenate

import android.app.Application
import com.rchang0501.rejuvenate.data.ReminderRoomDatabase

class RejuvenateApplication: Application() {

    // instantiates the database instance
    val database: ReminderRoomDatabase by lazy { ReminderRoomDatabase.getDatabase(this) }
}