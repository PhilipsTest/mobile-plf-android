package com.philips.cdp.registration.ads.helper

import com.philips.cdp.registration.ads.model.Response
import retrofit2.http.GET

interface GetServiceCountry {

//    @GET
//    fun retrieveRepositories( @Url url:String): Deferred<WestWingResponse>


    @GET("houseAds/ads.json")
    suspend fun getTestProductsList(): Response
}