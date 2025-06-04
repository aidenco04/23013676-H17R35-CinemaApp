package com.example.a23013676_h17135_cinemaapp

//importing required Android and Jetpack Compose libraries
import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.a23013676_h17135_cinemaapp.ui.theme._23013676H17135CinemaAppTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Calendar

//data class to represent a movie
data class Movie(
    val title: String,
    val director: String,
    val releaseDate: String,
    val posterResId: Int,
    val ageRating: String,
    val runtime: String,
    val genre: String
)

//data class to represent a seat
data class Seat(
    val rowLetter: Char,
    val number: Int,
    val isSelected: MutableState<Boolean> = mutableStateOf(false)
    //isSelected will track if the user selected that seat
)




//main entry point for the app
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //apply theme and show the main app UI
            _23013676H17135CinemaAppTheme {
                val navController = rememberNavController()
                CinemaApp(navController)
            }
        }
    }
}

//main composable for navigation and bottom navigation bar
@Composable
fun CinemaApp(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF2196F3), // Blue bottom bar
                tonalElevation = 8.dp
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //navigation buttons
                    TextButton(onClick = { navController.navigate("home") }) {
                        Text("Home", color = Color.White)
                    }
                    TextButton(onClick = { navController.navigate("movies") }) {
                        Text("Movies", color = Color.White)
                    }
                    TextButton(onClick = { navController.navigate("location") }) {
                        Text("Map", color = Color.White)
                    }
                    TextButton(onClick = { navController.navigate("contact") }) {
                        Text("Contact", color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        //defines routes and their corresponding screens
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("contact") { ContactScreen(navController) }
            composable("movies") { MovieScreen(navController) }
            //movie detail screen
            composable(
                "movieDetail/{title}/{director}/{releaseDate}/{posterResId}/{ageRating}/{runtime}/{genre}",
                arguments = listOf(
                    navArgument("title") { type = NavType.StringType },
                    navArgument("director") { type = NavType.StringType },
                    navArgument("releaseDate") { type = NavType.StringType },
                    navArgument("posterResId") { type = NavType.IntType },
                    navArgument("ageRating") { type = NavType.StringType },
                    navArgument("runtime") { type = NavType.StringType },
                    navArgument("genre") { type = NavType.StringType }
                )
            )
            { backStackEntry ->
                MovieDetailScreen(
                    title = backStackEntry.arguments?.getString("title") ?: "",
                    director = backStackEntry.arguments?.getString("director") ?: "",
                    releaseDate = backStackEntry.arguments?.getString("releaseDate") ?: "",
                    posterResId = backStackEntry.arguments?.getInt("posterResId") ?: R.drawable.lotr1,
                    ageRating = backStackEntry.arguments?.getString("ageRating") ?: "",
                    runtime = backStackEntry.arguments?.getString("runtime") ?: "",
                    genre = backStackEntry.arguments?.getString("genre") ?: "",
                    navController = navController
                )
            }
            composable("seating") {SeatingScreen(navController)}
            composable("location") { MapScreen() }
            composable("bookingForm") { BookingFormScreen(navController) }
            composable("confirmation") { ConfirmationScreen() }




        }
    }
}




@Composable
fun HomeScreen() {
    //layout of the screen with home title
    ScreenLayout(title = "Home") {
        //welcome message
        Text("Welcome to Lochaber Community Cinema", fontSize = 26.sp, textAlign = TextAlign.Center)

        //image of cinema at top under welcome message
        Image(
            painter = painterResource(id = R.drawable.lochabercinema),
            contentDescription = "Lochaber Cinema Image",
            modifier = Modifier
                .height(180.dp) // adjust as needed
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp)) //makes a space between image and new movies

        //new movies heading
        Text("New Movies", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        //list of movie posters on home screen
        val movies = listOf(
            R.drawable.lotr1,
            R.drawable.lotr2,
            R.drawable.lotr3,
            R.drawable.hobbit1,
            R.drawable.hobbit2,
            R.drawable.hobbit3
        )

        //displays movie posters
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            contentPadding = PaddingValues(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(movies) { movie ->
                Image(
                    painter = painterResource(id = movie),
                    contentDescription = "Movie Poster",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 3f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}


@Composable
fun ContactScreen(navController: NavController) {
    //shows contact information
    ScreenLayout(title = "Contact Info") {
        Text("Any Queries, Please get in contact using one of the options below:")

        Spacer(modifier = Modifier.height(16.dp))
        //address and contact info
        Text("Phone Number: 01397 609696", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        Text("Email Address: info@highlandcinema.co.uk", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        Text("Address", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        Text("Cameron Square", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        Text("Fort William, Highland", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        Text("PH33 6AJ", textAlign = TextAlign.Start,modifier = Modifier.fillMaxWidth())
        //shows back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

@Composable
fun MovieScreen(navController: NavController) {
    //creates list of movies with movie details
    val movieList = listOf(
        Movie("Lord of the Rings: The Fellowship of the Ring", "Peter Jackson", "2001", R.drawable.lotr1, "PG-13", "2h 58m", "Fantasy"),
        Movie("Lord of the Rings: The Two Towers", "Peter Jackson", "2002", R.drawable.lotr2, "PG-13", "2h 59m", "Fantasy"),
        Movie("Lord of the Rings: The Return of the King", "Peter Jackson", "2003", R.drawable.lotr3, "PG-13", "3h 21m", "Fantasy"),
        Movie("The Hobbit: An Unexpected Journey", "Peter Jackson", "2012", R.drawable.hobbit1, "PG-13", "2h 49m", "Fantasy"),
        Movie("The Hobbit: The Desolation of Smaug", "Peter Jackson", "2013", R.drawable.hobbit2, "PG-13", "2h 41m", "Fantasy"),
        Movie("The Hobbit: The Battle of the Five Armies", "Peter Jackson", "2014", R.drawable.hobbit3, "PG-13", "2h 24m", "Fantasy")
    )

    //shows title Movie Info
    ScreenLayout(title = "Movie Info") {
        //shows the movies in scrollable list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        )
        {
            items(movieList.size) { index ->
                val movie = movieList[index]
                //shows each movie in card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            //when movie is clicked will take you to their movie details
                            navController.navigate(
                                "movieDetail/${movie.title}/${movie.director}/${movie.releaseDate}/${movie.posterResId}/${movie.ageRating}/${movie.runtime}/${movie.genre}"
                            )

                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F1F1)),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(modifier = Modifier.padding(12.dp)) {
                        //shows movie poster
                        Image(
                            painter = painterResource(id = movie.posterResId),
                            contentDescription = "${movie.title} Poster",
                            modifier = Modifier
                                .width(100.dp)
                                .height(150.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))
                        //shows title, director and release year
                        Column {
                            Text(movie.title, style = MaterialTheme.typography.titleMedium)
                            Text("Director: ${movie.director}")
                            Text("Released: ${movie.releaseDate}")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}


@Composable
fun MovieDetailScreen(
    title: String,
    director: String,
    releaseDate: String,
    posterResId: Int,
    ageRating: String,
    runtime: String,
    genre: String,
    navController: NavController
) {
    //shows details of the selected movie
    ScreenLayout(title = "Movie Details") {
        //gets the title and centers it
        Text(" $title", style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center ,
                modifier = Modifier.fillMaxWidth())
            //shows the movie poster
            Image(
                painter = painterResource(id = posterResId),
                contentDescription = "$title Poster",
                modifier = Modifier
                    .height(240.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Fit
            )

        Spacer(modifier = Modifier.height(16.dp))

        //movie details
        Text("Director: $director", fontSize = 20.sp)
        Text("Release Date: $releaseDate", fontSize = 20.sp)
        Text("Age Rating: $ageRating", fontSize = 20.sp)
        Text("Runtime: $runtime", fontSize = 20.sp)
        Text("Genre: $genre", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(24.dp))
        //section for description
        Text(
            "Movie Description",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        //button to take you to seating screen
        Button(
            onClick = { navController.navigate("seating") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Book Seat")
        }

        Spacer(modifier = Modifier.height(16.dp))

        //back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}



@Composable
fun ScreenLayout(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        content()
    }
}

@Composable
fun SeatingScreen(navController: NavController, movieTitle: String = "") {
    LocalContext.current
    val seatPrice = 12 //each seat is £12

    //creates the seat picking screen. rows are A->F and collumns are 1->10
    val seatList = remember {
        mutableStateListOf<Seat>().apply {
            val rows = listOf('A', 'B', 'C', 'D', 'E', 'F')
            for (row in rows) {
                for (num in 1..10) {
                    add(Seat(rowLetter = row, number = num))
                }
            }
        }
    }

    val selectedSeats = seatList.filter { it.isSelected.value }
    val totalPrice = selectedSeats.size * seatPrice

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        //heading
        Text(
            text = "Seating for $movieTitle",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        //displays the seat layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Column {
                val rowLetters = listOf('A', 'B', 'C', 'D', 'E', 'F')
                for (rowLetter in rowLetters) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        //shows the row lable
                        Text(
                            text = "Row $rowLetter",
                            modifier = Modifier.width(60.dp),
                            style = MaterialTheme.typography.labelLarge
                        )
                        //shows each seat button to click
                        for (seatNum in 1..10) {
                            val seat = seatList.first { it.rowLetter == rowLetter && it.number == seatNum }
                            //seat will be Blue if not selected but green if selected
                            Button(
                                onClick = { seat.isSelected.value = !seat.isSelected.value },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (seat.isSelected.value) Color.Green else Color(0xFF2196F3)
                                ),
                                shape = CircleShape,
                                modifier = Modifier
                                    .padding(4.dp)
                                    .width(64.dp)
                                    .height(56.dp)
                            ) {
                                Text(
                                    text = "${seat.rowLetter}${seat.number}",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    softWrap = false,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //if seats are selected will show price total price of tickets and which seats booked
        if (selectedSeats.isNotEmpty()) {
            Text(
                text = "Selected: ${selectedSeats.joinToString { "${it.rowLetter}${it.number}" }}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total: £$totalPrice",
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            //takes you to payment screen
            Button(onClick = { navController.navigate("bookingForm") }) {
                Text("Book Tickets")
            }


            Spacer(modifier = Modifier.height(8.dp))
        }
        //back button
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}

@Composable
fun BookingFormScreen(navController: NavController) {
    //fields that user can input
    val firstName = remember { mutableStateOf("") }
    val lastName = remember { mutableStateOf("") }
    val mobile = remember { mutableStateOf("") }
    val cardNumber = remember { mutableStateOf("") }
    val expiryDate = remember { mutableStateOf("") }
    val securityCode = remember { mutableStateOf("") }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    //date picker for expiry date
    val datePickerDialog = DatePickerDialog(
        context,

        { _, year, month, day ->
            expiryDate.value = "$day/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Booking Details", style = MaterialTheme.typography.headlineSmall)
        //input fields for user information
        OutlinedTextField(value = firstName.value, onValueChange = { firstName.value = it }, label = { Text("First Name") })
        OutlinedTextField(value = lastName.value, onValueChange = { lastName.value = it }, label = { Text("Last Name") })
        OutlinedTextField(value = mobile.value, onValueChange = { mobile.value = it }, label = { Text("Mobile Number") })
        OutlinedTextField(value = cardNumber.value, onValueChange = { cardNumber.value = it }, label = { Text("Card Number") })

        //date picker
        OutlinedTextField(
            value = expiryDate.value,
            onValueChange = {},
            label = { Text("Card Expiry Date") },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { datePickerDialog.show() }
        )
        //show calendar button for date picker
        Button(onClick = { datePickerDialog.show() }) {
            Text("Show Date Picker")
        }
        OutlinedTextField(value = securityCode.value, onValueChange = { securityCode.value = it }, label = { Text("Card Security Code") })

        //button takes you to purchase confirmation screen
        Button(onClick = {
            navController.navigate("confirmation")
        }) {
            Text("Confirm Purchase")
        }
    }
}

@Composable
fun ConfirmationScreen() {
    //message after booking is complete
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //main message
        Text("🎉 Thank you for purchasing!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        Text("Your tickets have been booked.")
    }
}

@Composable
fun MapScreen() {

    val context = LocalContext.current

    //gets the phones current location
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    val userLocation = remember { mutableStateOf<LatLng?>(null) }

    //asks for location permission then gets the location of the phone
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    userLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
        }
    }

    //runs when screen opens
    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    //shows location of the cinema
    val cinemaLocation = LatLng(56.8198, -5.1052) // Example cinema location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(cinemaLocation, 13f)
    }

    //shows the coordinates of phone
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Your Coordinates:",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = userLocation.value?.let {
                "Latitude: ${it.latitude}, Longitude: ${it.longitude}"
            } ?: "Getting location...",
            modifier = Modifier.padding(start = 16.dp, bottom = 16.dp)
        )

        //shows map and location of phone
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            cameraPositionState = cameraPositionState
        ) {
            //cinema location
            Marker(
                state = MarkerState(position = cinemaLocation),
                title = "Lochaber Cinema"
            )
            //phone location
            userLocation.value?.let {
                Marker(
                    state = MarkerState(position = it),
                    title = "You Are Here"
                )
            }
        }
    }
}