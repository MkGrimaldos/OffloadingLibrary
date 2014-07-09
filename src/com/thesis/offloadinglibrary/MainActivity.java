package com.thesis.offloadinglibrary;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.math.BigInteger;

import com.thesis.offloadinglibrary.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	/*
	 * EditText base; EditText power; TextView result;
	 */

	long beginning = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*
		 * base = (EditText)findViewById(R.id.baseText); power =
		 * (EditText)findViewById(R.id.powerText); result =
		 * (TextView)findViewById(R.id.resultText);
		 * 
		 * Button onDeviceButton = (Button)findViewById(R.id.onDeviceButton);
		 * Button onServerButton = (Button)findViewById(R.id.onServerButton);
		 * 
		 * onDeviceButton.setOnClickListener(onDeviceButtonOnClickListener);
		 * onServerButton.setOnClickListener(onServerButtonOnClickListener);
		 */
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	public void computeOnDevice(View view) {
		beginning = System.nanoTime();

		EditText base = (EditText) findViewById(R.id.baseText);
		EditText power = (EditText) findViewById(R.id.powerText);
		TextView result = (TextView) findViewById(R.id.resultText);

		result.setText("");

		if (base.getText().toString().equals("")
				|| power.getText().toString().equals(""))
			// TODO create a error message
			System.exit(0);

		new ComputationTask().execute(base.getText().toString(), power
				.getText().toString());
	}

	public void computeOnServer(View view) {
		beginning = System.nanoTime();

		EditText base = (EditText) findViewById(R.id.baseText);
		EditText power = (EditText) findViewById(R.id.powerText);
		TextView result = (TextView) findViewById(R.id.resultText);

		result.setText("");

		if (base.getText().toString().equals("")
				|| power.getText().toString().equals(""))
			// TODO create a error message
			System.exit(0);
		
		new ServerInteractionTask().execute(base.getText().toString(), power
				.getText().toString());
	}
	
	private class ComputationTask extends AsyncTask<String, Integer, String>{

		@Override
		protected String doInBackground(String... params) {
			int n = Integer.parseInt(params[1]);

			long baseDo = Long.parseLong(params[0]);
			BigInteger resul = new BigInteger("1");

			while (n > 0) {
				resul = resul.multiply(BigInteger.valueOf(baseDo));
				--n;
			}
			
			return resul.toString();
		}
		
		protected void onPostExecute(String res) {
			EditText base = (EditText) findViewById(R.id.baseText);
			EditText power = (EditText) findViewById(R.id.powerText);
			TextView result = (TextView) findViewById(R.id.resultText);

			result.setText(base.getText().toString() + " to the power of "
					+ power.getText().toString() + " is " + res);

			System.out.println("Time elapsed: " + (System.nanoTime() - beginning)
					+ "ns");
		}
		
	}

	private class ServerInteractionTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			Socket socket = null;
			DataOutputStream dataOutputStream = null;
			DataInputStream dataInputStream = null;

			//System.out.println("Button clicked, still not connection...");

			try {
				//System.out.println("Connecting...");

				socket = new Socket("138.250.194.200", 8888);

				//System.out.println("Connected");

				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
				dataInputStream = new DataInputStream(socket.getInputStream());

				//System.out.println("Sending data...");

				dataOutputStream.writeUTF(params[0]);
				dataOutputStream.writeUTF(params[1]);

				//System.out.println("Sent");

				return dataInputStream.readUTF();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataOutputStream != null) {
					try {
						dataOutputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (dataInputStream != null) {
					try {
						dataInputStream.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			return "";
		}

		protected void onPostExecute(String res) {
			EditText base = (EditText) findViewById(R.id.baseText);
			EditText power = (EditText) findViewById(R.id.powerText);
			TextView result = (TextView) findViewById(R.id.resultText);

			result.setText(base.getText().toString() + " to the power of "
					+ power.getText().toString() + " is " + res);

			System.out.println("Time elapsed: "
					+ (System.nanoTime() - beginning) + "ns");
		}

	}

}
