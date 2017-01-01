package pranav.apps.amazing.hppoliceassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Pranav Gupta on 12/10/2016.
 */

public class Splash extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        final ImageView iv= (ImageView)findViewById(R.id.iv);
        final Animation an = AnimationUtils.loadAnimation(getBaseContext(),R.anim.fade_in);
        //final Animation an1 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.translate);
        final Animation an2 = AnimationUtils.loadAnimation(getBaseContext(),R.anim.abc_fade_out);
        iv.startAnimation(an);
        an.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //iv.startAnimation(an1);
                iv.startAnimation(an2);
                finish();
                Intent i =new Intent(getBaseContext(),Login.class);
                startActivity(i);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
