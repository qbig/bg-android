package sg.com.bigspoon.www.data;

public class MenuListTextItem {   
    private  String itemnames,itemdesc,itemprice;  
    private  int itemnumber;
    public String getItemName() {  
        return itemnames;  
    }  

	public void setItemname(String string) {  
    	itemnames = string;  
    }  

    public String getItemDesc() {  
        return itemdesc;  
    }  

    public void setItemDesc(String string) {  
    	itemdesc = string;  
    }  
    
    public String getItemPrice() {  
        return itemprice;  
    }  

    public void setItemPrice(String string) {  
    	itemprice = string;  
    }  
    
    public void setNumber(int num) {
		this.itemnumber=num;			
	}
    
    public int getNumber() {
		return itemnumber;			
	}
    
}  