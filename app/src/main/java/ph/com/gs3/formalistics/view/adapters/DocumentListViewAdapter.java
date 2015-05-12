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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.constants.StarMark;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.DocumentUtilities;
import ph.com.gs3.formalistics.model.values.business.User;
import ph.com.gs3.formalistics.model.values.business.document.DocumentSummary;
import ph.com.gs3.formalistics.service.managers.ImageManager;

public class DocumentListViewAdapter extends BaseAdapter {

    public static final String TAG = DocumentListViewAdapter.class.getSimpleName();

    private Context context;
    private ImageManager imageManager;

    private User activeUser;
    private List<DocumentSummary> documentSummaryList;
    private DocumentListItemActionListener documentListItemActionListener;

    public DocumentListViewAdapter(Context context, User activeUser) {
        this.context = context;
        this.activeUser = activeUser;
        documentSummaryList = new ArrayList<>();
        imageManager = new ImageManager(context);
    }

    public void setDocumentListItemActionListener(DocumentListItemActionListener documentListItemActionListener) {
        this.documentListItemActionListener = documentListItemActionListener;
    }

    public void setDisplayItems(List<DocumentSummary> documentSummaryList) {
        this.documentSummaryList.clear();
        this.documentSummaryList.addAll(documentSummaryList);
        this.notifyDataSetChanged();
    }

    public void addDisplayItems(List<DocumentSummary> documentSummaryList) {
        this.documentSummaryList.addAll(documentSummaryList);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return documentSummaryList.size();
    }

    @Override
    public Object getItem(int position) {
        return documentSummaryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return documentSummaryList.get(position).getDocumentId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        DocumentListViewItemHolder holder;

        final DocumentSummary documentSummary = (DocumentSummary) getItem(position);

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.view_document_list_item, parent, false);
            holder = new DocumentListViewItemHolder(row);
            row.setTag(holder);
        }

        updateView(row, documentSummary);

        holder = (DocumentListViewItemHolder) row.getTag();

        // TODO:Check if adding individual click listeners is the best implementation
        if (documentSummary.getActions().size() > 0 && DocumentUtilities.isProcessor(documentSummary, activeUser)) {
            holder.ibActions.setVisibility(View.VISIBLE);
            holder.ibActions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    documentListItemActionListener.onOpenDocumentActionsCommand(documentSummary);
                }
            });
        } else {
            holder.ibActions.setVisibility(View.GONE);
        }

        holder.ibStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (documentSummary.getStarMarkInt() == StarMark.STARRED) {
                    v.setBackgroundResource(R.drawable.starred_o_inbox);
                } else {
                    v.setBackgroundResource(R.drawable.starred_inbox);
                }

                documentListItemActionListener.onToggleDocumentStarMarkCommand(documentSummary);
            }
        });

        holder.ibComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                documentListItemActionListener.onOpenDocumentCommentsCommand(documentSummary);
            }
        });

        imageManager.requestUserImage(documentSummary.getAuthorId(), holder.ivAvatar);

        return row;
    }

    public void updateView(View view, final DocumentSummary documentSummary) {
        DocumentListViewItemHolder holder = (DocumentListViewItemHolder) view.getTag();

        holder.tvHeader.setText(documentSummary.getAuthorDisplayName());
        holder.tvBody.setText(getDocumentBody(documentSummary));
        holder.tvFooter.setText(documentSummary.getFormName());
        holder.tvSubFooter.setText(documentSummary.getStatus());
        holder.tvCommentCount.setText(Integer.toString(documentSummary.getCommentCount()));

        if (documentSummary.getDateUpdatedString() != null && !"null".equals(documentSummary.getDateUpdatedString())) {
            try {
                Date dateUpdated = DateUtilities.SERVER_DATE_FORMAT.parse(documentSummary.getDateUpdatedString());
                holder.tvDate.setText(DateUtilities.DEFAULT_DISPLAY_DATE_ONLY_FORMAT.format(dateUpdated));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (documentSummary.getStarMarkInt() == StarMark.STARRED) {
            holder.ibStar.setBackgroundResource(R.drawable.starred_inbox);
        } else {
            holder.ibStar.setBackgroundResource(R.drawable.starred_o_inbox);
        }
    }

    private String getDocumentBody(DocumentSummary documentSummary) {

        JSONObject fieldValues = documentSummary.getFieldValuesJSON();

        String body = "";

        try {
            Iterator keys = fieldValues.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                body += key + ": " + fieldValues.getString(key) + "\n";
            }
        } catch (JSONException e) {
            body = documentSummary.getTrackingNumber();
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
        TextView tvCommentCount;

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
            tvCommentCount = (TextView) view.findViewById(R.id.DocumentItem_tvCommentCount);

            ibActions = (ImageButton) view.findViewById(R.id.DocumentItem_ibActions);
            ibStar = (ImageButton) view.findViewById(R.id.DocumentItem_ibStarMark);
            ibComments = (ImageButton) view.findViewById(R.id.DocumentItem_ibComments);

        }

    }

}
