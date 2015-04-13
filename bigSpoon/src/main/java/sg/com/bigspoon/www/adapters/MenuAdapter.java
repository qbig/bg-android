package sg.com.bigspoon.www.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.activities.MenuActivity;
import sg.com.bigspoon.www.activities.ModifierActivity;
import sg.com.bigspoon.www.data.Constants;
import sg.com.bigspoon.www.data.DishModel;
import sg.com.bigspoon.www.data.OutletDetailsModel;
import sg.com.bigspoon.www.data.User;

import static sg.com.bigspoon.www.data.Constants.BASE_URL;
import static sg.com.bigspoon.www.data.Constants.CLEAR_BILL_URL;
import static sg.com.bigspoon.www.data.Constants.LOGIN_INFO_AUTHTOKEN;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_DISH_ID;
import static sg.com.bigspoon.www.data.Constants.MODIFIER_POPUP_REQUEST;
import static sg.com.bigspoon.www.data.Constants.NOTIF_MODIFIER_OK;
import static sg.com.bigspoon.www.data.Constants.PREFS_NAME;

public class MenuAdapter extends BaseAdapter {

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
	private SuperActivityToast mSuperActivityToast;
	
	private BroadcastReceiver mAfterModifierPopupReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (taskAfterModifierPopup != null && mContext !=null){
				((MenuActivity) mContext).runOnUiThread(taskAfterModifierPopup);
			}
		}
	};

    public int getDishPositionInFilteredList(int dishID) {
        for (int i = 0; i < mFilteredDishes.size(); i ++) {
            if (dishID == mFilteredDishes.get(i).id){
                return i;
            }
        }
        return -1;
    }

	public MenuAdapter(Context context, final OutletDetailsModel outletInfo) {
		super();
		this.mOutletInfo = outletInfo;
		this.mContext = context;
		mSuperActivityToast = new SuperActivityToast((Activity)mContext,
                SuperToast.Type.STANDARD);
		mSuperActivityToast.setText("Saved to 'Unsent Order'. Tab 'Orders' to view.");
		mSuperActivityToast.setTextSize(SuperToast.TextSize.LARGE);
		mSuperActivityToast.setAnimations(SuperToast.Animations.POPUP);
		mSuperActivityToast.setDuration(SuperToast.Duration.EXTRA_LONG);
		mSuperActivityToast.setBackground(SuperToast.Background.ORANGE);
        mSuperActivityToast.setOnClickWrapper(
            new OnClickWrapper("superactivitytoast",
                new SuperToast.OnClickListener() {
                    @Override
                    public void onClick(View view, Parcelable token) {
                        mSuperActivityToast.dismiss();
                    }
                }
            )
        );
		mSuperActivityToast.setIcon(SuperToast.Icon.Dark.INFO, SuperToast.IconPosition.LEFT);

		LocalBroadcastManager.getInstance(context).registerReceiver(mAfterModifierPopupReceiver,
				new IntentFilter(NOTIF_MODIFIER_OK));
		this.outOfStockBackground = context.getResources().getDrawable(R.drawable.out_of_stock);
		Ion.getDefault(context).configure().setLogging(ION_LOGGING_MENU_LIST, Log.DEBUG);
		initAddDishButtonListener();
        try {
            updateFilteredList();
        } catch (NullPointerException npe) {
            Crashlytics.log(npe.getMessage());
            ((Activity) mContext).finish();
        }

	}

	public void updateFilteredList() {
        //TODO dup code to clean up (Seen also in MenuActivity)

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
				mSuperActivityToast.dismiss();
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
					((MenuActivity) mContext)
							.startActivityForResult(intentForModifier, MODIFIER_POPUP_REQUEST);
					MenuAdapter.this.taskAfterModifierPopup = new Runnable() {
						@Override
						public void run() {
							try {
                                updateOrderCountAndDisplay(view);
								if (MenuActivity.isPhotoMode) {
									animatePhotoItemToCorner(view, itemPosition, DURATION_LONG);
								} else {
									animateTextItemToCorner(view, itemPosition, DURATION_LONG);
								}
                                if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() <= 3) {
                                    mSuperActivityToast.show();
                                    if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(mContext).currentSession.getPastOrder().getTotalQuantity() != 0){
                                        MenuAdapter.this.showClearOrderPopup();
                                    }
                                }

							} catch (Exception e) {
								Crashlytics.log(e.toString());
							}							
						}
					};
				} else {

					User.getInstance(mContext).currentSession.getCurrentOrder().addDish(currentDish);
                    updateOrderCountAndDisplay(view);

					if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() <= 3) {
						mSuperActivityToast.show();
                        if (User.getInstance(mContext).currentSession.getCurrentOrder().getTotalQuantity() == 1 && User.getInstance(mContext).currentSession.getPastOrder().getTotalQuantity() != 0){
                            MenuAdapter.this.showClearOrderPopup();
                        }
					}

					if (MenuActivity.isPhotoMode) {
						animatePhotoItemToCorner(view, itemPosition, DURATION_SHORT);
					} else {
						animateTextItemToCorner(view, itemPosition, DURATION_SHORT);
					}
				}
			}

            private void updateOrderCountAndDisplay(View viewClicked) {
                final View parent = (View) viewClicked.getParent().getParent().getParent();
                TextView cornertext = (TextView) parent.findViewById(R.id.corner);
                cornertext.setVisibility(View.VISIBLE);
                cornertext.setText(String.valueOf(User.getInstance(mContext).currentSession.getCurrentOrder()
                        .getTotalQuantity()));
                Animation a = AnimationUtils.loadAnimation(mContext, R.anim.scale_up);
                cornertext.startAnimation(a);
            }
        };
	}

    @SuppressWarnings("deprecation")
    private void showClearOrderPopup() {
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setTitle("Just Arrived?");
        alert.setMessage(
                "We found an existing order. If it belongs " +
                "to you, tap continue. Otherwise tap Start new session.");
        alert.setView(null);
        alert.setButton2("Start new session", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                JsonObject tableInfo = new JsonObject();
                tableInfo.addProperty("table", Integer.valueOf(User.getInstance(mContext).tableId));
                User.getInstance(mContext).currentSession.clearPastOrder();
                Ion.with(mContext).load(CLEAR_BILL_URL).setHeader("Content-Type", "application/json; charset=utf-8")
                        .setHeader("Authorization", "Token " + mContext.getSharedPreferences(PREFS_NAME, 0).getString(LOGIN_INFO_AUTHTOKEN, ""))
                        .setJsonObjectBody(tableInfo)
                        .asJsonObject().setCallback(new FutureCallback<JsonObject>() {

                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e != null) {
                            if (Constants.LOG) {
                                Toast.makeText(mContext, "Error clearing orders", Toast.LENGTH_LONG).show();
                            } else {
                                final JSONObject info = new JSONObject();
                                try {
                                    info.put("error", e.toString());
									Crashlytics.logException(e);
                                } catch (JSONException e1) {
                                    Crashlytics.logException(e1);
                                }
                                User.getInstance(mContext).mMixpanel.track("Error clearing orders",
                                        info);
                            }

                            return;
                        }
                        Toast.makeText(mContext, "Cleared", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        alert.setButton("Continue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        });
        alert.show();
        TextView messageView = (TextView) alert.findViewById(android.R.id.message);
        messageView.setGravity(Gravity.CENTER);
        // messageView.setHeight(140);
        messageView.setTextSize(17);
        //TODO refactor naming ...
        Button bq1 = alert.getButton(DialogInterface.BUTTON1);
        bq1.setTextColor(Color.parseColor("#117AFE"));
        bq1.setTypeface(null, Typeface.BOLD);
        bq1.setTextSize(19);
        Button bq2 = alert.getButton(DialogInterface.BUTTON2);
        bq2.setTextColor(Color.parseColor("#117AFE"));
        bq2.setTextSize(19);

    }

	private void animateTextItemToCorner(View view, final Integer itemPosition, long duration) {
		View viewToCopy = (View) view.getParent();

		ImageView viewToAnimate = new ImageView(MenuAdapter.this.mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(viewToCopy.getWidth(),
				viewToCopy.getHeight());
		viewToAnimate.setLayoutParams(params);

		viewToCopy.setDrawingCacheEnabled(true);
		viewToCopy.buildDrawingCache(true);
		Bitmap bm = Bitmap.createBitmap(viewToCopy.getDrawingCache());
		viewToCopy.setDrawingCacheEnabled(false);

		viewToAnimate.setImageBitmap(bm);
		((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
		MenuAdapter.this.moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
	}

	private void animatePhotoItemToCorner(View view, final Integer itemPosition, long duration) {
		ImageView imageViewToCopy = (ImageView) ((View) view.getParent()).findViewById(R.id.menuitem);
		ImageView viewToAnimate = new ImageView(MenuAdapter.this.mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageViewToCopy.getWidth(),
				imageViewToCopy.getHeight());
		viewToAnimate.setLayoutParams(params);
		viewToAnimate.setImageDrawable(imageViewToCopy.getDrawable());
		((ViewGroup) view.getParent().getParent().getParent()).addView(viewToAnimate);
		MenuAdapter.this.moveViewToScreenCorner(itemPosition, viewToAnimate, duration);
	}

	private void moveViewToScreenCorner(int position, final View start, long duration) {
		int fromLoc[] = new int[2];
		start.getLocationOnScreen(fromLoc);
		float startX = fromLoc[0];
		float startY = fromLoc[1];	
		position -= ((MenuActivity) mContext).listview.getFirstVisiblePosition();
		startY += ((position - 1) * (MenuActivity.isPhotoMode ? PHOTO_ITEM_HEIGHT : TEXT_ITEM_HEIGHT));
		
		int toLoc[] = new int[2];
		((MenuActivity) mContext).listview.getLocationOnScreen(toLoc);
		float destX = toLoc[0] + + ((MenuActivity) mContext).listview.getWidth();
		float destY = toLoc[1] + ((MenuActivity) mContext).listview.getHeight();

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
		return MenuActivity.isPhotoMode ? TYPE_PHOTO_ITEM : TYPE_TEXT_ITEM;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final DishModel currentDish = getItem(position);

		if (MenuActivity.isPhotoMode) {
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
				photoViewHolder.imageAddButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(photoViewHolder);

				photoViewHolder.imageAddButton.setOnClickListener(mOrderDishButtonOnClickListener);
				photoViewHolder.imageView.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						photoViewHolder.imageAddButton.performClick();
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
			photoViewHolder.imageAddButton.setTag(position);
			if (currentDish.isDummyDish()) {
				photoViewHolder.imageAddButton.setVisibility(View.GONE);
				photoViewHolder.textItemPrice.setVisibility(View.GONE);
			} else {
				photoViewHolder.imageAddButton.setVisibility(View.VISIBLE);
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
				textViewHolder.imageAddButton = (ImageButton) convertView.findViewById(R.id.addbutton);
				convertView.setTag(textViewHolder);

				textViewHolder.imageAddButton.setOnClickListener(mOrderDishButtonOnClickListener);
			} else {
				textViewHolder = (ListTextItemViewHolder) convertView.getTag();
			}

			textViewHolder.textItemName.setText(currentDish.name);
			textViewHolder.textItemDesc.setText(currentDish.description);
			textViewHolder.textItemPrice.setText(currentDish.price + "");
			textViewHolder.imageAddButton.setTag(position);
			if (currentDish.isDummyDish()) {
				textViewHolder.imageAddButton.setVisibility(View.GONE);
				textViewHolder.textItemPrice.setVisibility(View.GONE);
			} else {
				textViewHolder.imageAddButton.setVisibility(View.VISIBLE);
				textViewHolder.textItemPrice.setVisibility(View.VISIBLE);
			}

			return convertView;
		}
	}

	class ListPhotoItemViewHolder {
		ImageView imageView, overlay;
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageAddButton;
	}

	class ListTextItemViewHolder {
		TextView textItemPrice, textItemName, textItemDesc;
		ImageButton imageAddButton;
	}
}
