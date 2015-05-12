package ph.com.gs3.formalistics.view.document.contents;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by Ervinne on 4/22/2015.
 */
public class FViewCollection implements List<FView> {

    public static final String TAG = FViewCollection.class.getSimpleName();

    private List<FView> viewList;

    public FViewCollection() {
        viewList = new ArrayList<>();
    }

    public FViewCollection(List<FView> viewList) {
        this.viewList = viewList;
    }

    /**
     * Searches for the field view with the specified id. Note that this will not search
     * for views (ie embedded views).
     *
     * @param fieldId
     * @return The field searched if it is found, otherwise, returns null.
     */
    public FField findFieldView(String fieldId) {
        for (FView viewContent : viewList) {
            if (viewContent instanceof FField) {
                if (((FField) viewContent).getFieldName().equals(fieldId)) {
                    return (FField) viewContent;
                }
            }
        }

        return null;
    }


    @Override
    public boolean add(FView object) {
        return this.viewList.add(object);
    }

    @Override
    public void add(int location, FView object) {
        this.viewList.add(location, object);
    }


    @Override
    public boolean addAll(int location, Collection<? extends FView> collection) {
        return this.viewList.addAll(location, collection);
    }

    @Override
    public boolean addAll(Collection<? extends FView> collection) {
        return this.viewList.addAll(collection);
    }

    @Override
    public void clear() {
        this.viewList.clear();
    }

    @Override
    public boolean contains(Object object) {
        return viewList.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return viewList.containsAll(collection);
    }

    @Override
    public FView get(int location) {
        return viewList.get(location);
    }

    @Override
    public int indexOf(Object object) {
        return viewList.indexOf(object);
    }

    @Override
    public boolean isEmpty() {
        return viewList.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<FView> iterator() {
        return viewList.iterator();
    }

    @Override
    public int lastIndexOf(Object object) {
        return viewList.lastIndexOf(object);
    }

    @NonNull
    @Override
    public ListIterator<FView> listIterator() {
        return viewList.listIterator();
    }

    @NonNull
    @Override
    public ListIterator<FView> listIterator(int location) {
        return viewList.listIterator(location);
    }

    @Override
    public FView remove(int location) {
        return viewList.remove(location);
    }

    @Override
    public boolean remove(Object object) {
        return viewList.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return viewList.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return viewList.retainAll(collection);
    }

    @Override
    public FView set(int location, FView object) {
        return viewList.set(location, object);
    }

    @Override
    public int size() {
        return viewList.size();
    }

    @NonNull
    @Override
    public List<FView> subList(int start, int end) {
        return viewList.subList(start, end);
    }

    @NonNull
    @Override
    public FView[] toArray() {
        FView[] fViews = viewList.toArray(new FView[viewList.size()]);
        return fViews;
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        // Do nothing
        return array;
    }
}
