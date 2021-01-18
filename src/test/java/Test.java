import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.vspl.music.util.DateUtil;

class Test {

	public static void main(String s[]) throws ParseException {
  
		/*String a = "_23a";
		if(a.matches("^[0-9]*[A-Za-z][0-9]*+(?:[ _-][A-Za-z0-9]+)*$")){
			//System.out.println("matched");
		}
		else{
			//System.out.println("No matched");
		}

		int[] list1 = {1,2,3,4,5};
		int[] list2 = {1,2,3,4,5};*/
		float[] a = new float[]{1f,2f};
		
		//System.out.println(a);
		
		
		Calendar calendar = Calendar.getInstance();
	    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
	    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	    String outputDate = simpleDateFormat.format(calendar.getTime());
	    //System.out.println("outputDate:"+outputDate);
	    //System.out.println("getTime:"+calendar.getTime());
	    
	    String sDate6 = "2019-11-29";  
        SimpleDateFormat formatter6=new SimpleDateFormat("yyyy-MM-dd");
        Date date6 = formatter6.parse(sDate6);
        //System.out.println("formatted date: "+date6.toString());
        
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        //System.out.println("ffff:"+simpleDateFormat2.parse("2019-11-29"));
        Date myDate = simpleDateFormat2.parse("2019-11-29");
        //System.out.println(new SimpleDateFormat("MM-dd-yyyy").format(myDate));
        //System.out.println(new SimpleDateFormat("yyyy-MM-dd").format(myDate));
        
        
        String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	    	String password = "";
	    	for(int l = 0; l < 2; l++) {
	    		password += abc.charAt(new Random().nextInt(abc.length()));
		    }
	    	password += 1000 + new Random().nextInt(90000);
	    	//System.out.println(  password );
	    	System.out.println(  password.substring(0, 5) );
	    	//System.out.println(  password.substring(5 + 1)  );
	    
	}
}