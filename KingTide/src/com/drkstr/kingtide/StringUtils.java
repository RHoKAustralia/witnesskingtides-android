package com.drkstr.kingtide;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StringUtils {
	
	
	
	public StringUtils()
	{
		
	}
	
	/*
	 * For the King tide list class
	 */
	
	
	
	/*
	 * converting the selection to state letters to compare with the States from the json string
	 */
	public String getState(int position)
	{
		switch(position)
		{
		case 0:
			return "all";
		case 1:
			return "QLD";
		case 2:
			return "NSW";
		case 3:
			return "VIC";
		case 4:
			return "TAS";
		case 5:
			return "SA";
		case 6:
			return "WA";
		case 7:
			return "NT";
		}
		
	return null;	
	}
	
	
	/*
	 * Formating the string we get from the json string
	 */
	public String getDate(String date_json)
	{
			
		String date = date_json.substring(8)+" "
					 +getMonth(date_json.substring(5,7))
					 +" "
					 +date_json.substring(0,4);
						
						
						
		return date;
	}
	
	
	/*
	 * geting the month names from the date string that we get from the json stirng
	 */
	public String getMonth(String year)
	{		
		if(year.equalsIgnoreCase("01"))
			return "January";
		else if(year.equalsIgnoreCase("02"))
			return "February";
		else if(year.equalsIgnoreCase("03"))
			return "March";
		else if(year.equalsIgnoreCase("04"))
			return "April";
		else if(year.equalsIgnoreCase("05"))
			return "May";
		else if(year.equalsIgnoreCase("06"))
			return "June";
		else if(year.equalsIgnoreCase("07"))
			return "July";
		else if(year.equalsIgnoreCase("08"))
			return "August";
		else if(year.equalsIgnoreCase("09"))
			return "September";
		else if(year.equalsIgnoreCase("10"))
			return "October";
		else if(year.equalsIgnoreCase("11"))
			return "November";
		else 
			return "December";
		
	}
	
	/*
	 * For the Get_Pic class
	 */
	
	
	
	/*
	 * Formating the date stirng we get from the Pics meta data
	 */
	
	public String getDateFormated(String date)
	{
		
		String[] parts = date.split(" ");
		String formated_date = parts[0]+"T"+parts[1];
		
		
		return formated_date;

	}
	
	
	/*
	 * Converting the latitude and lngitude values from degrees, minutes and seconds to degrees
	 */
	public String getInDegrees(String attrLATITUDE, String attrLATITUDE_REF, String attrLONGITUDE, String attrLONGITUDE_REF)
	{
		
		double lat_double;
		double lng_double;
		
		 if((attrLATITUDE !=null) && (attrLATITUDE_REF !=null) && (attrLONGITUDE != null) && (attrLONGITUDE_REF !=null))
		 {
			 
			 if(attrLATITUDE_REF.equals("N"))
			    lat_double = convertToDegree(attrLATITUDE);
			 else
				lat_double = 0 - convertToDegree(attrLATITUDE);
			 
			 
			 
			 if(attrLONGITUDE_REF.equals("E"))
				lng_double = convertToDegree(attrLONGITUDE);
			 else
				lng_double = 0 - convertToDegree(attrLONGITUDE);

			 
			 
			 return String.valueOf(lat_double)+","+String.valueOf(lng_double);
			 
		 }
		 else
		 {
			 return null;
		 }
		
	}
	
	
	public Double convertToDegree(String stringDMS){
		 Double result = null;
		 String[] DMS = stringDMS.split(",", 3);

		 String[] stringD = DMS[0].split("/", 2);
		 Double D0 = Double.valueOf(stringD[0]);
		 Double D1 = Double.valueOf(stringD[1]);
		 Double D = D0/D1;

		 String[] stringM = DMS[1].split("/", 2);
		 Double M0 = Double.valueOf(stringM[0]);
		 Double M1 = Double.valueOf(stringM[1]);
		 Double M = M0/M1;

		 String[] stringS = DMS[2].split("/", 2);
		 Double S0 = Double.valueOf(stringS[0]);
		 Double S1 = Double.valueOf(stringS[1]);
		 Double S = S0/S1;

		    result = D + (M/60) + (S/3600);

		 return result;


		}
	/*
	 * For making java obj out of strings from the Upload class
	 */
	
	
	@Expose
	@SerializedName("Email")
	
	private String Email = null;
	public void setEmail(String Email) 
	{
		this.Email = Email;
	}

	@Expose
	@SerializedName("FirstName")
	
	private String Name = null;
	public void setName(String name) 
	{
		this.Name = name;
	}

	@Expose
	@SerializedName("Description")
	
	private String desc = null;
	public void setDesc(String desc) 
	{
		this.desc = desc;
	}


	@Expose
	@SerializedName("Latitude")
	private String lat = "null";
	
	public void setlat(String lat) 
	{
		this.lat = lat;
	}

	@Expose
	@SerializedName("Longitude")
	private String lng = "null";
	
	public void setlng(String lng) 
	{
		this.lng = lng;
	}

	@Expose
	@SerializedName("CreationTime")
	private String time = null;
	
	public void settime(String date) 
	{
		this.time = date;
	}

	@Expose
	@SerializedName("Photo")
	private String pic = null;
	
	public void setPhoto(String pic) 
	{
		this.pic = pic;
	}

	
	
	
}


