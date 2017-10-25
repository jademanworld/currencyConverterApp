/*
	This is a class for holding a Currency data
*/

package com.farayolatimileyin.money;
import android.graphics.drawable.*;

public class CurrencyDataClass
{
	// Instance Variables
	private String currencyName;
	private Double currencyRate;
	private String currencyImage;
	private String cryptoCname;

	// Class Constructor
	public CurrencyDataClass(String currencyName, Double currencyRate, String currencyImage, String cryptoCname)
	{
		this.currencyName = currencyName;
		this.currencyRate = currencyRate;
		this.currencyImage = currencyImage;
		this.cryptoCname = cryptoCname;
		
	}

	// Getter for CurrencyName
	public String getCurrencyName()
	{
		return currencyName;
	}

	//Getter for currencyRate
	public Double getCurrencyRate()
	{
		return currencyRate;
	}
	
	//Getter for currencyImage
	public String getCurrencyImage()
	{
		return currencyImage;
	}
	
	//Getter for cryptoCname
	public String getCryptoCname()
	{
		return cryptoCname;
	}

	//Overriden toString method of the class
	@Override
	public String toString()
	{
		// TODO: Implement this method
		String str = cryptoCname + " = "+currencyRate + " = "+currencyName;
		return str;
	}
	
	


}
