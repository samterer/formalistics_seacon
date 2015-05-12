package ph.com.gs3.formalistics.model.api.default_impl.parsers.json;

import org.json.JSONException;
import org.json.JSONObject;

import ph.com.gs3.formalistics.model.values.business.Comment;
import ph.com.gs3.formalistics.model.values.business.User;

/**
 * Created by Ervinne on 4/13/2015.
 */
public class CommentJSONParser {

    public static Comment createFromJSON(JSONObject raw) throws JSONException {

        Comment comment = new Comment();

        comment.setWebId(raw.getInt("id"));

        comment.setDocumentWebId(raw.getInt("request_id"));
        comment.setFormWebId(raw.getInt("form_id"));

        comment.setText(raw.getString("text"));
        comment.setDateCreated(raw.getString("date_created"));

        comment.setAuthor(createCommentAuthorFromJSON(raw));

        return comment;

    }

    public static User createCommentAuthorFromJSON(JSONObject raw) throws JSONException {

        User user = new User();

        user.setWebId(raw.getInt("author_id"));
        user.setDisplayName(raw.getString("author_display_name"));
        user.setImageURL(raw.getString("author_image_url"));

        return user;

    }

}
