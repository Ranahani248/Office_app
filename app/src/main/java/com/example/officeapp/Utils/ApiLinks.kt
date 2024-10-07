package com.example.officeapp.Utils

class ApiLinks {

    companion object {
        const val BASE_URL = "https://office-flare.sftester.pw/"
//       const val BASE_URL = "http://192.168.18.29:8001/"

        const val LOGIN_URL = BASE_URL + "api/v1/employee/login"
        const val PROFILE_URL = BASE_URL + "api/v1/employee/profile"
        const val ATTENDANCE_STATUS_URL = BASE_URL + "api/v1/employee/attendance"
        const val ATTENDANCE_IN_URL = BASE_URL + "api/v1/employee/attendance/in"
        const val ATTENDANCE_OUT_URL = BASE_URL + "api/v1/employee/attendance/off"
        const val APPLY_LEAVAES_URL = BASE_URL + "api/v1/employee/attendance/leave"
        const val HOLIDAYS_URL = BASE_URL + "api/v1/employee/holidays"
        const val ANNOUNCEMENT_URL = BASE_URL + "api/v1/employee/announcements"

    }
}