package gamsung.traveller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import gamsung.traveller.R;
import gamsung.traveller.activity.HereAppWidget;
import gamsung.traveller.activity.HereAppWidgetSelect;
import gamsung.traveller.util.widgetItem;

/**
 * Created by Hanbin Ju on 2018-03-01.
 */

public class WidgetAdapter extends BaseAdapter{

    private ArrayList<widgetItem> itemArrayList = new ArrayList<widgetItem>();

    @Override
    public int getCount() {
        return itemArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemArrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.widget_select_item,parent,false);
        }

        TextView textView = convertView.findViewById(R.id.tvWidgetSelect);
        textView.setText(itemArrayList.get(pos).getRoute());

        return convertView;
    }

    public void addItems(ArrayList<widgetItem> list){
        itemArrayList.addAll(list);
    }
}
