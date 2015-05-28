package sg.com.bigspoon.www.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.ImageViewFutureBuilder;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.BrandActivity;
import sg.com.bigspoon.www.activities.MenuActivity;
import sg.com.bigspoon.www.data.CategoryModel;
import sg.com.bigspoon.www.data.OutletDetailsModel;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.POS_FOR_CLICKED_CATEGORY;

public class CategoriesAdapter extends ArrayAdapter<CategoryModel> {
	Context context;
	private OutletDetailsModel outletDetails;
	private String outletIcon;
	private Drawable mRestaurantIconDrawable;
    private static final String DEFAULT_DISH_PHOTO_URL = "default.jpg";

	public CategoriesAdapter(Context context, final OutletDetailsModel outletDetails, String outletIcon) {
		super(context, R.layout.category_row, R.id.category_name, outletDetails.categoriesDetails);
		this.context = context;
		this.outletDetails = outletDetails;
		this.outletIcon = outletIcon;
	}

	private String getPhotoUrl(int categoryId) {
		for (int i = 0, len = outletDetails.dishes.length; i < len; i++) {
			if (outletDetails.dishes[i].categories[0].id == categoryId) {
                if (outletDetails.dishes[i].photo.thumbnail.contains(DEFAULT_DISH_PHOTO_URL)) {
                    return "media/" + this.outletDetails.defaultDishPhoto;
                } else {
                    return outletDetails.dishes[i].photo.thumbnail;
                }
			}
		}
		return null;
	}

	@Override
	public int getCount() {
		return outletDetails.categoriesDetails.length + 1;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}
	
	public static Bitmap drawableToBitmap (Drawable drawable) {
	    if (drawable instanceof BitmapDrawable) {
	        return ((BitmapDrawable)drawable).getBitmap();
	    }

	    Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap); 
	    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	    drawable.draw(canvas);

	    return bitmap;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (position == 0) {
			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.category_row_icon, parent, false);
                row.setActivated(false);
			}
			
			final ImageView iconView = (ImageView) row.findViewById(R.id.category_list_restaurant_icon);
			iconView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, MenuActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra(POS_FOR_CLICKED_CATEGORY, 0);
					context.startActivity(i);
				}
			});
            final TextView contactText = (TextView) row.findViewById(R.id.phone_text);
            contactText.setText(outletDetails.phoneNumber);
            final TextView openHoursText = (TextView) row.findViewById(R.id.open_hours);
            openHoursText.setText(outletDetails.operatingHours);
			Ion.with(context).load(BASE_URL + outletIcon).withBitmap().intoImageView(iconView).setCallback(new FutureCallback<ImageView>() {
				
				@Override
				public void onCompleted(Exception arg0, ImageView arg1) {
					if (arg0 == null && arg1 != null && arg1.getDrawable() != null) {
						// hack to copy a drawable
						mRestaurantIconDrawable = new BitmapDrawable(CategoriesAdapter.this.context.getResources(), CategoriesAdapter.this.drawableToBitmap(arg1.getDrawable()));
					}
				}
			});
			final Button brandButton = (Button) row.findViewById(R.id.brand_btn);
			brandButton.setText(outletDetails.name + " Story");
			brandButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent i = new Intent(context, BrandActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra(POS_FOR_CLICKED_CATEGORY, 0);
					context.startActivity(i);
				}
			});
			return row;
		} else {
			final CategoryModel currentCategory = getItem(position - 1);

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.category_row, parent, false);
			}
			
			final ImageView image = (ImageView) row.findViewById(R.id.category_image);
			final TextView categoryName = (TextView) row.findViewById(R.id.category_name);
			//Ion.with(context).load(BASE_URL + getPhotoUrl(currentCategory.id)).intoImageView(image);
			ImageViewFutureBuilder ionBuilder;
			if (mRestaurantIconDrawable != null ){
				ionBuilder = Ion.with(context).load(BASE_URL + getPhotoUrl(currentCategory.id)).withBitmap().placeholder(mRestaurantIconDrawable);
			} else {
				ionBuilder = Ion.with(context).load(BASE_URL + getPhotoUrl(currentCategory.id)).withBitmap();
			}
			
			ionBuilder.intoImageView(image);
			
			categoryName.setText(currentCategory.name);
			return row;
		}		
	}
}
