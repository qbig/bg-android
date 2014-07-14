package sg.com.bigspoon.www.activities;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class ExpandableViewUtil {
	public static void setExpandedListViewHeightBasedOnChildren(
			ExpandableListView listView, int groupPosition) {
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}
		View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
				listView);
		listItem.measure(0, 0);
		int appendHeight = 0;
		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			appendHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height += appendHeight;
		listView.setLayoutParams(params);
	}
	
	public static void setCollapseListViewHeightBasedOnChildren(
			ExpandableListView listView, int groupPosition) {
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}
		View listItem = listAdapter.getChildView(groupPosition, 0, true, null,
				listView);
		listItem.measure(0, 0);
		int appendHeight = 0;
		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			appendHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height -= appendHeight;
		listView.setLayoutParams(params);
	}
}
