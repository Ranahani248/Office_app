package com.example.officeapp.Utils

class ApiLinks {

    companion object {
        const val BASE_URL = "https://office-flare.sftester.pw/"
//       const val BASE_URL = "http://192.168.18.29:8001/"

        const val LOGIN_URL = BASE_URL + "api/v1/employee/login"
        const val PROFILE_URL = BASE_URL + "api/v1/employee/profile"
        const val ATTENDANCE_URL = BASE_URL + "api/v1/employee/attendance"
    }
}