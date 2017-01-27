package com.example.administrator.minh;

import android.widget.ImageButton;

public class ButtonPair {
    public ImageButton button;
    public Space space;

    public ButtonPair() {
        button = null;
        space = null;
    }

    public ButtonPair(ImageButton button, Space space) {
        this.button = button;
        this.space = space;
    }
}
