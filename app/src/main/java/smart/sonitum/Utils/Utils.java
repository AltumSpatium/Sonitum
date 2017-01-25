package smart.sonitum.Utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {
    public enum PlayMode {
        LOOP,
        LIST,
        SINGLE,
        SHUFFLE
    }

    public static LayoutAnimationController listAlphaTranslateAnimation(int alphaDuration, int translateDuration, boolean fromLeft, float animationDelay) {
        AnimationSet animationSet = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(alphaDuration);
        animationSet.addAnimation(animation);

        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, fromLeft ? 0f : 1f, Animation.RELATIVE_TO_SELF, fromLeft ? 1f : 0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(translateDuration);
        animationSet.addAnimation(animation);

        return new LayoutAnimationController(animationSet, animationDelay);
    }

    public static String getMinutesFromMillis(int millis) {
        return String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }
}
