package sg.com.bigspoon.www.adapters;

import java.util.ArrayList;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.MenuPhotoListActivity;
import sg.com.bigspoon.www.activities.ModifierActivity;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

public class MenuListViewAdapter extends BaseAdapter {

	private Context context;
	private OutletDetailsModel outletInfo;
	public int currentSelectedCategoryTabIndex;
	private static final String ION_LOGGING_MENU_LIST = "ion-menu-list";
	public MenuListViewAdapter(Context context, OutletDetailsModel outletInfo) {
		super();
		this.outletInfo = outletInfo;
		this.context = context;
		Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
	}

	@Override
	public DishModel getItem(int position) {
		int count = 0;
		
		for (int i = 0, len = outletInfo.dishes.length; i < len; i++){
			int categoryIndex = 0;
			for(int j = 0 ; j < outletInfo.categoriesOrder.length; j++){
				if(outletInfo.categoriesOrder[j].categoryId == outletInfo.dishes[i].categories[0].id){
					categoryIndex = outletInfo.categoriesOrder[j].position;
					break;
				}
			}
			if (categoryIndex == currentSelectedCategoryTabIndex){
				if (count == position){
					return outletInfo.dishes[i]; 
				} else {
					count++;
				}
			}
		}
		
		return null; //shit breaks
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		int count = 0;
		for (int i = 0, len = outletInfo.dishes.length; i < len; i++){
			int categoryIndex = 0;
			for(int j = 0 ; j < outletInfo.categoriesOrder.length; j++){
				if(outletInfo.categoriesOrder[j].categoryId == outletInfo.dishes[i].categories[0].id){
					categoryIndex = outletInfo.categoriesOrder[j].position;
					break;
				}
			}
			if (categoryIndex == currentSelectedCategoryTabIndex){
				count++;
			}
		}
		
		return count;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ListItemView listItemView;
		ListTextItemView listTextItemView;
		final DishModel currentDish = getItem(position);
		if (MenuPhotoListActivity.isPhotoMode == true) {
			convertView = LayoutInflater.from(context).inflate(R.layout.menu_photo_item_row, null);

			listItemView = new ListItemView();
			listItemView.imageView = (ImageView) convertView.findViewById(R.id.menuitem);
			listItemView.textItemDesc = (TextView) convertView.findViewById(R.id.itemdesc);
			listItemView.textItemPrice = (TextView) convertView.findViewById(R.id.textitemprice);
			listItemView.textItemName = (TextView) convertView.findViewById(R.id.textitemname);
			listItemView.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);

			listItemView.imageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (position == 0) {
						Intent intent = new Intent(context, ModifierActivity.class);
						intent.putExtra("Item Name", currentDish.name);
						context.startActivity(intent);
					} else {
						MenuPhotoListActivity.totalOrderNumber++;
						View parent = (View) view.getParent().getParent().getParent();
						TextView cornertext;
						cornertext = (TextView) parent.findViewById(R.id.corner);
						cornertext.setVisibility(View.VISIBLE);
						cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
						Animation a = AnimationUtils.loadAnimation(context, R.anim.scale_up);
						cornertext.startAnimation(a);
					}
				}
			});

			Ion.with(context)
			.load(BASE_URL + currentDish.photo.thumbnail)
			.intoImageView(listItemView.imageView);
			
			listItemView.textItemName.setText(currentDish.name);
			listItemView.textItemDesc.setText(currentDish.description);
			listItemView.textItemPrice.setText(currentDish.price + "");

			return convertView;
		} else {
			convertView = LayoutInflater.from(context).inflate(R.layout.menu_text_item_row, null);

			listTextItemView = new ListTextItemView();
			listTextItemView.textitemprice = (TextView) convertView.findViewById(R.id.textitemprice);
			listTextItemView.textitemname = (TextView) convertView.findViewById(R.id.textitemname);
			listTextItemView.textitemdesc = (TextView) convertView.findViewById(R.id.textitemdesc);
			listTextItemView.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);

			listTextItemView.imageButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (position == 0) {
						Intent intent = new Intent(context, ModifierActivity.class);
						intent.putExtra("Item Name", currentDish.name);
						context.startActivity(intent);
					} else {
						MenuPhotoListActivity.totalOrderNumber++;
						View parent = (View) view.getParent().getParent().getParent();
						TextView cornertext;
						cornertext = (TextView) parent.findViewById(R.id.corner);
						cornertext.setVisibility(View.VISIBLE);
						cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
						Animation a = AnimationUtils.loadAnimation(context, R.anim.scale_up);
						cornertext.startAnimation(a);
					}
				}
			});

			listTextItemView.textitemname.setText(currentDish.name);
			listTextItemView.textitemdesc.setText(currentDish.description);
			listTextItemView.textitemprice.setText(currentDish.price + "");

			return convertView;
		}
	}

	class ListItemView {
		ImageView imageView;
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageButton;
	}

	class ListTextItemView {
		TextView textitemprice, textitemname, textitemdesc;
		ImageButton imageButton;
	}
}
