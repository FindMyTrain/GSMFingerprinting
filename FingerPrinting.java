/*******************************************************************************/
/************ java FingerPrinting Training_file Testing_file *******************/
/*******************************************************************************/


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FingerPrinting 
{
	public static final double R = 6372800; // In meters
	public static void main(String[] args) throws IOException 
	{
		/************************** Input file reading *****************************/
		int failed =0, total=0;
		String train = args[0], test = args[1];
        //System.out.println(train+", "+test);
		File fileGPS = new File(train);
		if (!(fileGPS.exists())) 
		{
			System.out.println("Training file doesn't exist");
			System.exit(0);
		}
		
		BufferedReader brTrain = new BufferedReader(new FileReader(fileGPS));
		
		FileWriter sensorGSM = new FileWriter("output.csv");

		String lines;
		String[] loc = null;
		double lat, lon, speed;
		long cellID, RSS, timeStamp;
		
		Map<String, String> gsm2gps = new HashMap<>();
		Map<String, ArrayList<String>> gsm2AvgGps = new HashMap<>();
		Map<Long, List<Long>> nearestRSS = new HashMap<>();
		
		ArrayList<String> gpsList = null;
		List<Long> rssList = null, b_list = null;
		lines = brTrain.readLine();	//useless read to omit 1st line
		while ((lines = brTrain.readLine()) != null)	{
			loc = lines.split(", ");
			lat = Double.parseDouble(loc[0]);
			lon = Double.parseDouble(loc[1]);
			speed = Double.parseDouble(loc[2]);
			cellID = Long.parseLong(loc[4]);
			RSS = Long.parseLong(loc[5]);
			timeStamp = Long.parseLong(loc[6]);
			//System.out.println(lat+", "+lon);
			/********************Fill up the map with Train value ************************/
			
			if(!gsm2AvgGps.containsKey(cellID+"_"+RSS))	gsm2AvgGps.put(cellID+"_"+RSS, new ArrayList<String>());
			gpsList= gsm2AvgGps.get(cellID+"_"+RSS);
			gpsList.add(lat+"_"+lon);
			gsm2AvgGps.put(cellID+"_"+RSS, gpsList);
					
			
			if(!nearestRSS.containsKey(cellID))	nearestRSS.put(cellID, new ArrayList<Long>());
			rssList= nearestRSS.get(cellID);
			rssList.add(RSS);
			nearestRSS.put(cellID, rssList);
		}
		
		
		
		/********* Sort the RSS values for a particular cell ID ********/		
		for (Long l : nearestRSS.keySet()) {
			b_list= nearestRSS.get(l);
			Collections.sort(b_list);
			nearestRSS.put(l, b_list);
		}
		
		/********* Store Mean Lat_Lon for a particular cellID_RSS pair *********/
		int count = 0;
		String lat_lon="";
		Map<String, String> gsm2gpsMean = new HashMap<>();
		for (String str : gsm2AvgGps.keySet()) {
			count=0;
			lat=0;
			lon=0;
			ArrayList<String> g = gsm2AvgGps.get(str);
			for (String gps : g) {
				lat += Double.parseDouble(gps.split("_")[0]);
				lon += Double.parseDouble(gps.split("_")[1]);
				count++;
			}
			lat_lon = (lat/count)+"_"+(lon/count);
			gsm2gpsMean.put(str, lat_lon);
		}
		
//		for (String l : gsm2gpsMean.keySet()) {
//			System.out.println(l.split("_")[0]+","+l.split("_")[1]+","+gsm2gpsMean.get(l).split("_")[0]+","+gsm2gpsMean.get(l).split("_")[1]);
//		}
		
		/************** Find GPS data from cellid_RSS pair of test data ************/
		File fileGSM = new File(test);
		if (!(fileGSM.exists())) 
		{
			System.out.println("Testing file doesn't exist");
			System.exit(0);
		}
		BufferedReader brTest = new BufferedReader(new FileReader(fileGSM));
		
		String pairGPS;
		ArrayList<String> gsm = new ArrayList<>();
		lines = brTest.readLine();	//useless read to omit 1st line
		while ((lines = brTest.readLine()) != null)	{
			loc = lines.split(", ");
			cellID = Long.parseLong(loc[4]);
			RSS = Long.parseLong(loc[5]);
			timeStamp = Long.parseLong(loc[6]);
			gsm.add(cellID+"_"+RSS+"_"+timeStamp);
		}
		if(gsm.size()==0)	return;
		for (String pairGSM : gsm) {
			cellID = Long.parseLong(pairGSM.split("_")[0]);
			RSS = Long.parseLong(pairGSM.split("_")[1]);
			//System.out.println(cellID + ", " + RSS);
			pairGPS = new FingerPrinting().searchMap(cellID, RSS, gsm2gpsMean, nearestRSS);
			if(pairGPS!="Sorry")
				sensorGSM.append(pairGPS.split("_")[0]+","+pairGPS.split("_")[1]+","+pairGSM.split("_")[2]+"\n");
			else failed++;
		}
		sensorGSM.close();
		System.out.println("Total # cases : "+gsm.size()+" & Total Not_found : "+ failed);
		System.out.println("Not able to search : "+((failed*100)/gsm.size())+" %\n");
		calculateError();
	}
	
	public String searchMap(long a, long b, Map<String, String> mp, Map<Long, List<Long>> keyList){
		if(mp.containsKey(a+"_"+b))	return mp.get(a+"_"+b);
		long nearest_b = myOtherBinSearch(b, keyList.get(a));
		if(mp.containsKey(a+"_"+nearest_b))	return mp.get(a+"_"+nearest_b);
		return "Sorry";
	}
	
	public long myBinSrch(long b, List<Long> keyList){
		int start = 0;
        int end = keyList.size() - 1;
        int mid = (start + end) / 2;
        
        if(b>keyList.get(end))	return keyList.get(end);
        
        while (start < end) {
        	if(end == start+1 && b<keyList.get(end) && b>keyList.get(start))	return keyList.get(end);
        	
        	mid = (start + end) / 2;
            if (b < keyList.get(mid)) {
                end = mid;
            }
            else	start = mid;
        }
		return keyList.get(end);
	}
	
	 public long myOtherBinSearch(long x, List<Long> keyList) {
		 int low = 0;
		 if(keyList == null)	return 0;
		    int high = keyList.size() - 1;

		    while (low < high) {
		        int mid = (low + high) / 2;
		        assert(mid < high);
		        long d1 = Math.abs(keyList.get(mid) - x);
		        long d2 = Math.abs(keyList.get(mid+1) - x);
		        if (d2 <= d1)
		        {
		            low = mid+1;
		        }
		        else
		        {
		            high = mid;
		        }
		    }
		    return keyList.get(high);
	    }
	
	
	public static long getTimeStamp(String time) {
		String[] t = time.split(":");
		int hh = Integer.parseInt(t[0]), mm = Integer.parseInt(t[1]), ss = Integer.parseInt(t[2]); 
		return ss + 60*mm + 3600*hh;
	}
	
	public static void calculateError()	throws FileNotFoundException, IOException{
		double lat1, lon1, lat2, lon2;
		long time1, time2;
		double error;
		ArrayList<Double> errList = new ArrayList<Double>();
		File gt = new File("test.csv"), expr = new File("output.csv");
		
		BufferedReader brGT = new BufferedReader(new FileReader(gt));
		BufferedReader brExp = new BufferedReader(new FileReader(expr));
		FileWriter errFile = new FileWriter("error.txt");
		
		ArrayList<String> gndTruth = new ArrayList<String>(), exp = new ArrayList<String>();
		String lines;
		lines = brGT.readLine();
		while((lines = brGT.readLine())!=null)	{
			gndTruth.add(lines.split(", ")[0]+","+lines.split(", ")[1]+","+lines.split(", ")[4]+"," +lines.split(", ")[5]+","+lines.split(", ")[6]);
		}
		while((lines = brExp.readLine())!=null)	{
			time1 = Long.parseLong(lines.split(",")[2]);
			exp.add(lines.split(",")[0]+","+lines.split(",")[1]+","+lines.split(",")[2]);
//			System.out.println(lines.split(",")[0]+","+lines.split(",")[1]+","+lines.split(",")[2]);
			for (String loc : gndTruth) {
//				System.out.println(loc.split(",")[2]);
				time2 = Long.parseLong(loc.split(",")[4]);
				if(time1==time2)	{
					lat1 = Double.parseDouble(loc.split(",")[0]);
					lon1 = Double.parseDouble(loc.split(",")[1]);
					lat2 = Double.parseDouble(lines.split(",")[0]);
					lon2 = Double.parseDouble(lines.split(",")[1]);
					error = haversine(lat1, lon1, lat2, lon2);
					errList.add(error);
					errFile.append(error+"\n");
					if(error>5000)	System.out.println(lat1+", "+lon1+", "+lat2+", "+lon2+": "+ error);
				}
			}
		}
		errFile.close();
		String errMS = getStdDev(errList);
		System.out.println("Mean Error : "+errMS.split("_")[1]+" & Std Dev : "+errMS.split("_")[0]+"\n------------------------------------------------");
	}
	
	public static String getStdDev (ArrayList<Double> list) {
		double mean = 0, var= 0;
		for (int j = 0; j <list.size(); j++) {
			mean += list.get(j);
		}
		mean /= list.size();
		for (int j = 0; j <list.size(); j++) {
			var += (list.get(j) - mean) * (list.get(j) - mean);
		}
		var /= list.size();
		return(Math.sqrt(var)+"_"+mean);
	}
	
 	public static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
 
        double a = Math.pow(Math.sin(dLat / 2),2) + Math.pow(Math.sin(dLon / 2),2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}


