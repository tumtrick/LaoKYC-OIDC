package com.gov.mpt.laokyclib.button.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import com.gov.mpt.laokyclib.R;
import static com.gov.mpt.laokyclib.button.util.Constants.BUTTON_TEXT_SIZE;

/**
 * Created by SBlab on 2020-10-29.
 */

public class LaoKYCSignInButton extends AppCompatButton {

    /**
     * Text that user wants the button to have.
     * This overrides the default "Sign in with LaoKYC" text.
     */
    private String mText;

    /**
     * Flag to show the dark theme with LaoKYC standard dark blue color.
     */
    private boolean mIsDarkTheme;

    /**
     * Constructor
     *
     * @param context Context
     */
    public LaoKYCSignInButton(Context context) {
        super(context);
    }

    /**
     * Constructor
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    public LaoKYCSignInButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(context, attributeSet, 0);
    }

    /**
     * Constructor
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     * @param defStyleAttr int
     */
    public LaoKYCSignInButton(Context context, AttributeSet attributeSet, int defStyleAttr) {
        super(context, attributeSet, defStyleAttr);
        init(context, attributeSet, defStyleAttr);
    }

    /**
     * Initialize the process to get custom attributes from xml and set button params.
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    private void init(Context context, AttributeSet attributeSet, int defStyleAttr) {
        parseAttributes(context, attributeSet, defStyleAttr);
        setButtonParams();
    }

    /**
     * Parses out the custom attributes.
     *
     * @param context      Context
     * @param attributeSet AttributeSet
     */
    private void parseAttributes(Context context, AttributeSet attributeSet, int defStyleAttr) {
        if (attributeSet == null) {
            return;
        }

        // Retrieve styled attribute information from the styleable.
        TypedArray typedArray = context.getTheme().
                obtainStyledAttributes(attributeSet, R.styleable.ButtonStyleable, defStyleAttr, 0);

        try {
            // Get text which user wants to set the button.
            mText = typedArray.getString(R.styleable.ButtonStyleable_android_text);
            mIsDarkTheme = typedArray.getBoolean(R.styleable.ButtonStyleable_isDarkTheme, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            typedArray.recycle();
        }
    }

    /**
     * Set button parameters.
     */
    private void setButtonParams() {
        // We need not have only upper case character.
        this.setTransformationMethod(null);
        // Set button text size
        setButtonTextSize();
        // Set button text color
        setButtonTextColor();
        // Set background of button
        setButtonBackground();
    }

    /**
     * Set the text size to standard as mentioned in guidelines.
     */
    private void setButtonTextSize() {
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, BUTTON_TEXT_SIZE);
    }

    /**
     * Check the theme and set background based on theme which is a selector.
     * The selector handles the background color when button is clicked.
     */
    private void setButtonBackground() {

        int googleIconImageSelector = R.drawable.button_theme_laokyc_icon_selector;
        this.setBackgroundResource(googleIconImageSelector);

    }

    /**
     * Check the theme and set text color based on theme.
     */
    private void setButtonTextColor() {
        int textColor = mIsDarkTheme ? android.R.color.white : R.color.text_color_dark;
        this.setTextColor(ContextCompat.getColor(getContext(), textColor));
    }

}
