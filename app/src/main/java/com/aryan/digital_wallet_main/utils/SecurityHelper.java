package com.aryan.digital_wallet_main.utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class SecurityHelper {
    private static final String PREF_NAME = "wallet_security_prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_CURRENT_USER_EMAIL = "current_user_email";
    private static final String KEY_USERS = "users_data";

    private final SharedPreferences sharedPreferences;
    private final String TAG = "SecurityHelper";

    public SecurityHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }


    public void setLoggedIn(boolean isLoggedIn, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        if (isLoggedIn) {
            editor.putString(KEY_CURRENT_USER_EMAIL, email);
        } else {
            editor.remove(KEY_CURRENT_USER_EMAIL);
        }
        editor.apply();
    }


    public boolean registerUser(String name, String email, String password) {
        try {
            // Get existing users
            JSONArray usersArray = getUsersArray();

            // Check if user already exists
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    Log.d(TAG, "User with email " + email + " already exists");
                    return false; // Email already exists
                }
            }


            JSONObject newUser = new JSONObject();
            newUser.put("name", name);
            newUser.put("email", email);
            newUser.put("password", password); // In a real app, hash this password


            usersArray.put(newUser);


            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERS, usersArray.toString());
            editor.apply();

            Log.d(TAG, "User registered successfully: " + email);
            return true;
        } catch (JSONException e) {
            Log.e(TAG, "Error registering user: " + e.getMessage());
            return false;
        }
    }


    public boolean authenticate(String email, String password) {
        try {
            JSONArray usersArray = getUsersArray();

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(email) &&
                        user.getString("password").equals(password)) {
                    Log.d(TAG, "User authenticated: " + email);
                    return true;
                }
            }

            Log.d(TAG, "Authentication failed for: " + email);
            return false;
        } catch (JSONException e) {
            Log.e(TAG, "Error authenticating user: " + e.getMessage());
            return false;
        }
    }


    public String getCurrentUserName() {
        try {
            String email = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "");
            if (email.isEmpty()) {
                return "";
            }

            JSONArray usersArray = getUsersArray();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(email)) {
                    return user.getString("name");
                }
            }
            return "";
        } catch (JSONException e) {
            Log.e(TAG, "Error getting user name: " + e.getMessage());
            return "";
        }
    }

    public boolean updateUserProfile(String name, String email) {
        try {
            String currentEmail = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "");
            if (currentEmail.isEmpty()) {
                return false;
            }

            JSONArray usersArray = getUsersArray();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(currentEmail)) {
                    user.put("name", name);
                    user.put("email", email);

                    // Update current user email if it has changed
                    if (!currentEmail.equals(email)) {
                        setLoggedIn(true, email);
                    }

                    // Save updated array
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERS, usersArray.toString());
                    editor.apply();

                    Log.d(TAG, "User profile updated successfully: " + email);
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            Log.e(TAG, "Error updating user profile: " + e.getMessage());
            return false;
        }
    }


    public boolean changePassword(String currentPassword, String newPassword) {
        try {
            String email = sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "");
            if (email.isEmpty()) {
                return false;
            }

            JSONArray usersArray = getUsersArray();
            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject user = usersArray.getJSONObject(i);
                if (user.getString("email").equals(email) &&
                        user.getString("password").equals(currentPassword)) {
                    user.put("password", newPassword);

                    // Save updated array
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_USERS, usersArray.toString());
                    editor.apply();

                    Log.d(TAG, "Password changed successfully for: " + email);
                    return true;
                }
            }
            return false;
        } catch (JSONException e) {
            Log.e(TAG, "Error changing password: " + e.getMessage());
            return false;
        }
    }


    public String getCurrentUserEmail() {
        return sharedPreferences.getString(KEY_CURRENT_USER_EMAIL, "");
    }


    public void clearUserData() {
        setLoggedIn(false, "");
    }


    private JSONArray getUsersArray() throws JSONException {
        String usersJson = sharedPreferences.getString(KEY_USERS, "[]");
        return new JSONArray(usersJson);
    }
}