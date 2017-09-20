package com.olegsagenadatrytwo.partyapp.utilities.map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.SphericalUtil;
import com.olegsagenadatrytwo.partyapp.model.custom_map.CustomLocationObject;
import com.olegsagenadatrytwo.partyapp.utilities.ConvertionUtilities.ConvertionUtilities;

import java.nio.ByteBuffer;

import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_AZURE;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_BLUE;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_CYAN;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_GREEN;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_MAGENTA;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_ORANGE;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_RED;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_ROSE;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_VIOLET;
import static com.olegsagenadatrytwo.partyapp.Constant.COLOR_YELLOW;
import static com.olegsagenadatrytwo.partyapp.Constant.DEFAULT_TITLE;
import static com.olegsagenadatrytwo.partyapp.Constant.TAG_IMAGE_MARKER_SET;
import static com.olegsagenadatrytwo.partyapp.Constant.TAG_MARKER_COLOR_SET;
import static com.olegsagenadatrytwo.partyapp.Constant.TAG_MARKER_LOCATION_SET;


/**
 * Created by aaron on 9/16/17.
 */

public class MapUtilities {

    //                                                    //
    //   initializeNewMarker                              //
    //      Will initialize a marker with provided data   //
    //             sent from calling method               //
    //****************************************************//
    public static MarkerOptions initializeNewMarker(MarkerOptions markerOptions, String title, CustomLocationObject locationObject, String markerColor) {
        markerOptions = setMarkerTitle(markerOptions, title);
        markerOptions = setMarkerLocation(markerOptions, locationObject);
        Log.d(TAG_MARKER_LOCATION_SET, "initializeNewMarker: " + markerOptions.getPosition().toString());
        markerOptions = setMarkerColor(markerOptions, markerColor, null);
        return markerOptions;
    }

    //                                                    //
    //   setMarkerTitle                                   //
    //      sets up a marker's title with either passed   //
    //             String or a default title              //
    //****************************************************//
    public static MarkerOptions setMarkerTitle(MarkerOptions passedMarker, String title) {
        if (title.isEmpty()) {
            passedMarker.title(DEFAULT_TITLE);
        } else {
            passedMarker.title(title);
        }
        return passedMarker;
    }

    //                                                    //
    //   setMarkerLocation                                //
    //      sets location of the marker based on values   //
    //             set in the passedLocationObject        //
    //****************************************************//
    public static MarkerOptions setMarkerLocation(MarkerOptions passedMarker, CustomLocationObject locationObject) {
        if (locationObject.getLatitude_longitude() != null) {
            passedMarker.position(locationObject.getLatitude_longitude());
        } else if (locationObject.getLatitude() != null && locationObject.getLongitude() != null) {
            passedMarker.position(new LatLng(locationObject.getLatitude(), locationObject.getLongitude()));
        } else {
            Log.d(TAG_MARKER_LOCATION_SET, "setMarkerLocation: Location Object did not have needed data to set location");
        }

        return passedMarker;
    }

    //                                                    //
    //   setMarkerColor                                   //
    //      sets the marker color based off of callers    //
    //             param.  Default is blue.               //
    //****************************************************//
    public static MarkerOptions setMarkerColor(MarkerOptions passedMarker, @Nullable String colorChoice, @Nullable Float customColorCode) {
        if (customColorCode != null) {
            passedMarker.icon(BitmapDescriptorFactory.defaultMarker(customColorCode));
        } else if (colorChoice != null) {
            switch (colorChoice) {
                case COLOR_RED:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    break;
                case COLOR_ORANGE:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                    break;
                case COLOR_YELLOW:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    break;
                case COLOR_GREEN:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    break;
                case COLOR_CYAN:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    break;
                case COLOR_AZURE:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    break;
                case COLOR_BLUE:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    break;
                case COLOR_VIOLET:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    break;
                case COLOR_MAGENTA:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                    break;
                case COLOR_ROSE:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    break;
                default:
                    passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    Log.d(TAG_MARKER_COLOR_SET, "setMarkerColor: Passed Color not recognized, Using blue as default");
                    break;
            }
        } else {
            passedMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            Log.d(TAG_MARKER_COLOR_SET, "setMarkerColor: No criteria passed, using blue as default ");
        }
        return passedMarker;
    }

    //                                                    //
    //   setVisibilityOfMarker                            //
    //      Sets if the marker can be seen by the user    //
    //                                                    //
    //****************************************************//
    public static MarkerOptions setVisibilityOfMarker(MarkerOptions passedMarker, Boolean isVisibilityOnSelected) {
        return passedMarker.visible(isVisibilityOnSelected);
    }

    //                                                    //
    //   setImageAsMarker                                 //
    //      Takes a incoming image and sets it to be      //
    //             and sets it to be the marker icon      //
    //****************************************************//
    public static MarkerOptions setImageAsMarker(MarkerOptions passedMarker, Context context, @Nullable Image passedImage, @Nullable Bitmap passedBitmap) {
        if (passedImage == null && passedBitmap == null) {
            Log.d(TAG_IMAGE_MARKER_SET, "setVisibilityOfMarker: " + "No Image sent, returning unedited passed marker");
            return passedMarker;
        } else if (passedImage != null) {
            //get bitmap of image
            ByteBuffer imageBuffer = passedImage.getPlanes()[0].getBuffer();
            byte[] imageByteArray = new byte[imageBuffer.capacity()];
            imageBuffer.get(imageByteArray);
            Bitmap bitmapOfImage = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length, null);
            bitmapOfImage = setupBitmapForMarker(context,bitmapOfImage);
            //get a canvas
            Canvas canvasForBitmapOfImage = new Canvas(bitmapOfImage);
            Drawable drawableImage = new Drawable() {
                @Override
                public void draw(@NonNull Canvas canvas) {

                }

                @Override
                public void setAlpha(@IntRange(from = 0, to = 255) int i) {
                    this.setAlpha(i);
                }

                @Override
                public void setColorFilter(@Nullable ColorFilter colorFilter) {

                }

                @Override
                public int getOpacity() {
                    return PixelFormat.OPAQUE;
                }
            };
            drawableImage.draw(canvasForBitmapOfImage);
            BitmapDescriptor imageBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmapOfImage);
            passedMarker.icon(imageBitmapDescriptor);
        } else if (passedBitmap != null) {
            passedBitmap = setupBitmapForMarker(context,passedBitmap);
            Canvas canvasForBitmapOfDrawable = new Canvas(passedBitmap);
            Drawable drawableImage = new Drawable() {
                @Override
                public void draw(@NonNull Canvas canvas) {

                }

                @Override
                public void setAlpha(@IntRange(from = 0, to = 255) int i) {
                    this.setAlpha(i);
                }

                @Override
                public void setColorFilter(@Nullable ColorFilter colorFilter) {

                }

                @Override
                public int getOpacity() {
                    return PixelFormat.OPAQUE;
                }
            };
            drawableImage.draw(canvasForBitmapOfDrawable);
            BitmapDescriptor drawableBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(passedBitmap);
            passedMarker.icon(drawableBitmapDescriptor);
        }
        return passedMarker;
    }

    //                                                    //
    //   mapDisplayToRequestedDistance                    //
    //      Returns a CameraUpdate with the zoom level    //
    //             based of the requested distance        //
    //****************************************************//
    public static CameraUpdate mapDisplayToRequestedDistance(CustomLocationObject passedLocationObject, double distance, DisplayMetrics displaymetrics) {
        LatLng rightBottom = SphericalUtil.computeOffset(passedLocationObject.getLatitude_longitude(), distance, 135);
        LatLng leftTop = SphericalUtil.computeOffset(passedLocationObject.getLatitude_longitude(), distance, -45);
        LatLngBounds screenBounds = new LatLngBounds(new LatLng(rightBottom.latitude, leftTop.longitude), new LatLng(leftTop.latitude, rightBottom.longitude));
        return CameraUpdateFactory.newLatLngBounds(screenBounds, displaymetrics.heightPixels, displaymetrics.widthPixels, 5);
    }

    //                                                    //
    //   setupBitmapForMarker                             //
    //      Sets up a bitmap image so it can be used as   //
    //             a marker                               //
    //****************************************************//
    public static Bitmap setupBitmapForMarker(Context context, Bitmap bitmap) {
        if(!bitmap.isMutable())
        {
          bitmap = ConvertionUtilities.convertToMutable(context, bitmap);
        }
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
            bitmap, 40, 40, false);
        return resizedBitmap;
}

}





