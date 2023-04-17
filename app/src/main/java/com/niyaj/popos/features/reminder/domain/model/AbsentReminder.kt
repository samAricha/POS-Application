package com.niyaj.popos.features.reminder.domain.model

import com.niyaj.popos.features.reminder.domain.util.ReminderType
import com.niyaj.popos.util.Constants.ABSENT_REMINDER_ID
import com.niyaj.popos.util.Constants.ABSENT_REMINDER_NAME
import com.niyaj.popos.util.closingTime
import com.niyaj.popos.util.openingTime
import java.util.concurrent.TimeUnit

data class AbsentReminder(

    val absentRemId: String = ABSENT_REMINDER_ID,

    val reminderName: String = ABSENT_REMINDER_NAME,

    val reminderStartTime: String = openingTime,

    val reminderEndTime: String = closingTime,

    val reminderInterval: Int = 16,

    val reminderIntervalTimeUnit: String = TimeUnit.MINUTES.name,

    val reminderType: String = ReminderType.Attendance.reminderType,

    val isRepeatable: Boolean = true,

    val isCompleted: Boolean = false,

    val updatedAt: String = "",
)

internal fun AbsentReminder.toReminder(): Reminder {
    return Reminder(
        reminderId = this.absentRemId,
        reminderName = this.reminderName,
        reminderStartTime = this.reminderStartTime,
        reminderEndTime = this.reminderEndTime,
        reminderInterval = this.reminderInterval,
        reminderIntervalTimeUnit = this.reminderIntervalTimeUnit,
        reminderType = this.reminderType,
        isRepeatable = this.isRepeatable,
        isCompleted = this.isCompleted,
        updatedAt = System.currentTimeMillis().toString()
    )
}