<<<<<<< HEAD
 package com.example.indicab.api
 
 data class ApiResponse<T>(
     val success: Boolean,
     val data: T?,
     val error: String?
 ) 
=======
package com.example.indicab.api

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val error: String? = null,
    val errorMessage: String? = null // New property for detailed error messages
) {
    fun isSuccessful(): Boolean {
        return success && error == null
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
