package ph.com.gs3.formalistics.view.adapters;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.application.NavigationDrawerItem;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationDrawerListViewAdapter extends BaseAdapter {

	private Context context;
	private List<NavigationDrawerItem> navigationDrawerItems;

	public NavigationDrawerListViewAdapter(Context context) {
		this.context = context;
		navigationDrawerItems = new ArrayList<>();
	}

	public void setNavigationDrawerItems(List<NavigationDrawerItem> navigationDrawerItems) {
		this.navigationDrawerItems.clear();
		this.navigationDrawerItems.addAll(navigationDrawerItems);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return navigationDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return navigationDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return navigationDrawerItems.get(position).getId();
	}

	private static class NavigationDrawerItemViewHolder {

		ImageView ivIcon;
		TextView tvLabel;

		NavigationDrawerItemViewHolder(View rootView) {

			ivIcon = (ImageView) rootView.findViewById(R.id.NavDrawer_ivIcon);
			tvLabel = (TextView) rootView.findViewById(R.id.NavDrawer_tvLabel);

		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		NavigationDrawerItemViewHolder holder;

		NavigationDrawerItem navigationDrawerItem = (NavigationDrawerItem) getItem(position);

		if (row == null) {

			LayoutInflater inflater = (LayoutInflater) context
			        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.view_navigation_list_item, parent, false);
			holder = new NavigationDrawerItemViewHolder(row);
			row.setTag(holder);

		}

		holder = (NavigationDrawerItemViewHolder) row.getTag();
		holder.tvLabel.setText(navigationDrawerItem.getLabel());
		holder.ivIcon.setImageResource(navigationDrawerItem.getImageResourceId());
		return row;
	}

}
