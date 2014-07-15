package sg.com.bigspoon.www.adapters;

import java.util.Arrays;
import java.util.Comparator;

import com.koushikdutta.ion.Ion;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.R.id;
import sg.com.bigspoon.www.R.layout;
import sg.com.bigspoon.www.data.CategoryModel;
import sg.com.bigspoon.www.data.CategoryOrder;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoriesAdapter extends ArrayAdapter<CategoryModel>{
	Context context;
	private OutletDetailsModel outletDetails;
	
	public CategoriesAdapter(Context context, final OutletDetailsModel outletDetails) {
		super(context, R.layout.category_row,R.id.categoryname, outletDetails.categoriesDetails);
		this.context=context;
		this.outletDetails = outletDetails;
		
		Arrays.sort(outletDetails.categoriesDetails, new Comparator<CategoryModel>() {
			public int compare(CategoryModel cat1, CategoryModel cat2) {
				int cat1Index = 0;
				int cat2Index = 0;
				for (int i = 0, len = outletDetails.categoriesOrder.length; i < len; i++){
					if(outletDetails.categoriesOrder[i].categoryId == cat1.id){
						cat1Index = outletDetails.categoriesOrder[i].position;
					}
					
					if(outletDetails.categoriesOrder[i].categoryId == cat2.id){
						cat2Index = outletDetails.categoriesOrder[i].position;
					}
				}
				return cat1Index - cat2Index;
			}
		});
	}

	private String getPhotoUrl(int categoryId) {
		for(int i = 0, len = outletDetails.dishes.length; i < len ; i ++){
			if( outletDetails.dishes[i].categories[0].id == categoryId) {
				return outletDetails.dishes[i].photo.thumbnail;
			}
		}
		return null;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final CategoryModel currentCategory = getItem(position);
		View row=convertView;
		if(convertView==null)
		{
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row=inflater.inflate(R.layout.category_row,parent,false);
		}
		final ImageView image = (ImageView) row.findViewById(R.id.categoryimage);
		final TextView categoryName = (TextView) row.findViewById(R.id.categoryname);
		Ion.with(context)
		.load(BASE_URL + getPhotoUrl(currentCategory.id))
		.intoImageView(image);
		categoryName.setText(currentCategory.name);
		return row;
	}

}
