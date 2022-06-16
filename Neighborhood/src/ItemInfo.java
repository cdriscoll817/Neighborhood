
public class ItemInfo {
	public int itemNumber;
	public int itemRating;
	
	public ItemInfo(int n, int r)
	{
		itemNumber = n;
		itemRating = r;
	}
	
	public int getNumber()
	{
		return itemNumber;
	}
	
	public int getRating()
	{
		return itemRating;
	}
	
	public void setNumber(int n)
	{
		itemNumber = n;
	}
	
	public void setRating(int r)
	{
		itemRating = r;
	} 
}
