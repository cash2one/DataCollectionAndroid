package registration;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUtilities
{
	public static final String TWITTER_CONSUMER_KEY = "BaWtyknv1RwsU60jVccA";
	public static final String TWITTER_CONSUMER_SECRET = "EDopj7ySkVstUTD294ODgUlmhctGi3PBSkW2OljhhPY";
	public static final String TWITTER_CALLBACK_URL = "oauth://datacollection";
	public static final String PREFERENCE_TWITTER_IS_LOGGED_IN = "isTwitterLogedIn";
	public static final String URL_PARAMETER_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	public static final String PREFERENCE_TWITTER_OAUTH_TOKEN = "oauth_token";
	public static final String PREFERENCE_TWITTER_OAUTH_TOKEN_SECRET = "oauth_token_secret";

	static TwitterUtilities instance = new TwitterUtilities();

	private RequestToken requestToken = null;
	private AccessToken accessToken = null;
	private TwitterFactory twitterFactory = null;
	private Twitter twitter;

	public TwitterUtilities()
	{
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitterFactory = new TwitterFactory(configuration);
		twitter = twitterFactory.getInstance();
	}

	public TwitterFactory getTwitterFactory()
	{
		return twitterFactory;
	}

	public void setTwitterFactory(AccessToken accessToken)
	{
		twitter = twitterFactory.getInstance(accessToken);
	}

	public Twitter getTwitter()
	{
		return twitter;
	}

	public RequestToken getRequestToken()
	{
		try
		{
			requestToken = twitterFactory.getInstance().getOAuthRequestToken(
					TWITTER_CALLBACK_URL);

		}
		catch (TwitterException e)
		{
			e.printStackTrace();
		}
		return requestToken;
	}

	public AccessToken getAccessToken(String verifier)
	{
		//if (accessToken == null)
		//{
			try
			{
				accessToken = twitterFactory.getInstance().getOAuthAccessToken(
						requestToken, verifier);
			}
			catch (TwitterException e)
			{
				System.err.println("Error retrieving access token");
			}
			catch (IllegalStateException e)
			{
				System.out.println("Something went wrong...");
			}
			catch (NullPointerException e)
			{
				System.out.println("User pressed cancel. Returning null.");
			}
		//}
		return accessToken;
	}

	public static TwitterUtilities getInstance()
	{
		return instance;
	}

	public void reset()
	{
		instance = new TwitterUtilities();
	}
}
