package sdql.fsyt.sdql.welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.github.paolorotolo.appintro.AppIntro;

import sdql.fsyt.sdql.Login;
import sdql.fsyt.sdql.R;

/**
 * Created by rohit on 22/7/15.
 */
public class Intro extends AppIntro {


    @Override
    public void init(Bundle savedInstanceState) {
        addSlide(SampleSlide.newInstance(R.layout.intro));
        addSlide(SampleSlide.newInstance(R.layout.intro2));
        addSlide(SampleSlide.newInstance(R.layout.intro3));
        addSlide(SampleSlide.newInstance(R.layout.intro4));

        SharedPreferences sp = getSharedPreferences("IsFirstRun", 0);
        if(sp.getBoolean("First", true)){
            SharedPreferences.Editor ed = sp.edit();
            setFadeAnimation();
            ed.putBoolean("First",false);
            ed.commit();
            System.out.println("执行到了");
        }else{
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            this.finish();
        }
    }

    private void loadMainActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onSkipPressed() {
        loadMainActivity();
        //Toast.makeText(getApplicationContext(), getString(R.string.skip), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

    public void getStarted(View v) {
        loadMainActivity();
    }

}
