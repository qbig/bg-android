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

	private Context mContext;
	private OutletDetailsModel outletInfo;
	public int currentSelectedCategoryTabIndex;
	private static final String ION_LOGGING_MENU_LIST = "ion-menu-list";

	private static final int MENU_LIST_VIEW_TYPE_COUNT_IS_2 = 2;
	private static final int TYPE_PHOTO_ITEM = 0;
	private static final int TYPE_TEXT_ITEM = 1;
	private View.OnClickListener addDishButtonOnClickListener;

	public MenuListViewAdapter(Context context, OutletDetailsModel outletInfo) {
		super();
		this.outletInfo = outletInfo;
		this.mContext = context;
		Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
		initAddDishButtonListener();
	}

	private void initAddDishButtonListener() {
		addDishButtonOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Integer itemPosition = (Integer) view.getTag();
				final DishModel currentDish = (DishModel) getItem(itemPosition.intValue());
				if (currentDish.customizable) {
					Intent intent = new Intent(mContext, ModifierActivity.class);
					intent.putExtra("Item Name", currentDish.name);
					mContext.startActivity(intent);
				} else {
					MenuPhotoListActivity.totalOrderNumber++;
					View parent = (View) view.getParent().getParent().getParent();
					TextView cornertext;
					cornertext = (TextView) parent.findViewById(R.id.corner);
					cornertext.setVisibility(View.VISIBLE);
					cornertext.setText(String.valueOf(MenuPhotoListActivity.totalOrderNumber));
					Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
					cornertext.startAnimation(a);
				}
			}
		};
	}

	@Override
	public DishModel getItem(int position) {
		int count = 0;

		for (int i = 0, len = outletInfo.dishes.length; i < len; i++) {
			if (outletInfo.dishes[i].categories[0].id == outletInfo.categoriesDetails[currentSelectedCategoryTabIndex].id) {
				if (count == position) {
					return outletInfo.dishes[i];
				} else {
					count++;
				}
			}
		}

		return null; // shit breaks
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		int count = 0;
		for (int i = 0, len = outletInfo.dishes.length; i < len; i++) {
			if (outletInfo.dishes[i].categories[0].id == outletInfo.categoriesDetails[currentSelectedCategoryTabIndex].id) {
				count++;
			}
		}

		return count;
	}

	@Override
	public int getViewTypeCount() {
		return MENU_LIST_VIEW_TYPE_COUNT_IS_2;
	}

	@Override
	public int getItemViewType(int position) {
		return MenuPhotoListActivity.isPhotoMode ? TYPE_PHOTO_ITEM : TYPE_TEXT_ITEM;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DishModel currentDish = getItem(position);
		
		if ( MenuPhotoListActivity.isPhotoMode ) {
			final ListPhotoItemViewHolder photoViewHolder;
			if (convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_photo_item_row, null);

				photoViewHolder = new ListPhotoItemViewHolder();
				photoViewHolder.imageView = (ImageView) convertView.findViewById(R.id.menuitem);
				photoViewHolder.textItemDesc = (TextView) convertView.findViewById(R.id.itemdesc);
				photoViewHolder.textItemPrice = (TextView) convertView.findViewById(R.id.textitemprice);
				photoViewHolder.textItemName = (TextView) convertView.findViewById(R.id.textitemname);
				photoViewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(photoViewHolder);
				photoViewHolder.imageButton.setOnClickListener(addDishButtonOnClickListener);
			} else {
				photoViewHolder = (ListPhotoItemViewHolder) convertView.getTag();
			}
			
			Ion.with(mContext)
			.load(BASE_URL + currentDish.photo.thumbnail)
			.intoImageView(photoViewHolder.imageView);
			
			photoViewHolder.textItemName.setText(currentDish.name);
			photoViewHolder.textItemDesc.setText(currentDish.description);
			photoViewHolder.textItemPrice.setText(currentDish.price + "");
			photoViewHolder.imageButton.setTag(position);
			return convertView;
		} else {
			final ListTextItemViewHolder textViewHolder;
			if (convertView == null){
				convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_text_item_row, null);

				textViewHolder = new ListTextItemViewHolder();
				textViewHolder.textitemprice = (TextView) convertView.findViewById(R.id.textitemprice);
				textViewHolder.textitemname = (TextView) convertView.findViewById(R.id.textitemname);
				textViewHolder.textitemdesc = (TextView) convertView.findViewById(R.id.textitemdesc);
				textViewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(textViewHolder);
				textViewHolder.imageButton.setOnClickListener(addDishButtonOnClickListener);
			} else {
				textViewHolder = (ListTextItemViewHolder) convertView.getTag();
			}
	
			textViewHolder.textitemname.setText(currentDish.name);
			textViewHolder.textitemdesc.setText(currentDish.description);
			textViewHolder.textitemprice.setText(currentDish.price + "");
			textViewHolder.imageButton.setTag(position);
			return convertView;
		}
	}

	class ListPhotoItemViewHolder {
		ImageView imageView;
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageButton;
	}

	class ListTextItemViewHolder {
		TextView textitemprice, textitemname, textitemdesc;
		ImageButton imageButton;
	}
}
