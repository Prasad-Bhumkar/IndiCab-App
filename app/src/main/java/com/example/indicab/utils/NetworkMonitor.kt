 package com.example.indicab.utils
 
 import android.content.Context
 import android.net.ConnectivityManager
 import android.net.Network
 import android.net.NetworkCapabilities
 import android.net.NetworkRequest
 import kotlinx.coroutines.channels.awaitClose
 import kotlinx.coroutines.flow.Flow
 import kotlinx.coroutines.flow.callbackFlow
 import javax.inject.Inject
 import javax.inject.Singleton
 
 @Singleton
 class NetworkMonitor @Inject constructor(
     private val context: Context
 ) {
     val isOnline: Flow<Boolean> = callbackFlow {
         val connectivityManager = context.getSystemService(
             Context.CONNECTIVITY_SERVICE
         ) as ConnectivityManager
 
         val callback = object : ConnectivityManager.NetworkCallback() {
             override fun onAvailable(network: Network) {
                 trySend(true)
             }
 
             override fun onLost(network: Network) {
                 trySend(false)
             }
         }
 
         val request = NetworkRequest.Builder()
             .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
             .build()
         
         connectivityManager.registerNetworkCallback(request, callback)
         
         awaitClose {
             connectivityManager.unregisterNetworkCallback(callback)
         }
     }
 }
