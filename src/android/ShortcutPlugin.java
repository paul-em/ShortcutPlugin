//Copyright 2013 Jorge Cisneros jorgecis@gmail.com

package com.plugins.shortcut;

import com.w3pin.browser.Browser;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.util.Base64;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.*;
import android.content.Intent;
import android.content.Context;
import android.os.Parcelable;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager;

public class ShortcutPlugin extends CordovaPlugin {
	public static final String ACTION_ADD_SHORTCUT = "addShortcut";
	public static final String ACTION_DEL_SHORTCUT = "delShortcut";

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {

		try {
			if (ACTION_ADD_SHORTCUT.equals(action)) {

				// Get params
				JSONObject arg_object = args.getJSONObject(0);

				// set param defaults
				String shortcuttext = arg_object.getString("text");
				String iconBase64 = null;
				String activityClass = null;
				String activityPackage = null;
				if( arg_object.has("icon")){
				  iconBase64 = arg_object.getString("icon");
        }

        if( arg_object.has("activityClass") & arg_object.has("activityPackage")){
          activityClass = arg_object.getString("activityClass");
          activityPackage = arg_object.getString("activityPackage");
        }


				Context context = this.cordova.getActivity()
						.getApplicationContext();
				PackageManager pm = context.getPackageManager();

				Intent i = new Intent();
				if(activityClass == null){
				  i.setClassName(this.cordova.getActivity().getPackageName(),
            this.cordova.getActivity().getClass().getName());
				} else {
				  i.setClassName(activityPackage,activityClass);
				}

				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				Intent shortcutintent = new Intent(
						"com.android.launcher.action.INSTALL_SHORTCUT");
				shortcutintent.putExtra("duplicate", false);
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						shortcuttext);

				// Get Icon
				if(iconBase64 == null){
					ResolveInfo ri = pm.resolveActivity(i, 0);
					int iconId = ri.activityInfo.applicationInfo.icon;
					Parcelable icon = Intent.ShortcutIconResource.fromContext(
						context, iconId);
		        	shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
				} else {
				  //Bitmap bmpIcon = decodeBase64(iconBase64);
				  //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmpIcon, 128, 128, true);
          //shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, scaledBitmap);
					shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, decodeBase64(iconBase64));
				}

				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
				context.sendBroadcast(shortcutintent);

				callbackContext.success();
				return true;
			} else if (ACTION_DEL_SHORTCUT.equals(action)) {
				JSONObject arg_object = args.getJSONObject(0);
				Context context = this.cordova.getActivity()
						.getApplicationContext();

				Intent i = new Intent();
				i.setClassName(this.cordova.getActivity().getPackageName(),
						this.cordova.getActivity().getClass().getName());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				Intent shortcutintent = new Intent(
						"com.android.launcher.action.UNINSTALL_SHORTCUT");
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						arg_object.getString("shortcuttext"));
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, i);
				context.sendBroadcast(shortcutintent);
				callbackContext.success();
			}
			callbackContext.error("Invalid action");
			return false;
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			callbackContext.error(e.getMessage());
			return false;
		}
	}

	private static Bitmap decodeBase64(String input)
    {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
