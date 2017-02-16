package com.creativetrends.simple.app.helpers;

import android.webkit.WebView;

public class BadgeHelper {

    private static final int BADGE_UPDATE_INTERVAL = 45000;

    private static final int NEWS_BADGE = 6000;


    public static void updateNums(WebView view) {
        view.loadUrl("javascript:(function()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%7D)()");
    }

    public static void updateNumsService(WebView view) {
        view.loadUrl("javascript:(function()%7Bfunction%20n_s()%7Bandroid.getNums(document.querySelector(%22%23notifications_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23messages_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML%2Cdocument.querySelector(%22%23requests_jewel%20%3E%20a%20%3E%20div%20%3E%20span%5Bdata-sigil%3Dcount%5D%22).innerHTML)%2CsetTimeout(n_s%2C" + BADGE_UPDATE_INTERVAL + ")%7Dtry%7Bn_s()%7Dcatch(_)%7B%7D%7D)()");
    }

    public static void updateFeedService(WebView view) {
        view.loadUrl("javascript:function feed_service(){android.getFeedCount(document.querySelector('#feed_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(feed_service, " + NEWS_BADGE + ");}try{feed_service();}catch(e){}");
    }

    public static void updateFeedNum(WebView view) {
        view.loadUrl("javascript:function feed_service(){android.getFeedCount(document.querySelector('#feed_jewel > a > div > span[data-sigil=count]').innerHTML);setTimeout(feed_service, " + NEWS_BADGE + "));}try{feed_service();}catch(e){}");
    }




    public static void updateUserInfo(WebView view) {
        // Get logged in info
        view.loadUrl("javascript:try{android.getUserInfo(document.querySelector('form#mbasic_inline_feed_composer').getElementsByClassName('profpic')[0].outerHTML)}catch(e){null;}");
    }

    public static void videoView(WebView view){
        view.loadUrl("javascript:(function prepareVideo() { var el = document.querySelectorAll('div[data-sigil]');for(var i=0;i<el.length; i++){var sigil = el[i].dataset.sigil;if(sigil.indexOf('inlineVideo') > -1){delete el[i].dataset.sigil;console.log(i);var jsonData = JSON.parse(el[i].dataset.store);el[i].setAttribute('onClick', 'Downloader.processVideo(\"'+jsonData['src']+'\",\"'+jsonData['videoID']+'\");');}}})()" );
        view.loadUrl("javascript:( window.onload=prepareVideo;)()");
    }


    public static boolean isInteger(String str) {
        return (str.matches("^-?\\d+$"));
    }




}




