package com.sougata.meditrack;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ShapesAdapter extends BaseAdapter {
    private Context context;
    private List<ShapeDropdownItem> shapeList;
    public ShapesAdapter(Context context, List<ShapeDropdownItem> shapeList){
        this.context = context;
        this.shapeList = shapeList;
    }
    @Override
    public int getCount() {
        return shapeList != null ? shapeList.size(): 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.item_shape_add_medicine, parent,false);
        TextView shapeName = rootView.findViewById(R.id.tv_add_shape_item);
        ImageView shapeIcon = rootView.findViewById(R.id.iv_shape_add);
        shapeName.setText(shapeList.get(position).getName());
        shapeIcon.setImageResource(shapeList.get(position).getImage());
        return rootView;
    }
}
