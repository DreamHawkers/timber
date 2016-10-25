/**
 * Mupen64PlusAE, an N64 emulator for the Android platform
 * 
 * Copyright (C) 2012 Paul Lamb
 * 
 * This file is part of Mupen64PlusAE.
 * 
 * Mupen64PlusAE is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * Mupen64PlusAE is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * See the GNU General Public License for more details. You should have received a copy of the GNU
 * General Public License along with Mupen64PlusAE. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Authors: littleguy77
 */

package com.naman14.timber.models;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.text.TextUtils;


/**
 * Utility class that encapsulates and standardizes interactions between activities.
 */
public class ActivityHelper
{
    /**
     * Keys used to pass data to activities via the intent extras bundle. It's good practice to
     * namespace the keys to avoid conflicts with other apps. By convention this is usually the
     * package name but it's not a strict requirement. We'll use the fully qualified name of this
     * class since it's easy to get.
     */
    public static class Keys
    {
        private static final String NAMESPACE = Keys.class.getCanonicalName() + ".";
        //@formatter:off
        public static final String ROM_PATH             = NAMESPACE + "ROM_PATH";
        public static final String ROM_MD5              = NAMESPACE + "ROM_MD5";
        public static final String ROM_CRC              = NAMESPACE + "ROM_CRC";
        public static final String ROM_HEADER_NAME      = NAMESPACE + "ROM_HEADER_NAME";
        public static final String ROM_COUNTRY_CODE     = NAMESPACE + "ROM_COUNTRY_CODE";
        public static final String ROM_GOOD_NAME        = NAMESPACE + "ROM_GOOD_NAME";
        public static final String ROM_ART_PATH         = NAMESPACE + "ROM_ART_PATH";
        public static final String ROM_LEGACY_SAVE      = NAMESPACE + "ROM_LEGACY_SAVE";
        public static final String DO_RESTART           = NAMESPACE + "DO_RESTART";
        public static final String PROFILE_NAME         = NAMESPACE + "PROFILE_NAME";
        public static final String SEARCH_PATH          = NAMESPACE + "GALLERY_SEARCH_PATH";
        public static final String DATABASE_PATH        = NAMESPACE + "GALLERY_DATABASE_PATH";
        public static final String CONFIG_PATH          = NAMESPACE + "GALLERY_CONFIG_PATH";
        public static final String ART_DIR              = NAMESPACE + "GALLERY_ART_PATH";
        public static final String UNZIP_DIR            = NAMESPACE + "GALLERY_UNZIP_PATH";
        public static final String SEARCH_ZIPS          = NAMESPACE + "GALLERY_SEARCH_ZIP";
        public static final String DOWNLOAD_ART         = NAMESPACE + "GALLERY_DOWNLOAD_ART";
        public static final String CLEAR_GALLERY        = NAMESPACE + "GALLERY_CLEAR_GALLERY";
        public static final String SEARCH_SUBDIR        = NAMESPACE + "GALLERY_SEARCH_SUBDIR";
        //@formatter:on
    }
    
    public static final int SCAN_ROM_REQUEST_CODE = 1;
    public static final int EXTRACT_TEXTURES_CODE = 2;

    public static void launchUri(Context context, int resId )
    {
        launchUri( context, context.getString( resId ) );
    }
    
    public static void launchUri(Context context, String uriString )
    {
        launchUri( context, Uri.parse( uriString ) );
    }
    
    public static void launchUri(Context context, Uri uri )
    {
        context.startActivity( new Intent( Intent.ACTION_VIEW, uri ) );
    }
    
    @SuppressLint( "InlinedApi" )
    public static void launchPlainText(Context context, String text, CharSequence chooserTitle )
    {
        // See http://android-developers.blogspot.com/2012/02/share-with-intents.html
        Intent intent = new Intent( android.content.Intent.ACTION_SEND );
        intent.setType( "text/plain" );
        intent.addFlags( Intent.FLAG_ACTIVITY_NEW_DOCUMENT );

        //Put a limit on this to avoid android.os.TransactionTooLargeException exception
        int limit = 1024*1024-1000;
        if(text.length() > limit)
        {
            text = text.substring(text.length()-limit, text.length());
        }

        intent.putExtra( Intent.EXTRA_TEXT, text );
        // intent.putExtra( Intent.EXTRA_SUBJECT, subject );
        // intent.putExtra( Intent.EXTRA_EMAIL, new String[] { emailTo } );
        context.startActivity( Intent.createChooser( intent, chooserTitle ) );
    }

    public static void restartActivity( Activity activity )
    {
        activity.finish();
        activity.startActivity( activity.getIntent() );
    }

}
