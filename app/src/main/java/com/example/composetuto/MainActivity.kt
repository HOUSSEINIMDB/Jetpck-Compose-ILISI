package com.example.composetuto

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.composetuto.ui.theme.ComposeTutoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

import android.widget.ImageView
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import coil.annotation.ExperimentalCoilApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

//@OptIn(, ExperimentalCoilApi::class)
//@OptIn(ExperimentalMaterial3Api::class)

class MainActivity : ComponentActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeTutoTheme {
                var results by remember {
                    mutableStateOf(listOf<Map<String, String>>())
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding((16.dp))
                    ){
                        Row (
                            modifier = Modifier
                                .background(Color.Transparent)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,

                        )
                        {
                            Text(text =  "Chicken Soup Recipes")
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            modifier = Modifier.
                            padding(horizontal = 25.dp),
                            onClick = {

                                lifecycleScope.launch {
                                    try {
                                        val client = OkHttpClient()
                                        val request = Request.Builder()
                                            .url("https://food-recipes-with-images.p.rapidapi.com/?q=chicken%20soup")
                                            .get()
                                            .addHeader("X-RapidAPI-Key", "1dd1b6e76fmsh04dc17db125883cp14c8bcjsn6103674b54f5")
                                            .addHeader("X-RapidAPI-Host", "food-recipes-with-images.p.rapidapi.com")
                                            .build()

                                        val response = withContext(Dispatchers.IO) {
                                            client.newCall(request).execute()
                                        }


                                        if (response.isSuccessful) {
                                            val jsonResponse = response.body?.string()

                                            val jsonElement = Json.parseToJsonElement(jsonResponse.orEmpty())
                                            Log.i("The element", jsonElement.toString())

                                            withContext(Dispatchers.Main) {
                                                results = if (jsonElement is JsonObject) {
                                                    val dataArray = jsonElement["d"]?.jsonArray
                                                    dataArray?.mapNotNull { recipeElement ->
                                                        if (recipeElement is JsonObject) {
                                                            val title = recipeElement["Title"]?.jsonPrimitive?.contentOrNull
                                                            val image = recipeElement["Image"]?.jsonPrimitive?.contentOrNull

                                                            if (title != null && image != null) {
                                                                mapOf("title" to title, "imageUrl" to image)
                                                            } else {
                                                                Log.e("Parsing Error", "Title or Image is null for a recipe: $recipeElement")
                                                                null
                                                            }
                                                        } else {
                                                            Log.e("Parsing Error", "Recipe element is not a JsonObject: $recipeElement")
                                                            null
                                                        }
                                                    } ?: emptyList()
                                                } else {
                                                    Log.e("Parsing Error", "JsonElement is not a JsonObject: $jsonElement")
                                                    emptyList()
                                                }
                                            }
                                        }



                                        Log.i("The results",results.toString())






                                    } catch (e: Exception) {
                                        Log.e("MainActivity", "Network request failed", e)
                                    }
                                }
                        }
                        ){
                            Text(text="Reload")
                        }
                        LazyColumn {
                            val textStyle = TextStyle(
                                color = Color.Green,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )

                            val urlTextStyle = TextStyle(
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                            items(results) { result ->
                                val imageUrl = result["imageUrl"]
                                Text(
                                    text = result["title"] ?: "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = textStyle
                                )

                                Text(

                                    text = result["imageUrl"] ?: "",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    style = urlTextStyle
                                )

                                //Asynchrone
                                AsyncImage(
                                    model = "https:$imageUrl",
                                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                                    error = painterResource(id = R.drawable.ic_launcher_foreground),
                                    contentDescription = imageUrl,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                                Divider()
                            }
                        }


                    }
                }
            }
        }
    }

}


