package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import kotlin.math.roundToInt


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(binding.root)
        binding.apply {
            setContent {}
        }
        getData()
    }


    fun initData(cities: MutableList<City>) {
        val cities2 = cities.sorted()

        val ffamily = FontFamily(
            Font(R.font.roboto, FontWeight.Normal),
            Font(R.font.roboto_medium, FontWeight.Medium)
        )
        binding.apply {
            setContent {
                NumberList(ffamily, cities2)
            }
        }
    }

    fun getData() : MutableList<City>  {
        val client = OkHttpClient()
        val cities = mutableListOf<City>()
        val request = Request.Builder()
            .url("https://gist.githubusercontent.com/Stronger197/764f9886a1e8392ddcae2521437d5a3b/raw/65164ea1af958c75c81a7f0221bead610590448e/cities.json")
            .build()
        client.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        binding.apply { setContent {FindError() } }
                        throw IOException(
                            "Запрос к серверу не был успешен:" +
                                    " ${response.code} ${response.message}"
                        )
                    }
                    val jsonString = response.body?.string()
                    val jsonArray = JSONTokener(jsonString).nextValue() as JSONArray
                    for (i in 0 until jsonArray.length()) {
                        val name = jsonArray.getJSONObject(i).getString("city")
                        val lat = jsonArray.getJSONObject(i).getDouble("latitude")
                        val long = jsonArray.getJSONObject(i).getDouble("longitude")
                        if (name != "") {
                            cities.add(City(name, lat, long))
                            println("city $name added")
                        }
                    }

                }
                initData(cities)
            }
        })

        return cities
    }

@Composable
    fun FindError() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight()
        .wrapContentHeight(Alignment.CenterVertically)
        .wrapContentWidth(Alignment.CenterHorizontally)) {

        Box(
            Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)) {
            Text(text = "Произошла ошибка", textAlign = TextAlign.Center)

            Button(
                onClick = { getData() }, Modifier
                    .offset(y = 42.dp)
            )
            {
                Text(text = "Обновить")
            }
        }
    }
}
    fun nextActivity(name : String, lat : Double, long: Double, temp : String) {
        val weatherIntent = Intent(this, WeatherActivity::class.java)
        weatherIntent.putExtra("cityName", name)
        weatherIntent.putExtra("lat", lat)
        weatherIntent.putExtra("long", long)
        weatherIntent.putExtra("temp", temp)
        startActivity(weatherIntent)
    }


    @SuppressLint("UnrememberedMutableInteractionSource")
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun NumberList(ff : FontFamily, list: List<City>) {
        println(list[0].name)
        val list2 = list.sortedBy { it.name }
        val counter = 0
        val groups = list2.groupBy { it.name.get(0)}
        LazyColumn ()
        {

            groups.forEach { (symbol, item) ->
                counter.inc()
                stickyHeader () {
                    Box(

                        modifier = Modifier
                            .width(40.dp)

                            .height(56.dp)
                            .wrapContentHeight(Alignment.CenterVertically)
                            .wrapContentWidth(Alignment.CenterHorizontally)

                    )
                    {
                        Text(
                            text = symbol.toString(), fontSize = 24.sp,
                            fontFamily = ff,
                            fontWeight = FontWeight.Medium,
                            fontStyle = FontStyle.Normal,
                            modifier = Modifier

                        )

                    }

                }

                items(item) { city ->
                    Box(
                        modifier = Modifier
                            .offset(x = 40.dp, y = (0).dp)

                            .clip(shape = RoundedCornerShape(15.dp))
                            .clickable(interactionSource = MutableInteractionSource(),
                                indication = rememberRipple(
                                    bounded = true
                                ),
                                onClick = {getInfo(city.name, city.lat, city.long)}
                            )



                    ) {

                        Box(

                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, top = 0.dp)
                                .height(56.dp)
                                .wrapContentHeight(Alignment.CenterVertically)
                                .clip(shape = RoundedCornerShape(15.dp))


                        ) {
                            Text(
                                text = city.name, fontSize = 20.sp,
                                fontFamily = ff,
                                fontWeight = FontWeight.Normal,
                                fontStyle = FontStyle.Normal
                            )

                        }

                    }

                }


            }
        }
        }
    fun getInfo(name: String, lat : Double, long: Double) {

        val client = OkHttpClient()
        var temp = String()
        val request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?lat=$lat&lon=$long&appid=344f91d342999f5fae9f50703256b09f")
            .build()
        client.run {
            newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }


                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            throw IOException(
                                "Запрос к серверу не был успешен:" +
                                        " ${response.code} ${response.message}"
                            )

                        }

                        val jsonString = response.body?.string()
                        val jsonObject = JSONTokener(jsonString).nextValue() as JSONObject
                        val main = jsonObject.getJSONObject("main")
                        println(main.getDouble("temp"))
                        temp = (main.getDouble("temp")-273.15).roundToInt().toString()
                        nextActivity(name, lat, long, temp)
                    }
                }
            })
        }
    }
}




