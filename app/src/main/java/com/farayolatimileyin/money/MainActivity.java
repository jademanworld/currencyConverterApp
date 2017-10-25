package com.farayolatimileyin.money;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.net.*;
import android.os.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.util.*;
import okhttp3.*;
import org.json.*;

import android.app.AlertDialog;
import java.text.*;
import java.math.*;

public class MainActivity extends AppCompatActivity
{
	Snackbar sb;
	ListView btcList;
	ListView ethList;
	ProgressDialog progress;

	String btcUrl = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=USD,EUR,NGN,KRW,HKD,CHF,NZD,CAD,BRL,AUD,JPY,GBP,CNY,GHS,ISK,INR,ILS,RUB,ZAR,SAR";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		load();

		//		Finding Views and Button by ID from the Activity's main.xml layout
		btcList = (ListView) findViewById(R.id.lid);
		ethList = (ListView) findViewById(R.id.lst);


    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// TODO: Implement this method
		getMenuInflater().inflate(R.menu.home_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// TODO: Implement this method
		int choice = item.getItemId();
		switch (choice)
		{
			case R.id.refresh:
				load();
				break;
		}
		return super.onOptionsItemSelected(item);
	}






	public void load(View v)
	{

		//					Checks if the Android device is connected to the Internet in order to get the List since Internet is required
		ConnectivityManager check = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = check.getActiveNetworkInfo();

//					If Android Device is connected to the Internet, the Operation will be executed in the backgroud thread using AsynTask
		if ((activeNetwork != null) && (activeNetwork.isConnected()))
		{
			BgTask bgt = new BgTask();
			bgt.execute();

		}

//					If Android Device is not connected to the Internet, this SnackBar will be Displayed
		else
		{
			sb = Snackbar.make(getWindow().getDecorView().getRootView(), "No Connection to the Internet", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener(){
					@Override
					public void onClick(View v)
					{
						load();
					}
				});
			sb.setActionTextColor(Color.RED);
			sb.show();
		}
	}

	public void load()
	{

		//					Checks if the Android device is connected to the Internet in order to get the List since Internet is required
		ConnectivityManager check = (ConnectivityManager) getBaseContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = check.getActiveNetworkInfo();

//					If Android Device is connected to the Internet, the Operation will be executed in the backgroud thread using AsynTask
		if ((activeNetwork != null) && (activeNetwork.isConnected()))
		{
			BgTask bgt = new BgTask();
			bgt.execute();

		}

//					If Android Device is not connected to the Internet, this SnackBar will be Displayed
		else
		{
			sb = Snackbar.make(getWindow().getDecorView().getRootView(), "No Connection to the Internet", Snackbar.LENGTH_INDEFINITE).setAction("RETRY", new View.OnClickListener(){
					@Override
					public void onClick(View v)
					{
						load(v);
					}
				});
			sb.setActionTextColor(Color.RED);
			sb.show();
		}
	}

// format method to format conversion result to 3 decimal places
	public String format(Number n)
	{
		NumberFormat format = DecimalFormat.getInstance();    
		format.setRoundingMode(RoundingMode.FLOOR);    
		format.setMinimumFractionDigits(0);      
		format.setMaximumFractionDigits(3);
		return format.format(n);
	}

// getDrawable method that gets drawable resource available in the android res/drawable folder with the name passed in 
	public Drawable getDrawable(String name)
	{
		Context context = com.farayolatimileyin.money.MainActivity.this;
		int resourceId = context.getResources().getIdentifier(name, "drawable", MainActivity.this.getPackageName());
		return context.getResources().getDrawable(resourceId);
	}


// This inner class handles the background task of fecthing the data from the API 
	public class BgTask extends AsyncTask<String, Void, String>
	{
		OkHttpClient client = new OkHttpClient();
		@Override
		protected String doInBackground(String... p1)
		{
			// TODO: Implement this method
			okhttp3.Request req = new Request.Builder().url(btcUrl).get().build();
			String result="";
			try
			{
				okhttp3.Response res = client.newCall(req).execute();
				result = res.body().string();
				return result;
			}
			catch (IOException e)
			{}
			return null;
		}

		@Override
		protected void onPreExecute()
		{
			// TODO: Implement this method
			progress = new ProgressDialog(MainActivity.this);
			progress.setTitle("Fetching");
			progress.setMessage("Please wait while I fetch you the latest rates");
			progress.setCancelable(true);
			progress.setCanceledOnTouchOutside(false);
			progress.show();
			super.onPreExecute();
		}

		@Override
		protected void onPostExecute(String result)
		{
			// TODO: Implement this method
			progress.cancel();
			ArrayList<CurrencyDataClass> btcArrayList = new ArrayList<>();
			ArrayList<CurrencyDataClass> ethArrayList = new ArrayList<>();
			CurrencyDataClass btcCurrencyData = null;
			CurrencyDataClass ethCurrencyData = null;

			// Parsing through the JSON data to extract the currencies info
			try
			{
				JSONObject jobj = new JSONObject(result);
				JSONObject btcJsonObj = jobj.getJSONObject("BTC");
				JSONObject ethJsonObj = jobj.getJSONObject("ETH");

				Iterator<String>  btckeys = btcJsonObj.keys();
				while (btckeys.hasNext())
				{

					String btckey =  btckeys.next();
					Double btcRate = btcJsonObj.getDouble(btckey);
					String imageName = btckey.toLowerCase() + "1";
					String cryptoName = "BTC";

					// Adding the bitcoin currency data to the class data holder { CurrencyDataClass }
					btcCurrencyData = new CurrencyDataClass(btckey, btcRate, imageName, cryptoName);
					btcArrayList.add(btcCurrencyData);


				}

				Iterator<String>  ethkeys = ethJsonObj.keys();
				while (ethkeys.hasNext())
				{

					String ethkey =  ethkeys.next();
					Double ethRate = ethJsonObj.getDouble(ethkey);
					String imageName = ethkey.toLowerCase() + "1";
					String cryptoName = "ETH";


					// Adding the etherum currency data to the class data holder { CurrencyDataClass }
					ethCurrencyData = new CurrencyDataClass(ethkey, ethRate, imageName, cryptoName);
					ethArrayList.add(ethCurrencyData);


				}

			}
			catch (JSONException e)
			{}

			// Making use of our Customer Adapter to hold currency data for our ListViews
			CustomAdapter btcAdapter = new CustomAdapter(MainActivity.this, btcArrayList);
			CustomAdapter ethAdapter = new CustomAdapter(MainActivity.this, ethArrayList);

			btcList.setAdapter(btcAdapter);
			ethList.setAdapter(ethAdapter);

// Setting onItemClickListener on the Bitcoin ListView
			btcList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					public void onItemClick(AdapterView<?> parent, View v, int position, long id)
					{
						String currencyinfo = parent.getItemAtPosition(position).toString();
						String[] split = currencyinfo.split(" = ");
						final String cryptoCurrency = split[0];
						final Double exRate = Double.parseDouble(split[1]);
						final String fxCurrency = split[2];

						//Inflating the Layout to be used on the AlertDialog box for currency conversion
						LayoutInflater inflater = getLayoutInflater();
						View conversionLayout = inflater.inflate(R.layout.conversionlayout, null);
						final EditText cryptoC = (EditText) conversionLayout.findViewById(R.id.cryptocurrency);
						cryptoC.setHint(cryptoCurrency);

						final EditText fxC = (EditText) conversionLayout.findViewById(R.id.fxcurrency);
						fxC.setHint(fxCurrency);
						final TextView result = (TextView) conversionLayout.findViewById(R.id.result);

						ImageView cryptimage = (ImageView) conversionLayout.findViewById(R.id.cryptoimage);
						cryptimage.setImageDrawable(getDrawable("bitcoin"));

						ImageView fximage = (ImageView) conversionLayout.findViewById(R.id.fximage);
						fximage.setImageDrawable(getDrawable(fxCurrency.toLowerCase() + "1"));

						cryptoC.addTextChangedListener(new TextWatcher(){
								public void beforeTextChanged(CharSequence p, int start, int count, int after)
								{

								}

								public void onTextChanged(CharSequence s, int start, int count, int after)
								{


								}

								public void afterTextChanged(Editable p)
								{
									fxC.getText().clear();
									if (p.toString().length() == 0 || p.toString().isEmpty() || p.toString().startsWith("."))
									{
										result.setText("0.00");
										return;
									}
									else
									{
										Double fxrate = exRate * Double.parseDouble(p.toString());
										if (fxrate < 1)
										{
											DecimalFormat df = new DecimalFormat("#.#####");
											result.setText(p.toString() + "" + cryptoCurrency + " = " + df.format(fxrate) + "" + fxCurrency);
											return;
										}
										else
										{
											result.setText(p.toString() + "" + cryptoCurrency + " = " + format(fxrate) + "" + fxCurrency);
										}
									}
								}

							});

						fxC.addTextChangedListener(new TextWatcher(){

								public void beforeTextChanged(CharSequence p, int start, int count, int after)
								{

								}

								public void onTextChanged(CharSequence s, int start, int count, int after)
								{


								}

								public void afterTextChanged(Editable p)
								{
									cryptoC.getText().clear();
									if (p.toString().length() == 0 || p.toString().isEmpty() || p.toString().startsWith("."))
									{
										result.setText("0.00");
										return;
									}
									else
									{
										Double cryptoRate = Double.parseDouble(p.toString()) / exRate;
										if (cryptoRate < 1)
										{
											DecimalFormat df = new DecimalFormat("#.#####");
											result.setText(p.toString() + "" + fxCurrency + " = " + df.format(cryptoRate) + "" + cryptoCurrency);
											return;
										}
										else
										{
											result.setText(p.toString() + "" + fxCurrency + " = " + format(cryptoRate) + "" + cryptoCurrency);
										}
									}
								}


							});


//Making an AlertDialog to allow conversion of Currencies
						AlertDialog.Builder conversionDialog = new AlertDialog.Builder(MainActivity.this);
						conversionDialog.setTitle("1 " + cryptoCurrency + " exchanges for " + Double.toString(exRate) + " " + fxCurrency);
						conversionDialog.setView(conversionLayout);
						conversionDialog.setCancelable(false);
						conversionDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface d1, int p1)
								{
									return;
								}

							});
						AlertDialog convert = conversionDialog.create();
						convert.show();

					}

				});
// setting onItemClickListener on the Etherum ListView
			ethList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

					public void onItemClick(AdapterView<?> parent, View v, int position, long id)
					{
						String currencyinfo = parent.getItemAtPosition(position).toString();
						String[] split = currencyinfo.split(" = ");
						final String cryptoCurrency = split[0];
						final Double exRate = Double.parseDouble(split[1]);
						final String fxCurrency = split[2];

						// Inflating a Layout for the AlertDialog 
						LayoutInflater inflater = getLayoutInflater();
						View conversionLayout = inflater.inflate(R.layout.conversionlayout, null);
						final TextView result = (TextView) conversionLayout.findViewById(R.id.result);

						final EditText cryptoC = (EditText) conversionLayout.findViewById(R.id.cryptocurrency);
						cryptoC.setHint(cryptoCurrency);

						final EditText fxC = (EditText) conversionLayout.findViewById(R.id.fxcurrency);
						fxC.setHint(fxCurrency);
						cryptoC.addTextChangedListener(new TextWatcher(){
								public void beforeTextChanged(CharSequence p, int start, int count, int after)
								{

								}

								public void onTextChanged(CharSequence s, int start, int count, int after)
								{


								}

								public void afterTextChanged(Editable p)
								{
									fxC.getText().clear();
									if (p.toString().length() == 0 || p.toString().isEmpty() || p.toString().startsWith("."))
									{
										result.setText("0.00");
										return;
									}
									else
									{
										Double fxrate = exRate * Double.parseDouble(p.toString());
										if (fxrate < 1)
										{
											DecimalFormat df = new DecimalFormat("#.#####");
											result.setText(p.toString() + "" + cryptoCurrency + " = " + df.format(fxrate) + "" + fxCurrency);
											return;
										}
										else
										{
											result.setText(p.toString() + "" + cryptoCurrency + " = " + format(fxrate) + "" + fxCurrency);
										}
									}
								}

							});


						ImageView cryptimage = (ImageView) conversionLayout.findViewById(R.id.cryptoimage);
						cryptimage.setImageDrawable(getDrawable("etherum"));

						fxC.addTextChangedListener(new TextWatcher(){

								public void beforeTextChanged(CharSequence p, int start, int count, int after)
								{

								}

								public void onTextChanged(CharSequence s, int start, int count, int after)
								{


								}

								public void afterTextChanged(Editable p)
								{
									cryptoC.getText().clear();
									if (p.toString().length() == 0 || p.toString().isEmpty() || p.toString().startsWith("."))
									{
										result.setText("0.00");
										return;
									}
									else
									{
										Double cryptoRate = Double.parseDouble(p.toString()) / exRate;
										if (cryptoRate < 1)
										{
											DecimalFormat df = new DecimalFormat("#.#####");
											result.setText(p.toString() + "" + fxCurrency + " = " + df.format(cryptoRate) + "" + cryptoCurrency);
											return;
										}
										else
										{
											result.setText(p.toString() + "" + fxCurrency + " = " + format(cryptoRate) + "" + cryptoCurrency);
										}
									}
								}


							});


						ImageView fximage = (ImageView) conversionLayout.findViewById(R.id.fximage);
						fximage.setImageDrawable(getDrawable(fxCurrency.toLowerCase() + "1"));



//Making an AlertDialog to allow conversion of Currencies
						AlertDialog.Builder conversionDialog = new AlertDialog.Builder(MainActivity.this);
						conversionDialog.setTitle("1 " + cryptoCurrency + " exchanges for " + Double.toString(exRate) + " " + fxCurrency);
						conversionDialog.setView(conversionLayout);
						conversionDialog.setCancelable(false);
						conversionDialog.setNegativeButton("CLOSE", new DialogInterface.OnClickListener(){
								public void onClick(DialogInterface d1, int p1)
								{
									return;
								}

							});
						AlertDialog convert = conversionDialog.create();
						convert.show();


					}

				});


			super.onPostExecute(result);
		}


	}



	/*	This is a inner CustomAdapter Class that extends the ArrayAdapter 
	 it will serve as the Adapter for our  ListView
	 */
	public class CustomAdapter extends ArrayAdapter<CurrencyDataClass> 
	{
		public View listItemView;
		public CustomAdapter(Activity context, ArrayList<CurrencyDataClass> currencydetails)
		{
			super(context, 0, currencydetails);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			listItemView = convertView;
			if (listItemView == null)
			{
				listItemView = LayoutInflater.from(getContext()).inflate(
					R.layout.currenclayout, parent, false);
			}

			final CurrencyDataClass currentCurrency = getItem(position);

			// Finding the TextViews in the currenclayout.xml layout with their respective IDs and setting their respective informations for display to user
			final TextView cName = (TextView) listItemView.findViewById(R.id.currname);
			cName.setText(currentCurrency.getCurrencyName());

			final TextView cRate = (TextView) listItemView.findViewById(R.id.currvalue);
			cRate.setText(Double.toString(currentCurrency.getCurrencyRate()));

			ImageView cImage = (ImageView) listItemView.findViewById(R.id.currImage);
			cImage.setImageDrawable(getDrawable(currentCurrency.getCurrencyImage()));

			ImageView cryptmage = (ImageView) listItemView.findViewById(R.id.cryptmage);
			String str = currentCurrency.getCryptoCname().toLowerCase();
			if (str.startsWith("b"))
			{
				cryptmage.setImageDrawable(getDrawable("bitcoin"));
			}
			else if (str.startsWith("e"))
			{
				cryptmage.setImageDrawable(getDrawable("etherum"));
			}

			return listItemView;

		}


	}

}
