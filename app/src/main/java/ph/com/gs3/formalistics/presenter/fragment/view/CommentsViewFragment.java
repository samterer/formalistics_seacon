package ph.com.gs3.formalistics.presenter.fragment.view;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.List;

import ph.com.gs3.formalistics.R;
import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.view.adapters.CommentListItemViewAdapter;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentsViewFragment extends Fragment {

    public static final String TAG = CommentsViewFragment.class.getSimpleName();

    private CommentListItemViewAdapter commentListItemViewAdapter;
    private CommentListViewActionListener listener;

    private ListView lvCommentsContainer;
    private EditText etCommentText;
    private Button bSubmitComment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (CommentListViewActionListener) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(activity.getClass().getSimpleName() + " must implement " + CommentsViewFragment.class.getSimpleName());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comments, container, false);

        etCommentText = (EditText) rootView.findViewById(R.id.Comment_etCommentText);

        bSubmitComment = (Button) rootView.findViewById(R.id.Comment_bSubmitComment);
        bSubmitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitNewComment();
            }
        });

        commentListItemViewAdapter = new CommentListItemViewAdapter(getActivity());

        lvCommentsContainer = (ListView) rootView.findViewById(R.id.Comment_lvCommentsContainer);
        lvCommentsContainer.setAdapter(commentListItemViewAdapter);

        getActivity().registerForContextMenu(lvCommentsContainer);

        scrollToBottom();

        listener.onViewReady();

        return rootView;
    }

    public void markCommentAsProcessed(String commentDbId) {

        List<Comment> commentsOnDisplay = commentListItemViewAdapter.getViewData();
        int commentCount = commentsOnDisplay.size();

        // TODO: replace this with search for pending comment only, not the whole comment
        // list
        for (int i = 0; i < commentCount; i++) {
            if (commentDbId.equals(commentsOnDisplay.get(i).getId())) {
                commentsOnDisplay.get(i).setCurrentlyBeingProcessed(false);
                commentsOnDisplay.get(i).setOutgoing(false);
                break;
            }
        }

        commentListItemViewAdapter.notifyDataSetChanged();

    }

    public void setComments(List<Comment> comments) {
        commentListItemViewAdapter.setViewData(comments);
    }

    public void addComment(Comment comment) {
        commentListItemViewAdapter.addComment(comment);
    }

    public void removeComment(Comment comment) {
        commentListItemViewAdapter.removeComment(comment);
    }

    public void scrollToBottom() {
        lvCommentsContainer.setSelection(commentListItemViewAdapter.getCount());
    }

    public void submitNewComment() {

        String commentText = etCommentText.getText().toString();

        if (!commentText.isEmpty()) {
            listener.onSubmitCommentCommand(commentText);
            scrollToBottom();
        }

        etCommentText.setText("");
    }

    public List<Comment> getCommentsOnDisplay() {
        return commentListItemViewAdapter.getViewData();
    }

    public interface CommentListViewActionListener {
        void onViewReady();

        void onSubmitCommentCommand(String commentText);
    }
}
