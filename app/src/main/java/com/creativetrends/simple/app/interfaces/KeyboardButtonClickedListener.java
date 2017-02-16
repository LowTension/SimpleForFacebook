package com.creativetrends.simple.app.interfaces;


import com.creativetrends.simple.app.lock.KeyboardButtonEnum;


public interface KeyboardButtonClickedListener {


    void onKeyboardClick(KeyboardButtonEnum keyboardButtonEnum);


    void onRippleAnimationEnd();

}
