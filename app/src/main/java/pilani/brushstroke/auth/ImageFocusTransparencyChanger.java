package pilani.brushstroke.auth;

import android.view.View;
import android.widget.ImageView;


public class ImageFocusTransparencyChanger implements View.OnFocusChangeListener {
    private final ImageView mTogglePasswordImage;
    private final float mSlightlyVisible;
    private final float mVisible;

    public ImageFocusTransparencyChanger(
            ImageView togglePasswordImage,
            float visible,
            float slightlyVisible) {
        mTogglePasswordImage = togglePasswordImage;
        mVisible = visible;
        mSlightlyVisible = slightlyVisible;
        mTogglePasswordImage.setAlpha(mSlightlyVisible);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mTogglePasswordImage.setAlpha(mVisible);
        } else {
            mTogglePasswordImage.setAlpha(mSlightlyVisible);
        }

    }
}
