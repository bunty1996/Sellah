package com.app.admin.sellah.controller.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validations {
	SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MMM-dd" );
	public static boolean match(String str)
	{
		Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
		Matcher ms = ps.matcher(str);
	    boolean bs = ms.matches();
	    return bs;
	}

	private boolean isValidEmaillId(String email){

	    return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
	              + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	              + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
	              + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	              + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
	              + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
	     }
	
	public boolean userLoginvalidate(Context context, String username, String userpassword) {
		if(username.length()==0 || userpassword.length()==0 )
		{
			String message  = "Please fill all fields";
			showMessageOKCancel(context, message);
			return false;
		}
		
		else if (username.length()==0) {
			String message  = "Please fill username field";
			showMessageOKCancel(context, message);
			return false;
		} 


		
		else if (userpassword.length()==0) {
			String message  = "Please fill password field";
			showMessageOKCancel(context, message);
			return false;
		} 
		

		 else if( userpassword.length() < 5)
			{
			
			 String message  = "The password you've entered is incorrect";
				showMessageOKCancel(context, message);
				return false;
			
			}

		else
		{
		return true;
		}	
	}
	




	public  boolean stripevalid(Context context, String first,  String last, String dob,String pid,  String address, String country,String state,String city,  String postalcode, String currency,String acc,String routing)
	{

		if(first.length()==0 && last.length()==0 && dob.length()==0 &&  pid.length()==0  && address.length()==0 &&  country.length()==0 && state.length()==0 && city.length()==0 &&  postalcode.length()==0 && currency.length()==0 && acc.length()==0 &&  routing.length()==0 )
		{
			String message  = "Please fill all fields";
			showMessageOKCancel(context, message);
			return false;

		}

		else if (first.length()==0) {
			String message  = "Please enter first name.";
			showMessageOKCancel(context, message);
			return false;
		}


		else if (last.length()==0) {
			String message  = "Please enter last name.";
			showMessageOKCancel(context, message);
			return false;
		}


		else if (dob.length()==0) {
			String message  = "Please enter date of birth";
			showMessageOKCancel(context, message);
			return false;
		}

		else if (pid.length()==0) {
			String message  = "Please enter personal ID";
			showMessageOKCancel(context, message);
			return false;
		}



		else if (address.length()==0) {
			String message  = "Please enter address";
			showMessageOKCancel(context, message);
			return false;
		}
		else if (country.length()==0) {
			String message  = "Please enter country";
			showMessageOKCancel(context, message);
			return false;
		}

		else if (state.length()==0) {
			String message  = "Please enter state";
			showMessageOKCancel(context, message);
			return false;
		}
		else if (city.length()==0) {
			String message  = "Please enter city";
			showMessageOKCancel(context, message);
			return false;
		}

		else if (postalcode.length()==0) {
			String message  = "Please enter postal code";
			showMessageOKCancel(context, message);
			return false;
		}

		else if (currency.length()==0) {
			String message  = "Please enter currency";
			showMessageOKCancel(context, message);
			return false;
		}

		else if (acc.length()==0) {
			String message  = "Please enter account number";
			showMessageOKCancel(context, message);
			return false;
		}
		else if (routing.length()==0) {
			String message  = "Please enter routing number";
			showMessageOKCancel(context, message);
			return false;
		}
		else
			return true;
	}


	public boolean productinfo_validate(Context context, String name,String category,String sub_Cat,String price,String type,String condition,String quantity)
	{

		if(name.length()==0 ){

			String message  = "Please enter Product Name!";
			showtoast(context, message);
			return false;

		}

		if(category.equalsIgnoreCase("Select Category") ){

			String message  = "Please select an Category!";
			showtoast(context, message);
			return false;

		}

		if(sub_Cat.equalsIgnoreCase("Select Sub-Category") ){

			String message  = "Please select an Sub-Category!";
			showtoast(context, message);
			return false;

		}

		if(price.length()==0 || price.equalsIgnoreCase("0.00") || price.equalsIgnoreCase("0")){

			String message  = "Please enter Product Price!";
			showtoast(context, message);
			return false;

		}
		if(type.equalsIgnoreCase("Selecy Type") ){

			String message  = "Please select Product Type!";
			showtoast(context, message);
			return false;

		}

		if(condition.equalsIgnoreCase("Select Condition") ){

			String message  = "Please select Product Condition!";
			showtoast(context, message);
			return false;

		}


		if(quantity.length()==0 ){

			String message  = "Please enter Product Quantity!";
			showtoast(context, message);
			return false;

		}




		else {
			return true;
		}



	}



	private void showtoast(Context context, String message) {
		Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
	}
		private void showMessageOKCancel(Context context, String message) {
		new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK).
		setTitle("Alert !")
		.setMessage(message)
		 .setPositiveButton("Ok", new OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				
			
			}
		}).
		create().show();
	}


	
}
