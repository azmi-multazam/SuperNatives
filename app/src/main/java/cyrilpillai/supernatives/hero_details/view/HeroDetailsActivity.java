package cyrilpillai.supernatives.hero_details.view;

import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.MenuItem;
import android.view.View;

import java.util.List;

import javax.inject.Inject;

import cyrilpillai.supernatives.R;
import cyrilpillai.supernatives.databinding.ActivityHeroDetailsBinding;
import cyrilpillai.supernatives.hero_details.contract.HeroDetailsContract;
import cyrilpillai.supernatives.hero_details.view.adapter.HeroDetailsAdapter;
import cyrilpillai.supernatives.heroes_list.entity.SuperHero;
import cyrilpillai.supernatives.utils.Constants;
import dagger.android.AndroidInjection;

/**
 * Created by cyrilpillai on 12-11-2017.
 */

public class HeroDetailsActivity extends AppCompatActivity implements HeroDetailsContract.View {

    private ActivityHeroDetailsBinding binding;
    private Context context;

    @Inject
    HeroDetailsContract.Presenter presenter;

    @Inject
    HeroDetailsAdapter adapter;

    public static void start(Context context, SuperHero superHero) {
        Intent intent = new Intent(context, HeroDetailsActivity.class);
        intent.putExtra(Constants.SUPERHERO, superHero);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_hero_details);
        context = this;

        SuperHero superHero = getIntent().getParcelableExtra(Constants.SUPERHERO);

        if (superHero == null) {
            finish();
        } else {

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle(superHero.getName());
            }


            binding.rvHeroDetails.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL, false));
            binding.rvHeroDetails.setAdapter(adapter);

            binding.btnTryAgain.setOnClickListener(v ->
                    presenter.getSuperHeroDetails(superHero.getId()));

            binding.btnTryAgain.performClick();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setSuperHeroDetailsView(List<Object> detailsView) {
        adapter.setData(detailsView);
    }

    @Override
    public void loadingView(boolean isLoading) {
        binding.pbLoading.setVisibility(isLoading ? View.VISIBLE : View.GONE);
    }

    @Override
    public void superHeroDetailsView(boolean show) {
        binding.rvHeroDetails.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void errorView(boolean show) {
        binding.llErrorView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
