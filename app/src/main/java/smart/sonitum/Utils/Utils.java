package smart.sonitum.Utils;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

public class Utils {
    public enum PlayMode {
        LOOP,
        LIST,
        SINGLE,
        SHUFFLE;
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
}
