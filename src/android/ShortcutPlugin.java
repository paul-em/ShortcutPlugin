//Copyright 2013 Jorge Cisneros jorgecis@gmail.com

package com.plugins.shortcut;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
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
				String shortcuttext = arg_object.getString("shortcuttext");
				String icon = arg_object.getString("icon");


				Context context = this.cordova.getActivity()
						.getApplicationContext();
				PackageManager pm = context.getPackageManager();

				Intent i = new Intent();
				i.setClassName(this.cordova.getActivity().getPackageName(),
						this.cordova.getActivity().getClass().getName());
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				Intent shortcutintent = new Intent(
						"com.android.launcher.action.INSTALL_SHORTCUT");
				shortcutintent.putExtra("duplicate", false);
				shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
						arg_object.getString("shortcuttext"));

				// Get Icon
				if(isNull(icon)){
					ResolveInfo ri = pm.resolveActivity(i, 0);
					int iconId = ri.activityInfo.applicationInfo.icon;
					Parcelable icon = Intent.ShortcutIconResource.fromContext(
						context, iconId);
		        	shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
				} else {
					Drawable iconDrawable = decodeBase64(icon);
					BitmapDrawable bd = (BitmapDrawable) iconDrawable;
					shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON, bd.getBitmap());
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
