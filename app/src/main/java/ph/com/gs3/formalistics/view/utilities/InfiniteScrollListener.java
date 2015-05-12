package ph.com.gs3.formalistics.view.utilities;

import android.widget.AbsListView;

import ph.com.gs3.formalistics.global.utilities.logging.FLLogger;

/**
 * Created by Ervinne on 5/11/2015.
 */
public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {

    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;

    public InfiniteScrollListener(int bufferItemCount) {
        this.bufferItemCount = bufferItemCount;
    }

    public abstract void loadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (totalItemCount < itemCount) {
            this.itemCount = totalItemCount;
            if (totalItemCount == 0) {
                FLLogger.d("InfiniteScrollListener.onScroll", " this.itemCount: " + this.itemCount + " - " + totalItemCount);
                this.isLoading = true;
            }
        }

        if (isLoading && (totalItemCount > itemCount)) {
            isLoading = false;
            itemCount = totalItemCount;
            currentPage++;
        }

        FLLogger.d("InfiniteScrollListener.onScroll", firstVisibleItem + " " + visibleItemCount + " " + totalItemCount);
        FLLogger.d("InfiniteScrollListener.onScroll", "isLoading: " + isLoading);

        if (!isLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + bufferItemCount)) {
            loadMore(currentPage + 1, totalItemCount);
            isLoading = true;
        }
    }
}
