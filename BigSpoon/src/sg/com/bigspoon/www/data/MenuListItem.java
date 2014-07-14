package sg.com.bigspoon.www.data;

import android.graphics.drawable.Drawable;

public class MenuListItem {  
    private  Drawable image;  
    private  String itemnames,itemdesc,itemprice;  
    //private int itemnumber;
    public Drawable getImage() {  
        return image;  
    }  

	public void setImage(Drawable image) {  
        this.image = image;  
    }  
    
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
    
    /*public void setNumber(int number) {
		this.itemnumber=number;
		
	}
    
    public int getNumber(){
    	return itemnumber;
    }*/
    
}  
