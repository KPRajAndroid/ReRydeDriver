package com.reryde.provider.Activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.reryde.provider.Model.CityListModel;
import com.reryde.provider.R;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 26/02/18.
 */
public class CountryListAdapter extends BaseAdapter {

    private Context mContext;
    List<CityListModel.City> countries;
    LayoutInflater inflater;

    public CountryListAdapter(Context context, List<CityListModel.City> countries) {
        super();
        this.mContext = context;
        this.countries = countries;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        CityListModel.City country = countries.get(position);

        if (view == null)
            view = inflater.inflate(R.layout.list_item_world_country, null);

        Cell cell = Cell.from(view);
        cell.textView.setText(country.getName());

        return view;
    }

    static class Cell {
        public TextView textView;

        static Cell from(View view) {
            if (view == null)
                return null;

            if (view.getTag() == null) {
                Cell cell = new Cell();
                cell.textView = (TextView) view.findViewById(R.id.country_name);
                view.setTag(cell);
                return cell;
            } else {
                return (Cell) view.getTag();
            }
        }
    }
}
