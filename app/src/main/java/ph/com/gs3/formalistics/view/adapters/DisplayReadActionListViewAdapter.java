package ph.com.gs3.formalistics.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;
import ph.com.gs3.formalistics.model.values.business.view.DisplayReadyAction;

public class DisplayReadActionListViewAdapter extends BaseAdapter {

    public static final String TAG = DisplayReadActionListViewAdapter.class.getSimpleName();

    private Context context;
    private List<DisplayReadyAction> displayReadyActionList;

    public DisplayReadActionListViewAdapter(Context context) {
        this.context = context;
        displayReadyActionList = new ArrayList<>();
    }

    public void setDisplayItems(List<DisplayReadyAction> documentsForDisplay) {
        this.displayReadyActionList.clear();
        this.displayReadyActionList.addAll(documentsForDisplay);
        this.notifyDataSetChanged();

        FLLogger.d(TAG, "documentsForDisplay: " + documentsForDisplay.size());

    }

    @Override
    public int getCount() {
        return displayReadyActionList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayReadyActionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return displayReadyActionList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DocumentListViewItemHolder holder;

        final DisplayReadyAction displayReadyAction = (DisplayReadyAction) getItem(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_document_list_item, parent, false);
            holder = new DocumentListViewItemHolder(row);
            row.setTag(holder);

        }

        holder = (DocumentListViewItemHolder) row.getTag();

        holder.tvHeader.setText(displayReadyAction.getFormName());
        holder.tvBody.setText(getDocumentBody(displayReadyAction));
        holder.tvFooter.setText("Issued Action(s):");
        holder.tvSubFooter.setText(displayReadyAction.getIssuedActions().toString());
        holder.tvDate.setText(displayReadyAction.getDateIssued());

        holder.ibActions.setVisibility(View.GONE);
        holder.ibStar.setVisibility(View.GONE);
        holder.ibComments.setVisibility(View.GONE);

        return row;
    }

    private String getDocumentBody(DisplayReadyAction displayReadyAction) {

        String body = "";

        try {
            JSONObject fieldValues = new JSONObject(displayReadyAction.getDocumentFieldUpdatesString());
            Iterator keys = fieldValues.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                body += key + ": " + fieldValues.getString(key) + "\n";
            }
        } catch (JSONException e) {
            body = displayReadyAction.getTrackingNumber();
        }

        return body;
    }

    private static class DocumentListViewItemHolder {

        ImageView ivAvatar;

        TextView tvHeader;
        TextView tvBody;
        TextView tvFooter;
        TextView tvSubFooter;
        TextView tvDate;

        ImageButton ibActions;
        ImageButton ibStar;
        ImageButton ibComments;

        DocumentListViewItemHolder(View view) {

            ivAvatar = (ImageView) view.findViewById(R.id.DocumentItem_ivAvatar);

            tvHeader = (TextView) view.findViewById(R.id.DocumentItem_tvHeader);
            tvBody = (TextView) view.findViewById(R.id.DocumentItem_tvBody);
            tvFooter = (TextView) view.findViewById(R.id.DocumentItem_tvFooter);
            tvSubFooter = (TextView) view.findViewById(R.id.DocumentItem_tvSubFooter);
            tvDate = (TextView) view.findViewById(R.id.DocumentItem_tvDate);

            ibActions = (ImageButton) view.findViewById(R.id.DocumentItem_ibActions);
            ibStar = (ImageButton) view.findViewById(R.id.DocumentItem_ibStarMark);
            ibComments = (ImageButton) view.findViewById(R.id.DocumentItem_ibComments);

        }

    }

}
