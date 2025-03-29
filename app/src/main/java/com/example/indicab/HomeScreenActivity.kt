class HomeScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var carTypesAdapter: CarTypesAdapter
    private var pickupLocation: Location? = null
    private var dropLocation: Location? = null
    private var selectedDate: Date = Date()
    private var tripType: String = "ONE_WAY"
    private var selectedCar: CarType? = null

    private const val LOCATION_PERMISSION_REQUEST_CODE = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        checkLocationPermissions()
        setupUI()
        setupObservers()
        loadCarTypes()
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    private fun calculateFare() {
        pickupLocation?.let { pickup ->
            dropLocation?.let { drop ->
                selectedCar?.let { car ->
                    try {
                        viewModel.calculateFare(pickup, drop, car.id)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error calculating fare: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun handleBooking() {
        pickupLocation?.let { pickup ->
            dropLocation?.let { drop ->
                selectedCar?.let { car ->
                    val dateTimeString = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).format(selectedDate)
                    val bookingRequest = BookingRequest(pickupLocation = pickup, dropLocation = drop, date = dateTimeString, carType = car.id, tripType = tripType)
                    try {
                        viewModel.createBooking(bookingRequest)
                    } catch (e: Exception) {
                        Toast.makeText(this, "Error creating booking: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
