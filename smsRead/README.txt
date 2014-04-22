To get this project to work properly:

1) Import the project into Eclipse
2) Download a copy of the Facebook SDK at https://developers.facebook.com/docs/android/
3) Import the FacebookSDK project into Eclipse
4) Right click on the project, go to "Properties" -> "Android" and ensure the following:
 - A version of Android is checked in the top half of the window
 - In the bottom half of the window the FacebookSDK probably has a red X
 - Remove the current version in the properties window
 - Click "Add" and select your version of the FacebookSDK
5) Right click on both the FacebookSDK and the App project and 
   go to "Android Tools" -> "Add Support Library"
   This will fix any issues with the support libraries not matching
6) If any errors persist, clean both the SDK and the app project
7) Run the app on your Android device, and copy the hash key generated and found in the LogCat
8) Email tom-werner@uiowa.edu, tengyu-wang@uiowa.edu or octav-chipara@uiowa.edu with the key
   and request to be added the Facebook app key list.
9) When this is finished, you are all set to run the app.