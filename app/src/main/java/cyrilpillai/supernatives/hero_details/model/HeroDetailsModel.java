package cyrilpillai.supernatives.hero_details.model;

import androidx.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import javax.inject.Inject;

import cyrilpillai.supernatives.hero_details.contract.HeroDetailsContract;
import cyrilpillai.supernatives.hero_details.entity.SuperHeroDetails;
import cyrilpillai.supernatives.hero_details.repo.HeroDetailsRepo;
import cyrilpillai.supernatives.utils.callbacks.DataCallback;
import cyrilpillai.supernatives.utils.network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by cyrilpillai on 12-11-2017.
 */

public class HeroDetailsModel implements HeroDetailsContract.Model {

    private ApiService apiService;
    private HeroDetailsRepo heroDetailsRepo;

    @Inject
    public HeroDetailsModel(ApiService apiService, HeroDetailsRepo heroDetailsRepo) {
        this.apiService = apiService;
        this.heroDetailsRepo = heroDetailsRepo;
    }


    @Override
    public void fetchSuperHeroDetails(long characterId,
                                      DataCallback<SuperHeroDetails, Throwable> dataCallback) {
        SuperHeroDetails superHeroDetails = heroDetailsRepo.fetchById(characterId);
        if (superHeroDetails != null) {
            Log.d("Heroes", "fetchSuperHeroDetails: from Local Cache");
            dataCallback.onSuccess(superHeroDetails);
        } else {
            Log.d("Heroes", "fetchSuperHeroDetails: from Network");
            Call<SuperHeroDetails> call = apiService
                    .getSuperHeroDetails(characterId);
            call.enqueue(new Callback<SuperHeroDetails>() {
                @Override
                public void onResponse(
                        @NonNull Call<SuperHeroDetails> call,
                        @NonNull Response<SuperHeroDetails> response) {
                    SuperHeroDetails details = response.body();
                    if (response.isSuccessful() &&
                            details != null) {
                        Log.d("RESPONSE", new Gson().toJson(details));
                        heroDetailsRepo.save(details);
                        dataCallback.onSuccess(details);
                    } else {
                        dataCallback.onError(new Throwable("Error"));

                    }
                }

                @Override
                public void onFailure(
                        @NonNull Call<SuperHeroDetails> call,
                        @NonNull Throwable t) {
                    dataCallback.onError(t);
                }
            });
        }
    }
}
