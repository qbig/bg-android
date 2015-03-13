package sg.com.bigspoon.www.adapters;

import android.content.Context;
import android.database.Cursor;
import android.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sg.com.bigspoon.www.R;
import sg.com.bigspoon.www.data.DishModel;

/**
 * Created by qiaoliang89 on 13/3/15.
 */

public class MenuSearchSuggestionAdapter extends CursorAdapter {

    private DishModel[] dishes;

    public MenuSearchSuggestionAdapter(Context context, Cursor cursor, DishModel[] dishes) {
        super(context, cursor, false);
        this.dishes = dishes;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.itemName.setText(dishes[cursor.getPosition()].name);
        holder.itemDescription.setText(dishes[cursor.getPosition()].description);
        holder.itemPrice.setText(dishes[cursor.getPosition()].price + "");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.search_item, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.itemName = (TextView) view.findViewById(R.id.item_name);
        holder.itemDescription = (TextView) view.findViewById(R.id.item_dec);
        holder.itemPrice = (TextView) view.findViewById(R.id.item_price);

        view.setTag(holder);
        return view;
    }

    public static class ViewHolder {
        public TextView itemName;
        public TextView itemDescription;
        public TextView itemPrice;
    }
}