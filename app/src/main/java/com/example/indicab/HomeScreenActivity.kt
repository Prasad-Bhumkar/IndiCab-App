<<<<<<< HEAD
 package com.example.indicab
 
 import android.app.Dialog
 import android.graphics.Color
 import android.graphics.drawable.ColorDrawable
 import android.os.Bundle
 import android.view.View
 import android.view.Window
 import android.widget.Toast
 import androidx.appcompat.app.AppCompatActivity
 import androidx.lifecycle.ViewModelProvider
 import androidx.recyclerview.widget.LinearLayoutManager
 import com.example.indicab.adapters.CarTypesAdapter
 import com.example.indicab.components.PlacesAutocomplete
 import com.example.indicab.components.EnhancedDateTimePicker
 import com.example.indicab.databinding.FareDetailsModalBinding
 import com.example.indicab.databinding.ActivityHomeScreenBinding
 import com.example.indicab.databinding.DateTimeSelectorDialogBinding
 import com.example.indicab.models.BookingRequest
 import com.example.indicab.models.CarType
 import com.example.indicab.models.LatLng
 import androidx.compose.ui.platform.ComposeView
 import androidx.compose.ui.platform.ViewCompositionStrategy
 import androidx.compose.ui.platform.ComposeView
 import androidx.compose.material3.MaterialTheme
 import androidx.compose.material3.Surface
 import com.example.indicab.models.FareDetails
 import com.example.indicab.viewmodels.HomeViewModel
 import java.util.Date
 import java.time.LocalTime
 import java.time.format.DateTimeFormatter
 import java.util.Locale
 
 class HomeScreenActivity : AppCompatActivity() {
 
     private lateinit var binding: ActivityHomeScreenBinding
     private lateinit var viewModel: HomeViewModel
      private lateinit var carTypesAdapter: CarTypesAdapter
     
     private var pickupLocation: Location? = null
     private var dropLocation: Location? = null
     private var selectedDate: Date = Date()
     private var tripType: String = "ONE_WAY"
     private var selectedCar: CarType? = null
     private var fareDetails: FareDetails? = null
     private var isPickup: Boolean = true
 
     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityHomeScreenBinding.inflate(layoutInflater)
         setContentView(binding.root)
         
         viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
         
         setupUI()
         setupObservers()
         loadCarTypes()
     }
     
     private fun setupUI() {
         // Initialize car types recycler view
         carTypesAdapter = CarTypesAdapter { car ->
             selectedCar = car
             updateButtonStates()
         }
         
         // Location Selectors
         binding.pickupLocationSelector.setOnClickListener {
             showLocationSelector(true)
         }
         
         binding.dropLocationSelector.setOnClickListener {
             showLocationSelector(false)
         }
         
         // DateTime Selector
         binding.dateTimeSelector.setOnClickListener {
             showDateTimePicker()
         }
         
         // Trip Type Selector
         binding.tripTypeSelector.setOnCheckedChangeListener { _, checkedId ->
             tripType = if (checkedId == R.id.oneWayTrip) "ONE_WAY" else "ROUND_TRIP"
         }
         
         // Setup car types list
         binding.carTypesList.apply {
             layoutManager = LinearLayoutManager(this@HomeScreenActivity)
             adapter = carTypesAdapter
         }
         
         // Button click listeners
         binding.calculateFareButton.setOnClickListener {
             calculateFare()
         }
         
         binding.bookNowButton.setOnClickListener {
             handleBooking()
         }
         
         // Initialize with disabled state
         updateButtonStates()
     }
     
     private fun setupObservers() {
         viewModel.loading.observe(this) { isLoading ->
             binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
             updateButtonStates()
         }
         
         viewModel.error.observe(this) { errorMessage ->
             errorMessage?.let {
                 binding.errorMessage.text = it
                 binding.errorMessage.visibility = View.VISIBLE
             } ?: run {
                 binding.errorMessage.visibility = View.GONE
             }
         }
         
         viewModel.carTypes.observe(this) { carTypes ->
             carTypesAdapter.submitList(carTypes)
         }
         
         viewModel.fareDetails.observe(this) { fare ->
             fareDetails = fare
             fare?.let { showFareDetailsModal(it) }
         }
         
         viewModel.bookingCreated.observe(this) { booking ->
             booking?.let {
                 navigateToBookingConfirmation(it)
             }
         }
     }
     
     private fun loadCarTypes() {
         viewModel.loadCarTypes()
     }
     
     private fun calculateFare() {
         pickupLocation?.let { pickup ->
             dropLocation?.let { drop ->
                 selectedCar?.let { car ->
                     viewModel.calculateFare(pickup, drop, car.id)
                 }
             }
         }
     }
     
     private fun handleBooking() {
         pickupLocation?.let { pickup ->
             dropLocation?.let { drop ->
                 selectedCar?.let { car ->
                     val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                     val dateTimeString = dateFormat.format(selectedDate)
                     
                     val bookingRequest = BookingRequest(
                         pickupLocation = pickup,
                         dropLocation = drop,
                         date = dateTimeString,
                         time = dateTimeString,
                         carType = car.id,
                         tripType = tripType,
                         passengers = car.capacity
                     )
                     
                     viewModel.createBooking(bookingRequest)
                 }
             }
         }
     }
     
     private fun showLocationSelector(isPickup: Boolean) {
         this.isPickup = isPickup
         val dialog = Dialog(this)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setContentView(R.layout.location_selector_dialog)
 
         val composeView: ComposeView = dialog.findViewById(R.id.compose_view)
         composeView.setContent {
             MaterialTheme {
                 Surface {
                     PlacesAutocomplete(
                         label = if (isPickup) "Select Pickup Location" else "Select Drop Location",
                         onPlaceSelected = { place ->
                             val location = Location(
                                 id = place.placeId,
                                 name = place.name,
                                 address = place.address,
                                 latitude = place.latLng.latitude,
                                 longitude = place.latLng.longitude
                             )
                             if (isPickup) pickupLocation = location else dropLocation = location
                             if (isPickup) binding.pickupLocationSelector.text = location.name else binding.dropLocationSelector.text = location.name
                             updateButtonStates()
                             dialog.dismiss()
                         }
                     )
                 }
             }
         }
         dialog.show()
     }
     
     private fun showDateTimePicker() {
        val dialog = Dialog(this)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         val dialogBinding = DateTimeSelectorDialogBinding.inflate(layoutInflater)
         dialog.setContentView(dialogBinding.root)
 
         dialogBinding.dateTimeComposeView.apply {
             setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
             setContent {
             MaterialTheme {
                     Surface {
                       var selectedDate = this@HomeScreenActivity.selectedDate
                         var selectedTime = LocalTime.now()
 
                         EnhancedDateTimePicker(
                                 selectedDate = selectedDate,
                                 selectedTime = selectedTime,
                                 onDateSelected = { newDate ->
                                     selectedDate = newDate
                                     updateDateTimeSelectorText(selectedDate)
                                     dialog.dismiss()
                                 },
                                 onTimeSelected = { newTime ->
                                     selectedTime = newTime
                                     updateDateTimeSelectorText(selectedDate)
                                 },
                             )
                         }
                 }
             }
         }
         dialog.show()
     }
 
     private fun updateDateTimeSelectorText(selectedDate: Date){
         val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
         binding.dateTimeSelector.text = dateFormat.format(selectedDate)
     }
 
 
     private fun showFareDetailsModal(fareDetails: FareDetails) {
         val dialogBinding = FareDetailsModalBinding.inflate(layoutInflater)
         dialogBinding.distanceFareValue.text = String.format("%.2f", fareDetails.distanceFare)
         dialogBinding.timeFareValue.text = String.format("%.2f", fareDetails.timeFare)
         dialogBinding.totalFareValue.text = String.format("%.2f", fareDetails.total)
 
 
         dialogBinding.closeModalButton.setOnClickListener {
            dialog.dismiss()
         }
 
         val dialog = Dialog(this)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setContentView(dialogBinding.root)
 
         }
         dialog.show()
     }
     
     private fun navigateToBookingConfirmation(booking: BookingRequest) {
         // In a real app, you would start a new activity with intent extras
         Toast.makeText(this, "Booking created successfully!", Toast.LENGTH_LONG).show()
         
         // Example of starting booking confirmation activity
         /*
         val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
             putExtra("BOOKING_ID", booking.id)
             putExtra("PICKUP", booking.pickupLocation.name)
             putExtra("DROP", booking.dropLocation.name)
             putExtra("DATE", booking.date)
             putExtra("CAR_TYPE", booking.carType)
             putExtra("FARE", fareDetails?.total ?: 0.0)
         }
         startActivity(intent)
         */
     }
     
     private fun updateButtonStates() {
         val isLoading = viewModel.loading.value == true
         val isFormComplete = pickupLocation != null && dropLocation != null && selectedCar != null
         
         binding.calculateFareButton.isEnabled = isFormComplete && !isLoading
         binding.bookNowButton.isEnabled = isFormComplete && !isLoading
     }
 }
=======
package com.example.indicab

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indicab.adapters.CarTypesAdapter
import com.example.indicab.components.PlacesAutocomplete
import com.example.indicab.components.EnhancedDateTimePicker
import com.example.indicab.databinding.FareDetailsModalBinding
import com.example.indicab.databinding.ActivityHomeScreenBinding
import com.example.indicab.databinding.DateTimeSelectorDialogBinding
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.LatLng
import com.example.indicab.models.Location
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.platform.ComposeView
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.indicab.models.FareDetails
import com.example.indicab.viewmodels.HomeViewModel
import java.util.Date
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var viewModel: HomeViewModel
     private lateinit var carTypesAdapter: CarTypesAdapter
    
    private var pickupLocation: Location? = null
    private var dropLocation: Location? = null
    private var selectedDate: Date = Date()
    private var tripType: String = "ONE_WAY"
    private var selectedCar: CarType? = null
    private var fareDetails: FareDetails? = null
    private var isPickup: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        
        setupUI()
        setupObservers()
        loadCarTypes()
    }
    
    private fun setupUI() {
        // Initialize car types recycler view
        carTypesAdapter = CarTypesAdapter { car ->
            selectedCar = car
            updateButtonStates()
        }
        
        // Location Selectors
        binding.pickupLocationSelector.setOnClickListener {
            showLocationSelector(true)
        }
        
        binding.dropLocationSelector.setOnClickListener {
            showLocationSelector(false)
        }
        
        // DateTime Selector
        binding.dateTimeSelector.setOnClickListener {
            showDateTimePicker()
        }
        
        // Trip Type Selector
        binding.tripTypeSelector.setOnCheckedChangeListener { _, checkedId ->
            tripType = if (checkedId == R.id.oneWayTrip) "ONE_WAY" else "ROUND_TRIP"
        }
        
        // Setup car types list
        binding.carTypesList.apply {
            layoutManager = LinearLayoutManager(this@HomeScreenActivity)
            adapter = carTypesAdapter
        }
        
        // Button click listeners
        binding.calculateFareButton.setOnClickListener {
            calculateFare()
        }
        
        binding.bookNowButton.setOnClickListener {
            handleBooking()
        }
        
        // Initialize with disabled state
        updateButtonStates()
    }
    
    private fun setupObservers() {
        viewModel.loading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            updateButtonStates()
        }
        
        viewModel.error.observe(this) { errorMessage ->
            errorMessage?.let {
                binding.errorMessage.text = it
                binding.errorMessage.visibility = View.VISIBLE
            } ?: run {
                binding.errorMessage.visibility = View.GONE
            }
        }
        
        viewModel.carTypes.observe(this) { carTypes ->
            carTypesAdapter.submitList(carTypes)
        }
        
        viewModel.fareDetails.observe(this) { fare ->
            fareDetails = fare
            fare?.let { showFareDetailsModal(it) }
        }
        
        viewModel.bookingCreated.observe(this) { booking ->
            booking?.let {
                navigateToBookingConfirmation(it)
            }
        }
    }
    
    private fun loadCarTypes() {
        viewModel.loadCarTypes()
    }
    
    private fun calculateFare() {
        pickupLocation?.let { pickup ->
            dropLocation?.let { drop ->
                selectedCar?.let { car ->
                    viewModel.calculateFare(pickup, drop, car.id)
                }
            }
        }
    }
    
    private fun handleBooking() {
        pickupLocation?.let { pickup ->
            dropLocation?.let { drop ->
                selectedCar?.let { car ->
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                    val dateTimeString = dateFormat.format(selectedDate)
                    
                    val bookingRequest = BookingRequest(
                        pickupLocation = pickup,
                        dropLocation = drop,
                        date = dateTimeString,
                        time = dateTimeString,
                        carType = car.id,
                        tripType = tripType,
                        passengers = car.capacity
                    )
                    
                    viewModel.createBooking(bookingRequest)
                }
            }
        }
    }
    
    private fun showLocationSelector(isPickup: Boolean) {
        this.isPickup = isPickup
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.location_selector_dialog)

        val composeView: ComposeView = dialog.findViewById(R.id.compose_view)
        composeView.setContent {
            MaterialTheme {
                Surface {
                    PlacesAutocomplete(
                        label = if (isPickup) "Select Pickup Location" else "Select Drop Location",
                        onPlaceSelected = { place ->
                            val location = Location(
                                id = place.placeId,
                                name = place.name,
                                address = place.address,
                                latitude = place.latLng.latitude,
                                longitude = place.latLng.longitude
                            )
                            if (isPickup) pickupLocation = location else dropLocation = location
                            if (isPickup) binding.pickupLocationSelector.text = location.name else binding.dropLocationSelector.text = location.name
                            updateButtonStates()
                            dialog.dismiss()
                        }
                    )
                }
            }
        dialog.show()
    }
    
    private fun showDateTimePicker() {
       val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dialogBinding = DateTimeSelectorDialogBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.dateTimeComposeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
            MaterialTheme {
                    Surface {
                      var selectedDate = this@HomeScreenActivity.selectedDate
                        var selectedTime = LocalTime.now()

                        EnhancedDateTimePicker(
                                selectedDate = selectedDate,
                                selectedTime = selectedTime,
                                onDateSelected = { newDate ->
                                    selectedDate = newDate
                                    updateDateTimeSelectorText(selectedDate)
                                    dialog.dismiss()
                                },
                                onTimeSelected = { newTime ->
                                    selectedTime = newTime
                                    updateDateTimeSelectorText(selectedDate)
                                },
                            )
                        }
                }
            }
        }
        dialog.show()
    }

    private fun updateDateTimeSelectorText(selectedDate: Date){
        val dateFormat = java.text.SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        binding.dateTimeSelector.text = dateFormat.format(selectedDate)
    }


    private fun showFareDetailsModal(fareDetails: FareDetails) {
        val dialogBinding = FareDetailsModalBinding.inflate(layoutInflater)
        dialogBinding.distanceFareValue.text = String.format("%.2f", fareDetails.distanceFare)
        dialogBinding.timeFareValue.text = String.format("%.2f", fareDetails.timeFare)
        dialogBinding.totalFareValue.text = String.format("%.2f", fareDetails.total)


        dialogBinding.closeModalButton.setOnClickListener {
           dialog.dismiss()
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(dialogBinding.root)

        }
        dialog.show()
    }
    
    private fun navigateToBookingConfirmation(booking: BookingRequest) {
        val intent = Intent(this, BookingConfirmationActivity::class.java).apply {
            putExtra("BOOKING_ID", booking.id)
            putExtra("PICKUP", booking.pickupLocation.name)
            putExtra("DROP", booking.dropLocation.name)
            putExtra("DATE", booking.date)
            putExtra("CAR_TYPE", booking.carType)
            putExtra("FARE", fareDetails?.total ?: 0.0)
        }
        startActivity(intent)
    }
    
    private fun updateButtonStates() {
        val isLoading = viewModel.loading.value == true
        val isFormComplete = pickupLocation != null && dropLocation != null && selectedCar != null
        
        binding.calculateFareButton.isEnabled = isFormComplete && !isLoading
        binding.bookNowButton.isEnabled = isFormComplete && !isLoading
    }
}
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
