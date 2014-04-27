package registration;

import twitter4j.auth.AccessToken;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * Internal class used for accessing the access token after we have finished
 * authentication
 */
public class GetTwitterAccessToken extends
		AsyncTask<String, String, AccessToken>
{
	private MakeUser makeUser;

	public GetTwitterAccessToken(MakeUser makeUser)
	{
		this.makeUser = makeUser;
	}

	@Override
	protected void onPostExecute(AccessToken accessToken)
	{
		// This happens if the user has not verfied their twitter account
		if (accessToken == null)
		{
			makeUser.setOauthText("");
			makeUser.setOauthSecretText("");
			makeUser.setScreenNameText("");
			makeUser.setTwitterID("");


			makeUser.getLoginToFacebook().setEnabled(false);
			makeUser.getLoginToTwitter().setEnabled(true);

			makeUser.updateFacebookButton();
		}
		else
		{
			makeUser.setOauthText(accessToken.getToken());
			makeUser.setOauthSecretText(accessToken.getTokenSecret());
			makeUser.setScreenNameText(accessToken.getScreenName());
			makeUser.setTwitterID("" + accessToken.getUserId());

			makeUser.getLoginToFacebook().setEnabled(false);
			makeUser.getLoginToTwitter().setEnabled(false);
			makeUser.getLoginToTwitter().setText(
					"Logged in as " + accessToken.getScreenName());
			makeUser.getPhoneField().setEnabled(true);
			makeUser.getPhoneLabel().setEnabled(true);

			makeUser.updateFacebookButton();
		}
	}

	@Override
	protected AccessToken doInBackground(String... params)
	{
		AccessToken accessToken = TwitterUtilities.getInstance()
				.getAccessToken(params[0]);
		accessToken = TwitterUtilities.getInstance().getAccessToken(params[0]);
		if (accessToken == null)
		{
			return null;
		}
		SharedPreferences.Editor editor = makeUser.getSharedPreferences()
				.edit();
		editor.putString(TwitterUtilities.PREFERENCE_TWITTER_OAUTH_TOKEN,
				accessToken.getToken());
		editor.putString(
				TwitterUtilities.PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET,
				accessToken.getTokenSecret());

		editor.commit();
		return accessToken;
	}
}
