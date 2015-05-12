package ph.com.gs3.formalistics.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.view.ViewColumn;

public class KeyValueGroupListViewAdapter extends BaseAdapter {

	public static final String TAG = KeyValueGroupListViewAdapter.class.getSimpleName();

	public static final int ROW_VIEW_PADDING = 8;

	private final List<JSONObject> data;
	private final List<ViewColumn> viewColumns;

	private final Context context;

	private final LayoutParams picklistItemLayout = new LayoutParams(LayoutParams.MATCH_PARENT,
	        LayoutParams.MATCH_PARENT);

	public KeyValueGroupListViewAdapter(Context context) {
		this.context = context;
		data = new ArrayList<>();
		viewColumns = new ArrayList<>();
	}

	public void setData(List<ViewColumn> viewColumns, List<JSONObject> listData) {

		this.viewColumns.clear();
		this.viewColumns.addAll(viewColumns);

		this.data.clear();
		this.data.addAll(listData);
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		try {
			return data.get(position).getInt("_id");
//			return data.get(position).getInt("document_id");
		} catch (JSONException e) {
			FLLogger.w(TAG, e.getMessage());
			return 0;
		}
	}

	public static class PicklistItemContainerHolder {

		final List<TextView> tvKeyNames = new ArrayList<>();
		final List<TextView> tvValues = new ArrayList<>();
		private final LayoutInflater inflater;

		public PicklistItemContainerHolder(Context context, LinearLayout container,
		        int dataColumnCount) {

			inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			for (int i = 0; i < dataColumnCount; i++) {
				View picklistItemContainer = inflater.inflate(R.layout.view_key_value_strip,
				        container);
				TextView tvKeyName = (TextView) picklistItemContainer
				        .findViewById(R.id.KeyValue_tvKeyName);
				TextView tvValue = (TextView) picklistItemContainer
				        .findViewById(R.id.KeyValue_tvValue);

				tvKeyName.setId(i);
				tvValue.setId(i);

				tvKeyNames.add(tvKeyName);
				tvValues.add(tvValue);
			}

		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View row = convertView;
		PicklistItemContainerHolder holder = null;

		JSONObject data = (JSONObject) getItem(position);

		int keyCount = viewColumns.size();

		if (row == null) {
			LinearLayout container = new LinearLayout(context);
			container.setOrientation(LinearLayout.VERTICAL);
			container.setLayoutParams(picklistItemLayout);
			container.setPadding(ROW_VIEW_PADDING, ROW_VIEW_PADDING, ROW_VIEW_PADDING,
			        ROW_VIEW_PADDING);

			holder = new PicklistItemContainerHolder(context, container, keyCount);
			row = container;
			row.setTag(holder);
		}

		holder = (PicklistItemContainerHolder) row.getTag();

		for (int i = 0; i < keyCount; i++) {
			String keyName = viewColumns.get(i).getLabel();
			String key = viewColumns.get(i).getName();

			holder.tvKeyNames.get(i).setText(keyName);
			if (data.has(key)) {
				try {
					holder.tvValues.get(i).setText(data.getString(key));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return row;
	}
}
