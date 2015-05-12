package ph.com.gs3.formalistics.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.global.utilities.DateUtilities;
import ph.com.gs3.formalistics.global.utilities.TextHtmlParser;
import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.service.managers.ImageManager;

public class CommentListItemViewAdapter extends BaseAdapter {

    public static final String TAG = CommentListItemViewAdapter.class.getSimpleName();

    private Context context;

    private List<Comment> comments;
    private ImageManager imageManager;

    public CommentListItemViewAdapter(Context context) {

        this.context = context;
        imageManager = ImageManager.getDefaultInstance(context.getApplicationContext());
        comments = new ArrayList<>();

    }

    public void setViewData(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        notifyDataSetChanged();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        notifyDataSetChanged();
    }

    public List<Comment> getViewData() {
        return comments;
    }

    @Override
    public int getCount() {
        return comments.size();
    }

    @Override
    public Object getItem(int position) {
        return comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static class CommentListItemViewHolder {

        // view objects will have default access so they can only be accessed inside this
        // adapter
        RelativeLayout rlCommentContainer;

        TextView tvAuthorName;
        TextView tvDateCreated;
        TextView tvText;

        ImageView ivAvatar;

        public CommentListItemViewHolder(View view) {

            rlCommentContainer = (RelativeLayout) view.findViewById(R.id.Comment_rlCommentContainer);

            tvAuthorName = (TextView) view.findViewById(R.id.Comment_tvAuthorName);
            tvDateCreated = (TextView) view.findViewById(R.id.Comment_tvDateCreated);
            tvText = (TextView) view.findViewById(R.id.Comment_tvText);

            ivAvatar = (ImageView) view.findViewById(R.id.Comment_ivAvatar);

        }

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        CommentListItemViewHolder holder = null;

        Comment comment = comments.get(position);

        if (row == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = inflater.inflate(R.layout.view_comment_list_item, parent, false);

            holder = new CommentListItemViewHolder(row);
            row.setTag(holder);
        }

        String formattedDate = "";

        Date dateCreated;
        try {
            dateCreated = DateUtilities.parseToServerDate(comment.getDateCreated());
            if (DateUtilities.isDateToday(dateCreated)) {
                formattedDate = DateUtilities.WIDGET_DISPLAY_TIME_ONLY_FORMAT.format(dateCreated);
            } else {
                formattedDate = DateUtilities.WIDGET_DISPLAY_DATE_ONLY_FORMAT.format(dateCreated);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            formattedDate = "";
        }

        holder = (CommentListItemViewHolder) row.getTag();

        holder.tvAuthorName.setText(comment.getAuthor().getDisplayName());
        holder.tvDateCreated.setText(formattedDate);
        holder.tvText.setText(TextHtmlParser.htmlToString(comment.getText()));

        // imageManager.requestUserImage(data.getAuthorWebId(), data.getServer(),
        // holder.ivAvatar);
        imageManager.requestUserImage(comment.getAuthor().getId(), holder.ivAvatar);

        if (comment.isCurrentlyBeingProcessed() || comment.isOutgoing()) {
            holder.rlCommentContainer.setAlpha(0.5f);
        } else {
            holder.rlCommentContainer.setAlpha(1f);
        }

        return row;
    }

}
