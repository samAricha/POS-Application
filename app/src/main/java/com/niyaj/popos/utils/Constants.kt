package com.niyaj.popos.utils

import com.niyaj.popos.R

object Constants {

    // github login token - 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

    const val SPLASH_SCREEN_DURATION = 100

    const val PRINTER_DPI = 176

    const val PRINTER_WIDTH_MM = 58f

    const val PRINTER_NBR_LINE = 31

    const val PAYMENT_QR_DATA = "upi://pay?pa=paytmqr281005050101zry6uqipmngr@paytm&pn=Paytm%20Merchant&paytmqr=281005050101ZRY6UQIPMNGR"

    const val PRODUCT_NAME_LENGTH = 18

    const val PRINT_PRODUCT_WISE_REPORT_LIMIT = 30

    const val PRINT_ADDRESS_WISE_REPORT_LIMIT = 15

    const val PRINT_CUSTOMER_WISE_REPORT_LIMIT = 15

    const val JSON_FILE_TYPE = "application/json"

    const val JSON_FILE_EXTENSION = ".json"

    const val SAVEABLE_FILE_NAME = "popos"

    const val PAID = "Paid"

    const val NOT_PAID = "Not Paid"

    const val DELETE_DATA_NOTIFICATION_CHANNEL_ID = "delete_data"
    const val DELETE_DATA_NOTIFICATION_CHANNEL_NAME = "Data Deletion"
    const val DELETE_DATA_INTERVAL_HOUR: Long = 15

    const val GENERATE_REPORT_CHANNEL_ID = "generate_report"
    const val GENERATE_REPORT_CHANNEL_NAME = "Generating Report"
    const val GENERATE_REPORT_INTERVAL_HOUR: Long = 1

    const val SELECTED_CART_ORDER_ID = "33333333"
    const val SETTINGS_ID = "11111111"
    const val RESTAURANT_ID = "22222222"

    const val RESTAURANT_NAME = "Popos Highlight"
    const val RESTAURANT_TAGLINE = "- Pure And Tasty -"
    const val RESTAURANT_DESCRIPTION = "Multi Cuisine Veg & Non-Veg Restaurant"
    const val RESTAURANT_EMAIL = "poposhighlight@gmail.com"
    const val RESTAURANT_SECONDARY_PHONE: String = "9597185001"
    const val RESTAURANT_PRIMARY_PHONE: String = "9500825077"
    const val RESTAURANT_ADDRESS = "Chinna Seeragapadi, Salem, TamilNadu, India 636308, Opp. of VIMS Hospital"
    const val RESTAURANT_LOGO = R.drawable.reslogo.toString()

    //Common Constants
    const val SEARCH_ITEM_NOT_FOUND = "Searched Item Not Found"

    //Dialog positive button test tag
    const val POSITIVE_BUTTON = "positive"
    const val NEGATIVE_BUTTON = "negative"

    const val STANDARD_SEARCH_BAR = "Standard Search Bar"
    const val STANDARD_BACK_BUTTON = "Standard Back Button"
    const val SEARCH_BAR_CLEAR_BUTTON = "SearchBar Close Button"

    const val STANDARD_BOTTOM_SHEET = "BottomSheet"
    const val STANDARD_BOTTOM_SHEET_CLOSE_BTN = "Standard Bottom Close Button"

    const val SORT_ASCENDING = "Sort Ascending"
    const val SORT_DESCENDING = "Sort Descending"

    const val ABSENT_REMINDER_NOTE = "Selected employees will be mark as absent."
    const val DAILY_SALARY_REMINDER_NOTE = "Selected employees will be mark as paid."

    const val ABSENT_REMINDER_ID = "EA889977"
    const val ABSENT_REMINDER_NAME = "Employee Attendance"
    const val ABSENT_REMINDER_TITLE = "Did You Marked Employee Attendance?"
    const val ABSENT_REMINDER_TEXT = "Don't forget to mark absent employees."

    const val DAILY_SALARY_REMINDER_ID = "DAL907856"
    const val DAILY_SALARY_REMINDER_NAME = "Daily Salary Reminder"
    const val DAILY_SALARY_REMINDER_TITLE = "Did You Paid Employee Salary?"
    const val DAILY_SALARY_REMINDER_TEXT = "Don't forget to add salary entries."

    private const val HOST = "http://skniyajali.me/"
    private const val HOST_SECURE = "https://skniyajali.me/"

    const val SALARY_HOST = "${HOST}reminder/reminder_id=$DAILY_SALARY_REMINDER_ID"
    const val SALARY_HOST_SECURE = "${HOST_SECURE}reminder/reminder_id=$DAILY_SALARY_REMINDER_ID"

    const val ABSENT_HOST = "${HOST}reminder/reminder_id=$ABSENT_REMINDER_ID"
    const val ABSENT_HOST_SECURE = "${HOST_SECURE}reminder/reminder_id=$ABSENT_REMINDER_ID"

    enum class ImportExportType {
        IMPORT,
        EXPORT
    }
}