package cee.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import util.HttpClientHelper;

/**
 *
 * @author user
 */
public class TestClient {

	/**
	 *
	 * @param args
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void main(String[] args) throws UnsupportedEncodingException, IOException {
		HttpClient httpclient = null;
		try {
			httpclient = new HttpClientHelper().getClient();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpPost httppost1 = new HttpPost(
				"https://permits.sanantonio.gov/DP1/Metroplex/SanAntonio/login/WIZ_LOGIN.asp");
		List<BasicNameValuePair> nvps = null;
		InputStream is = null;
		nvps = new ArrayList<BasicNameValuePair>();
		nvps.add(new BasicNameValuePair("MAIN_LOGON_USRNAME", "gakhalsa"));
		nvps.add(new BasicNameValuePair("MAIN_LOGON_PWD", "a4vpuorx"));
		nvps.add(new BasicNameValuePair("WIZ_ACTION", "ACTION_NEXT"));
		nvps.add(new BasicNameValuePair("WIZ_CURWIZPG", "WP_LOGIN"));
		nvps.add(new BasicNameValuePair("WIZ_WIZFILENAME", "1312830034731WIZ_LOGIN"));
		nvps.add(new BasicNameValuePair("WIZ_ASPFILENAME", "%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));
		nvps.add(new BasicNameValuePair("WIZ_BTNNAME", "BTN_LOGIN"));
		nvps.add(new BasicNameValuePair("MAIN_LOGON_PWD_ENCRYPT", "Y"));
		nvps.add(new BasicNameValuePair("SERVERIP", "permits.sanantonio.gov"));
		nvps.add(new BasicNameValuePair("WANTTOPOSTTO", "%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));
		nvps.add(new BasicNameValuePair("ACTUALLYPOSTTO",
				"https%3A%2F%2Fpermits.sanantonio.gov%2FDP1%2FMetroplex%2FSanAntonio%2Flogin%2FWIZ_LOGIN.asp"));

		httppost1.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		HttpResponse response1 = httpclient.execute(httppost1);
		InputStream is2 = response1.getEntity().getContent();
		int ch = -1;
		while ((ch = is2.read()) != -1) {
			System.out.print((char) ch);
		}
	}
}
