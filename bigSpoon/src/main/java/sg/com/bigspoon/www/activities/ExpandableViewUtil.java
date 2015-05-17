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
		final View childViewItem = listAdapter.getChildView(groupPosition, 0, true, null,
				listView);
		final View groupViewItem = listAdapter.getGroupView(groupPosition, true, null,
				listView);
		childViewItem.measure(0, 0);
		groupViewItem.measure(0, 0);
		int appendHeight = 0;
		for (int i = 0; i < listAdapter.getChildrenCount(groupPosition); i++) {
			appendHeight += childViewItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if (groupPosition == 0){
			params.height = 0;
		}
		params.height += (groupViewItem.getMeasuredHeight() + appendHeight + 6);
		listView.setLayoutParams(params);
	}
	
	public static void setCollapseListViewHeightBasedOnChildren(
			ExpandableListView listView, int groupPosition) {
		ExpandableListAdapter listAdapter = listView.getExpandableListAdapter();
		if (listAdapter == null) {
			return;
		}

		final View groupViewItem = listAdapter.getGroupView(groupPosition, true, null,
				listView);
		groupViewItem.measure(0, 0);
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		if (groupPosition == 0){
			params.height = 10;
		}
		params.height += (groupViewItem.getMeasuredHeight() + 6);
		listView.setLayoutParams(params);
	}
}
