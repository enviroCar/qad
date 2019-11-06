package org.envirocar.qad.mapmatching;

import org.envirocar.qad.model.FeatureCollection;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface MapMatchingService {
    @POST("/mapmatching")
    @Headers({"Accept: application/json", "Content-Type: application/json"})
    Call<MapMatchingResult> mapMatch(@Body FeatureCollection featureCollection);
}
