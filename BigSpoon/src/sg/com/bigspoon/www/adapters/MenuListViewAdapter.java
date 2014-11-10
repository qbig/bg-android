package sg.com.bigspoon.www.adapters;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_KEY;
import static sg.com.bigspoon.www.data.Constants.NOTIF_LOCATION_UPDATED;
import static sg.com.bigspoon.www.data.Constants.NOTIF_MODIFIER_OK;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.MenuPhotoListActivity;
import sg.com.bigspoon.www.activities.ModifierActivity;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.koushikdutta.ion.Ion;

public class MenuListViewAdapter extends BaseAdapter {

	private Context mContext;
	private OutletDetailsModel mOutletInfo;
	public int mCurrentSelectedCategoryTabIndex;
	private View.OnClickListener mOrderDishButtonOnClickListener;
	private ArrayList<DishModel> mFilteredDishes;

	private static final String ION_LOGGING_MENU_LIST = "ion-menu-list";
	private static final String DEFAULT_DISH_PHOTO_URL = "default.jpg";
	private static final int MENU_LIST_VIEW_TYPE_COUNT_IS_2 = 2;
	private static final int TYPE_PHOTO_ITEM = 0;
	private static final int TYPE_TEXT_ITEM = 1;
	private static final float PHOTO_ITEM_HEIGHT = 242;
	private static final float TEXT_ITEM_HEIGHT = 142;
	private static final float CORNER_POS_X = 610;
	private static final float CORNER_POS_Y = 1160;
	private static final long DURATION_LONG = 1000;
	private static final long DURATION_SHORT = 500;
	private Drawable outOfStockBackground;
	private Runnable taskAfterModifierPopup;
	
	private BroadcastReceiver mAfterModifierPopupReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (taskAfterModifierPopup != null && mContext !=null){
				((MenuPhotoListActivity) mContext).runOnUiThread(taskAfterModifierPopup);
			}
		}
	};
	
	public MenuListViewAdapter(Context context, final OutletDetailsModel outletInfo) {
		super();
		this.mOutletInfo = outletInfo;
		this.mContext = context;
		LocalBroadcastManager.getInstance(context).registerReceiver(mAfterModifierPopupReceiver,
				new IntentFilter(NOTIF_MODIFIER_OK));
		this.outOfStockBackground = context.getResources().getDrawable(R.drawable.out_of_stock);
		Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
		initAddDishButtonListener();
		updateFilteredList();
	}

	public void updateFilteredList() {
		mFilteredDishes = new ArrayList<DishModel>();
		if (mOutletInfo.dishes == null || mOutletInfo.dishes.length == 0) {
			return;
		}

		for (int i = 0, len = mOutletInfo.dishes.length; i < len; i++) {
			if (mOutletInfo.dishes[i].categories[0].id == mOutletInfo.categoriesDetails[mCurrentSelectedCategoryTabIndex].id) {
				mFilteredDishes.add(mOutletInfo.dishes[i]);
			}
		}

		Collections.sort(mFilteredDishes, new Comparator<DishModel>() {
			@Override
			public int compare(DishModel lhs, DishModel rhs) {
				return lhs.pos - rhs.pos;
			}
		});
	}

	private void initAddDishButtonListener() {
		mOrderDishButtonOnClickListener = new View.OnClickListener() {
			@Override
			public void onClick(final View view) {

				final Integer itemPosition = (Integer) view.getTag();
				final DishModel currentDish = (DishModel) getItem(itemPosition.intValue());
				if (!currentDish.isServedNow()) {
					AlertDialog alertLocationFail = new AlertDialog.Builder(mContext).create();
					alertLocationFail.setTitle("Sorry");
					alertLocationFail.setMessage("This dish is only available from " + currentDish.startTime + " to "
							+ currentDish.endTime);
					alertLocationFail.setView(null);
					alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alertLocationFail.show();

					return;
				}

				if (currentDish.quantity == 0) {
					AlertDialog alertLocationFail = new AlertDialog.Builder(mContext).create();
					alertLocationFail.setTitle("Sorry");
					alertLocationFail.setMessage("This is out of stock :(");
					alertLocationFail.setView(null);
					alertLocationFail.setButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

						}
					});
					alertLocationFail.show();

					return;
				}

				if (currentDish.customizable) {
					final Intent intentForModifier = new Intent(mContext, ModifierActivity.class);
					intentForModifier.putExtra(MODIFIER_POPUP_DISH_ID, currentDish.id);
					((MenuPhotoListActivity) mContext)
							.startActivityForResult(intentForModifier, MODIFIER_POPUP_REQUEST);
					MenuListViewAdapter.this.taskAfterModifierPopup = new Runnable() {
						@Override
						public void run() {
							try {
								if (MenuPhotoListActivity.isPhotoMode) {
									animatePhotoItemToCorner(view, itemPosition, DURATION_LONG);
								} else {
									animateTextItemToCorner(view, itemPosition, DURATION_LONG);
								}
								if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1) {
									Toast.makeText(mContext, "Order saved to 'Unsent Order'. Tab 'Orders' to view.",
											Toast.LENGTH_LONG).show();
								}
							} catch (Exception e) {
								Crashlytics.log(e.toString());
							}							
						}
					};
				} else {

					User.getInstance(mContext).currentSession.getCurrentOrder().addDish(currentDish);
					final View parent = (View) view.getParent().getParent().getParent();
					TextView cornertext;
					cornertext = (TextView) parent.findViewById(R.id.corner);
					cornertext.setVisibility(View.VISIBLE);
					cornertext.setText(String.valueOf(User.getInstance(mContext).currentSession.getCurrentOrder()
							.getTotalQuantity()));
					Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
					cornertext.startAnimation(a);
					if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1) {
						Toast.makeText(mContext, "Order saved to 'Unsent Order'. Tab 'Orders' to view.",
								Toast.LENGTH_LONG).show();
					}

					if (MenuPhotoListActivity.isPhotoMode) {
						animatePhotoItemToCorner(view, itemPosition, DURATION_SHORT);
					} else {
						animateTextItemToCorner(view, itemPosition, DURATION_SHORT);
					}
				}
			}
		};
	}

	private void animateTextItemToCorner(View view, final Integer itemPosition, long duration) {
		View viewToCopy = (View) view.getParent();

		ImageView viewToAnimate = new ImageView(MenuListViewAdapter.this.mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewToCopy.getWidth(),
				viewToCopy.getHeight());
		viewToAnimate.setLayoutParams(params);

		viewToCopy.setDrawingCacheEnabled(true);
		viewToCopy.buildDrawingCache(true);
		Bitmap bm = Bitmap.createBitmap(viewToCopy.getDrawingCache());
		viewToCopy.setDrawingCacheEnabled(false);

		viewToAnimate.setImageBitmap(bm);
		((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
		MenuListViewAdapter.this.moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
	}

	private void animatePhotoItemToCorner(View view, final Integer itemPosition, long duration) {
		ImageView imageViewToCopy = (ImageView) ((View) view.getParent()).findViewById(R.id.menuitem);
		ImageView viewToAnimate = new ImageView(MenuListViewAdapter.this.mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewToCopy.getWidth(),
				imageViewToCopy.getHeight());
		viewToAnimate.setLayoutParams(params);
		viewToAnimate.setImageDrawable(imageViewToCopy.getDrawable());
		((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
		MenuListViewAdapter.this.moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
	}

	private void moveViewToScreenCorner(int position, final View start, long duration) {
		int fromLoc[] = new int[2];
		start.getLocationOnScreen(fromLoc);
		float startX = fromLoc[0];
		float startY = fromLoc[1];	
		position -= ((MenuPhotoListActivity) mContext).listview.getFirstVisiblePosition();
		startY += ((position - 1) * (MenuPhotoListActivity.isPhotoMode ? PHOTO_ITEM_HEIGHT : TEXT_ITEM_HEIGHT));
		
		int toLoc[] = new int[2];
		((MenuPhotoListActivity) mContext).listview.getLocationOnScreen(toLoc);
		float destX = toLoc[0] + + ((MenuPhotoListActivity) mContext).listview.getWidth();
		float destY = toLoc[1] + ((MenuPhotoListActivity) mContext).listview.getHeight();

		AnimationSet animSet = new AnimationSet(true);
		animSet.setFillAfter(true);
		animSet.setDuration(duration);
		ScaleAnimation scale = new ScaleAnimation(1, 0, 1, 0);
		animSet.addAnimation(scale);

		TranslateAnimation translate = new TranslateAnimation(Animation.ABSOLUTE, startX, Animation.ABSOLUTE, destX,
				Animation.ABSOLUTE, startY, Animation.ABSOLUTE, destY);
		
		animSet.addAnimation(translate);
		animSet.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				start.setVisibility(View.GONE);
			}
		});
		
		start.startAnimation(animSet);
	}

	@Override
	public DishModel getItem(int position) {
		return mFilteredDishes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return mFilteredDishes.size();
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

		if (MenuPhotoListActivity.isPhotoMode) {
			final ListPhotoItemViewHolder photoViewHolder;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_photo_item_row, null);
				photoViewHolder = new ListPhotoItemViewHolder();
				photoViewHolder.imageView = (ImageView) convertView.findViewById(R.id.menuitem);
				photoViewHolder.overlay = (ImageView) convertView.findViewById(R.id.overlay);
				photoViewHolder.textItemDesc = (TextView) convertView.findViewById(R.id.itemdesc);
				photoViewHolder.textItemPrice = (TextView) convertView.findViewById(R.id.textitemprice);
				photoViewHolder.textItemPrice.bringToFront();
				photoViewHolder.textItemName = (TextView) convertView.findViewById(R.id.textitemname);
				photoViewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(photoViewHolder);

				photoViewHolder.imageButton.setOnClickListener(mOrderDishButtonOnClickListener);
				photoViewHolder.imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						photoViewHolder.imageButton.performClick();
					}
				});
			} else {
				photoViewHolder = (ListPhotoItemViewHolder) convertView.getTag();
			}

			if (currentDish.photo.thumbnailLarge.contains(DEFAULT_DISH_PHOTO_URL)) {
				Ion.with(mContext).load(BASE_URL + "media/" + this.mOutletInfo.defaultDishPhoto)
						.intoImageView(photoViewHolder.imageView);
			} else {
				Ion.with(mContext).load(BASE_URL + currentDish.photo.thumbnailLarge)
						.intoImageView(photoViewHolder.imageView);
			}

			if (currentDish.quantity <= 0) {
				photoViewHolder.overlay.setBackgroundDrawable(this.outOfStockBackground);
				photoViewHolder.overlay.setVisibility(View.VISIBLE);
			} else {
				photoViewHolder.overlay.setBackgroundResource(0);
				photoViewHolder.overlay.setVisibility(View.GONE);
			}

			photoViewHolder.textItemName.setText(currentDish.name);
			photoViewHolder.textItemDesc.setText(currentDish.description);
			photoViewHolder.textItemPrice.setText(currentDish.price + "");
			photoViewHolder.imageButton.setTag(position);
			if (currentDish.isDummyDish()) {
				photoViewHolder.imageButton.setVisibility(View.GONE);
				photoViewHolder.textItemPrice.setVisibility(View.GONE);
			} else {
				photoViewHolder.imageButton.setVisibility(View.VISIBLE);
				photoViewHolder.textItemPrice.setVisibility(View.VISIBLE);
			}

			return convertView;
		} else {
			final ListTextItemViewHolder textViewHolder;

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.menu_text_item_row, null);

				textViewHolder = new ListTextItemViewHolder();
				textViewHolder.textItemPrice = (TextView) convertView.findViewById(R.id.textitemprice);
				textViewHolder.textItemName = (TextView) convertView.findViewById(R.id.textitemname);
				textViewHolder.textItemDesc = (TextView) convertView.findViewById(R.id.textitemdesc);
				textViewHolder.imageButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(textViewHolder);

				textViewHolder.imageButton.setOnClickListener(mOrderDishButtonOnClickListener);
			} else {
				textViewHolder = (ListTextItemViewHolder) convertView.getTag();
			}

			textViewHolder.textItemName.setText(currentDish.name);
			textViewHolder.textItemDesc.setText(currentDish.description);
			textViewHolder.textItemPrice.setText(currentDish.price + "");
			textViewHolder.imageButton.setTag(position);
			if (currentDish.isDummyDish()) {
				textViewHolder.imageButton.setVisibility(View.GONE);
				textViewHolder.textItemPrice.setVisibility(View.GONE);
			} else {
				textViewHolder.imageButton.setVisibility(View.VISIBLE);
				textViewHolder.textItemPrice.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}

	class ListPhotoItemViewHolder {
		ImageView imageView, overlay;
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageButton;
	}

	class ListTextItemViewHolder {
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageButton;
	}
}
