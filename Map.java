import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Map
{
	public static void main(String[] args) throws IOException 
	{
		/************************** Input file reading *****************************/
		String gps = "sensorGPS.csv", cidrss = "sensorCID.csv", lines, lines2;
		File fileGPS = new File(gps);
		File fileGSM = new File(cidrss);
		if (!(fileGPS.exists() && fileGSM.exists())) 
		{
			System.out.println("Input file doesn't exist");
			System.exit(0);
		}

		@SuppressWarnings("resource") 
		BufferedReader brGPS = new BufferedReader(new FileReader(fileGPS));
		@SuppressWarnings("resource")
		BufferedReader brGSM = new BufferedReader(new FileReader(fileGSM));
		
		String[] word1 = null, word2 = null;
		
		double lat=0, lon=0, speed=0;
		long cellID=0, RSS=0, timeGPS, timeGSM;
		long  min, t1=0,t2=0;
		
		
		while ((lines = brGSM.readLine()) != null)	{
			word1 = lines.split(",");
			cellID = Long.parseLong(word1[0]);
			RSS = Long.parseLong(word1[1]);
			timeGSM = Long.parseLong(word1[2]);
			min = 10000000;
			while((lines2 = brGPS.readLine()) != null)	{
				word2 = lines2.split(",");
				timeGPS = Long.parseLong(word2[3]);
				if(Math.abs(timeGPS - timeGSM) < min)	{
					min = Math.abs(timeGPS - timeGSM);
					lat = Double.parseDouble(word2[0]);
					lon = Double.parseDouble(word2[1]);
					speed = Double.parseDouble(word2[2]);
					t1=timeGPS;
					t2=timeGSM;
				}
			}
			System.out.println(lat+", "+lon+", "+speed+", "+t1+", "+cellID+", "+RSS+", "+t2);	
			brGPS = new BufferedReader(new FileReader(fileGPS));
		}
		
	}
}

