import java.io.*;
import java.util.*;

public class neighborTest {
	public static void main(String[] args)
	{
		long startTime = System.nanoTime();
		int canEst = 0;
		int cannotEst = 0;
		double newRatEst=0;
		double RMS=0;
		double RMSSum=0;
	
		
		Map<String, List<ItemInfo>> userMap = new HashMap<String, List<ItemInfo>>();
		Map<String, similar[]> neighborMap = new HashMap<String, similar[]>();
		
		System.out.println("Enter the path to where the ratings csv file is located:");
		Scanner keyboard = new Scanner(System.in);
		String ratings = keyboard.nextLine();
		BufferedReader br = null;
		String line = "";
		try {

			br = new BufferedReader(new FileReader(ratings));
			while ((line = br.readLine()) != null) 
			{
				String[] info = line.split(",");
				int u, i, r;
				u = Integer.parseInt(info[0]);
				i = Integer.parseInt(info[1]);
	            r = Integer.parseInt(info[2]);
	           
	            ItemInfo itemInfo = new ItemInfo(i, r);
				List<ItemInfo> l = userMap.get(info[0]);
				if(l==null)
				{
					userMap.put(info[0], l=new ArrayList<ItemInfo>());
				}
				l.add(itemInfo);
	            	            
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		double similarityValue = 0;
		double squaredDifference = 0;
		double numberOfSims = 0; 
		
		System.out.println("Enter the neighborhood size: ");
		int neighborhoodSize = keyboard.nextInt();
		similar[] neighbors = new similar[neighborhoodSize];

		for(Map.Entry<String, List<ItemInfo>> entry : userMap.entrySet())
		{
			
			for(int x = 0; x < neighborhoodSize; x ++)
			{
				neighbors[x]=null;	
			}
		
			String userNum = entry.getKey();
			List<ItemInfo> ir = entry.getValue();
			int size = ir.size();
			for(Map.Entry<String, List<ItemInfo>> otherEntry : userMap.entrySet())
			{
				
				similarityValue = 0;
				squaredDifference = 0;
				numberOfSims = 0; 
				String otherUserNum = otherEntry.getKey();
				if(otherUserNum != userNum)
				{
					List<ItemInfo> oir = otherEntry.getValue();
					int otherSize = oir.size();
					for(int k = 0; k < otherSize; k++)
					{
						for(int l = 0; l < size; l++)
						{
							if(ir.get(l).getNumber() == oir.get(k).getNumber())
							{
								squaredDifference = Math.pow(ir.get(l).getRating()-oir.get(k).getRating(),2);
								numberOfSims++;
							}	
						}
					}
					similarityValue = squaredDifference/numberOfSims; 
					similar s = new similar(otherUserNum, similarityValue);
					for(int m = 0; m < neighborhoodSize; m ++)
					{
						if(neighbors[m] == null)
						{
							neighbors[m] = s;
						}
						else if(neighbors[m].getSim() > s.getSim());
						{
							similar temp = neighbors[m];
							neighbors[m] = s;
							s = temp;
						}
						
					}
				}
			}
			neighborMap.put(userNum,neighbors);
		}
				
		System.out.println("Enter name and path for new csv file to be created: ");
		String newFileName = keyboard.nextLine();
		try
		{
		FileWriter writer = new FileWriter(newFileName);
		
		writer.write("user id, item id, actual rating, predicted rating, RMSE");
		writer.write("\r\n");
			
		for(Map.Entry<String, List<ItemInfo>> entryAgain : userMap.entrySet())
		{
			String un = entryAgain.getKey();
			List<ItemInfo> itIn = entryAgain.getValue();
			int testSize = itIn.size();
			similar[] neigTest = neighborMap.get(un);
			for(int y = 0; y < testSize; y ++)
			{
				double testSum = 0;
				double testCount = 0;
				double testItemNum = itIn.get(y).getNumber();
				double testRatNum = itIn.get(y).getRating();
				for(int z = 0; z < neighborhoodSize; z++)
				{ 
					List<ItemInfo> testItIn = userMap.get(neigTest[z].getUserNum());
					int testItSize = testItIn.size();
					for(int a = 0; a < testItSize; a ++)
					{
						if(testItemNum == testItIn.get(a).getNumber())
						{
							testSum = testItIn.get(a).getRating();
							testCount++;
						}	
							
					}	
				}
				if(testCount == 0)
				{	
					cannotEst ++;
					writer.write(un +','+ testItemNum +','+ testRatNum +','+ "null" +','+ "null");
					writer.write("\r\n");
				}
				else
				{
					canEst ++;
					newRatEst = testSum/testCount;
					RMS = Math.sqrt(Math.pow(newRatEst-testRatNum,2));
					RMSSum = RMSSum + RMS;
					writer.write(un+','+testItemNum+','+testRatNum+','+newRatEst+','+RMS);
					writer.write("\r\n");
				}
			}
		}	
			
		writer.flush();
		writer.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}	
			
			
			
		System.out.println("For a neighborhood size of " + neighborhoodSize + " : ");
		System.out.println("Number that cannot be estimated: "+cannotEst);
		System.out.println("Number that can be estimated: "+canEst);
		System.out.println("Average RMS: " + RMSSum/canEst);
		long endTime =System.nanoTime();
		System.out.println("Took " + (endTime-startTime) + " ns");
			
	}
}
