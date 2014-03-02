package com.moga.example;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;

public class PWMTask extends AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... input) {
		//TODO: get info and parse web
		System.out.println("PWNTASK");
		try{
			Socket s = null;
			PrintWriter out;
			s = new Socket("192.168.1.135", 8181);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
			out.println(input[0]);
			out.flush();
			out.close();
			s.close();
		}catch(Exception e){
			System.out.println("PWM error " + e.toString());
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
	
	@Override
	protected void onPostExecute(String result) {
		
	}



}