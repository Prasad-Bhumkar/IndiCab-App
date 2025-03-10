package com.example.indicab

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.indicab.adapters.CarTypesAdapter
import com.example.indicab.api.BookingService
import com.example.indicab.databinding.ActivityHomeScreenBinding
import com.example.indicab.models.BookingRequest
import com.example.indicab.models.CarType
import com.example.indicab.models.FareDetails
import com.example.indicab.models.Location
import com.example.indicab.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
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
        // Sample implementation - in a real app, you'd show a location picker
        val locations = listOf(
            Location("1", "Airport", "International Airport", 12.9716, 77.5946),
            Location("2", "City Center", "Downtown", 12.9716, 77.5946),
            Location("3", "Mall", "Shopping Center", 12.9716, 77.5946)
        )
        
        val locationNames = locations.map { it.name }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle(if (isPickup) "Select Pickup Location" else "Select Drop Location")
            .setItems(locationNames) { _, which ->
                val selectedLocation = locations[which]
                if (isPickup) {
                    pickupLocation = selectedLocation
                    binding.pickupLocationSelector.text = selectedLocation.name
                } else {
                    dropLocation = selectedLocation
                    binding.dropLocationSelector.text = selectedLocation.name
                }
                updateButtonStates()
            }
            .show()
    }
    
    private fun showDateTimePicker() {
        // Sample implementation - in a real app, you'd use DatePickerDialog and TimePickerDialog
        selectedDate = Date() // Current date/time
        val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        binding.dateTimeSelector.text = dateFormat.format(selectedDate)
    }
    
    private fun showFareDetailsModal(fareDetails: FareDetails) {
        binding.fareDetailsModal.visibility = View.VISIBLE
        // Populate fare details in the modal
        // In a real implementation, you'd set the values in the modal layout
        
        binding.closeModalButton.setOnClickListener {
            binding.fareDetailsModal.visibility = View.GONE
        }
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