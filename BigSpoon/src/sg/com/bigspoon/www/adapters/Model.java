package sg.com.bigspoon.www.adapters;

public class Model {
    
    private String name;
    private boolean selected;
    private String value;
    
    public Model(String name) {
        this.name = name;
        value="0";
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public void setValue(String value){
    	this.value=value;
    }
    
    public String getValue(){
    	return value;
    }
} 