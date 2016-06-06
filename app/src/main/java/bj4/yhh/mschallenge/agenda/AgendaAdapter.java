package bj4.yhh.mschallenge.agenda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import bj4.yhh.mschallenge.Utilities;

/**
 * Created by yenhsunhuang on 2016/6/6.
 */
public class AgendaAdapter extends BaseAdapter {
    private static final boolean DEBUG = Utilities.DEBUG;
    private static final String TAG = "AgendaAdapter";

    public static final int ITEM_VIEW_TYPE_SECTION = 0;
    public static final int ITEM_VIEW_TYPE_EVENT = 1;
    public static final int ITEM_VIEW_TYPE_WEATHER = 2;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final ArrayList<AgendaItem> mItems = new ArrayList<>();

    public AgendaAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private void initData() {

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public AgendaItem getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Section) return ITEM_VIEW_TYPE_SECTION;
        else if (getItem(position) instanceof Event) return ITEM_VIEW_TYPE_EVENT;
        else if (getItem(position) instanceof Weather) return ITEM_VIEW_TYPE_WEATHER;
        else throw new RuntimeException("unexpected item view type: " + getItem(position));
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }
}
